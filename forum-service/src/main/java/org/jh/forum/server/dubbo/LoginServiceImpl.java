package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.common.dto.response.LoginResponse;
import org.jh.forum.common.dto.response.OauthUserInfoElement;
import org.jh.forum.server.utils.UserCenterUtils;
import org.jh.usercenter.api.LoginRequest;
import org.jh.usercenter.api.Response;
import org.jh.usercenter.api.UserCenterService;
import org.jh.forum.api.dubbo.service.LoginService;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.UserTypeEnum;
import org.jh.forum.common.entity.User;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.manager.UserManager;
import org.jh.forum.server.mapper.UserMapper;

import java.util.Objects;

/**
 * @author MangoGovo
 */
@DubboService
@Slf4j
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {
    private UserMapper userMapper;
    private UserManager userManager;
    private UserCenterService userCenterService;

    /**
     * 学生登录
     *
     * @return LoginResponse
     */
    @Override
    public LoginResponse login(String username, String password, UserTypeEnum loginType) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getStudentId, username));
        if (loginType == UserTypeEnum.STUDENT) {
//          学生登陆逻辑
            OauthUserInfoElement oauthLoginData = oauthLogin(username, password);
            if (Objects.isNull(user)) {
                // 首次登录, 数据库创建对象
                // 统一登录,下面的字段从统一拿
                user = User.builder()
                        .nickname("精小弘")
                        .realname(oauthLoginData.getName())
                        .studentId(username)
                        .password(BCrypt.hashpw(password))
                        .college("")
                        .gender(oauthLoginData.getGender())
                        .role(loginType)
                        .reportCount(0)
                        .resolvedReportCount(0).build();
                userMapper.insert(user);
                userManager.insertUserDetail(user.getId());
            }
            StpUtil.login(user.getId());
            return LoginResponse.builder()
                    .userType(UserTypeEnum.STUDENT)
                    .userInfo(oauthLoginData).build();
        }
        // 管理员登陆逻辑
        if (Objects.isNull(user)) {
            throw new ApiException(ExceptionEnum.WRONG_USERNAME_OR_PASSWORD);
        }
        if (!BCrypt.checkpw(password, user.getPassword()) || !user.getRole().equals(loginType)) {
            // 数据库密码校验错误
            throw new ApiException(ExceptionEnum.WRONG_USERNAME_OR_PASSWORD);
        }
        return LoginResponse.builder().userType(user.getRole()).build();
    }

    private OauthUserInfoElement oauthLogin(String username, String password) {
        try {
            LoginRequest loginRequest = LoginRequest.newBuilder()
                    .setStudentId(username)
                    .setPassword(password)
                    .build();
            Response resp = userCenterService.oauthLogin(loginRequest);
//            错误处理
            Integer code = resp.getCode();
            ExceptionEnum exceptionEnum = UserCenterUtils.toForumException(code);
            if (exceptionEnum != null) {
                throw new ApiException(exceptionEnum);
            }

            Value data = resp.getData();
            if (data.getKindCase() != Value.KindCase.STRUCT_VALUE) {
                log.error("用户中心请求结果类型异常");
                throw new ApiException(ExceptionEnum.SERVER_ERROR);
            }
            Struct dataStruct = data.getStructValue();
            return OauthUserInfoElement.builder()
                    .studentId(dataStruct.getFieldsOrThrow("studentId").getStringValue())
                    .name(dataStruct.getFieldsOrThrow("name").getStringValue())
                    .gender(UserCenterUtils.toGenderEnum(dataStruct.getFieldsOrThrow("gender").getStringValue()))
                    .studentType(dataStruct.getFieldsOrThrow("userTypeDesc").getStringValue())
                    .build();
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(ExceptionEnum.SERVER_ERROR);
        }
    }
}
