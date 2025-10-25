package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;
import org.jh.forum.api.dubbo.service.LoginService;
import org.jh.forum.common.constants.DeviceTypeEnum;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.GenderEnum;
import org.jh.forum.common.constants.UserTypeEnum;
import org.jh.forum.common.dto.request.AdminRegisterRequest;
import org.jh.forum.common.dto.response.LoginResponse;
import org.jh.forum.common.dto.response.OauthUserInfoElement;
import org.jh.forum.common.entity.User;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.manager.UserManager;
import org.jh.forum.server.mapper.UserMapper;
import org.jh.forum.server.utils.UserCenterUtils;
import org.jh.usercenter.api.LoginRequest;
import org.jh.usercenter.api.Response;
import org.jh.usercenter.api.UserCenterService;

import java.util.Arrays;
import java.util.Objects;

import static org.jh.forum.server.config.service.AdminRegisterSwitchService.adminRegisterSwitch;
import static org.jh.forum.server.config.service.SuperLoginSwitchService.superLoginSwitch;

/**
 * @author MangoGovo
 */
@DubboService
@Slf4j
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {
    private static final String OPERATOR_HEADER = "X-JH-Operator";
    private UserMapper userMapper;
    private UserManager userManager;
    private UserCenterService userCenterService;

    /**
     * 学生登录
     *
     * @return LoginResponse
     */
    @Override
    public LoginResponse userLogin(String username, String password, DeviceTypeEnum deviceType) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getStudentId, username));
        // 处理白名单登陆逻辑
        if (!Objects.isNull(user) && hasSuperPermission()) {
            StpUtil.login(user.getId());
            OauthUserInfoElement userInfo = OauthUserInfoElement.builder()
                    .studentType("本科生")
                    .gender(user.getGender())
                    .name(user.getRealname())
                    .studentId(user.getStudentId())
                    .build();
            return LoginResponse.builder()
                    .userType(UserTypeEnum.STUDENT)
                    .userInfo(userInfo).build();
        }
        // 处理正常登陆逻辑
        OauthUserInfoElement oauthLoginData = oauthLogin(username, password);
        if (Objects.isNull(user)) {
            // 首次登录, 数据库创建对象
            // 统一登录,下面的字段从统一拿
            user = User.builder()
                    .nickname(userManager.generateRandomNickname())
                    .realname(oauthLoginData.getName())
                    .studentId(username)
                    .password(BCrypt.hashpw(password))
                    .collegeId("000000")
                    .gender(oauthLoginData.getGender())
                    .role(UserTypeEnum.STUDENT)
                    .reportCount(0)
                    .resolvedReportCount(0).build();
            userMapper.insert(user);
            userManager.insertUserDetail(user.getId());
        } else {
            // 自动同步密码
            user.setPassword(BCrypt.hashpw(password));
            userMapper.updateById(user);
        }
        StpUtil.login(user.getId(), deviceType.getValue());
        return LoginResponse.builder()
                .userType(UserTypeEnum.STUDENT)
                .userInfo(oauthLoginData).build();
    }

    /**
     * 管理员登陆
     *
     * @return LoginResponse
     */
    @Override
    public LoginResponse adminLogin(String username, String password) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getStudentId, username));
        if (Objects.isNull(user)) {
            throw new ApiException(ExceptionEnum.WRONG_USERNAME_OR_PASSWORD);
        }
        UserTypeEnum userType = user.getRole();
        Boolean isAdmin = userType.equals(UserTypeEnum.ADMIN) || userType.equals(UserTypeEnum.SUPER_ADMIN);
        // 处理白名单登陆逻辑
        if (hasSuperPermission() && isAdmin) {
            StpUtil.login(user.getId());
            return LoginResponse.builder().userType(user.getRole()).build();
        }

        // 处理正常登陆逻辑
        if (!BCrypt.checkpw(password, user.getPassword())) {
            // 数据库密码校验错误
            throw new ApiException(ExceptionEnum.WRONG_USERNAME_OR_PASSWORD);
        }
        if (!isAdmin) {
            // 权限错误
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }
        StpUtil.login(user.getId());
        return LoginResponse.builder().userType(user.getRole()).build();
    }

    private Boolean hasSuperPermission() {
        String operatorId = RpcContext.getClientAttachment().getAttachment(OPERATOR_HEADER);
        if (Objects.isNull(operatorId) || Objects.isNull(superLoginSwitch)) {
            return false;
        }
        if (!superLoginSwitch.getEnabled()) {
            log.info("{}不具有白名单权限", operatorId);
            return false;
        }
        boolean hasPermission = Arrays.stream(superLoginSwitch.getWhiteList())
                .toList()
                .contains(NumberUtils.toLong(operatorId));
        if (hasPermission) {
            log.info("{}具有白名单权限", operatorId);
            return true;
        }
        return false;
    }


    private OauthUserInfoElement oauthLogin(String username, String password) {
        LoginRequest loginRequest = LoginRequest.newBuilder()
                .setStudentId(username)
                .setPassword(password)
                .build();
        Response resp = userCenterService.oauthLogin(loginRequest);
        Integer code = resp.getCode();
        ExceptionEnum exceptionEnum = UserCenterUtils.toForumException(code);
        if (exceptionEnum != null) {
            throw new ApiException(exceptionEnum);
        }

        Value data = resp.getData();
        if (data.getKindCase() != Value.KindCase.STRUCT_VALUE) {
            log.error("用户中心请求结果类型异常, data = {}", data);
            throw new ApiException(ExceptionEnum.SERVER_ERROR);
        }
        Struct dataStruct = data.getStructValue();
        return OauthUserInfoElement.builder()
                .studentId(dataStruct.getFieldsOrThrow("studentId").getStringValue())
                .name(dataStruct.getFieldsOrThrow("name").getStringValue())
                .gender(UserCenterUtils.toGenderEnum(dataStruct.getFieldsOrThrow("gender").getStringValue()))
                .studentType(dataStruct.getFieldsOrThrow("userTypeDesc").getStringValue())
                .build();
    }

    @Override
    public void adminRegister(AdminRegisterRequest request) {
        if (Objects.isNull(adminRegisterSwitch)) {
            log.info("未获取到 AdminRegister 配置");
            throw new ApiException(ExceptionEnum.NOT_FOUND_ERROR);
        }
        String secretKey = adminRegisterSwitch.getKey();
        Boolean adminRegisterEnabled = adminRegisterSwitch.getEnabled();
        // 接口是否下线
        if (!adminRegisterEnabled) {
            log.info("AdminRegister 接口已下线");
            throw new ApiException(ExceptionEnum.NOT_FOUND_ERROR);
        }
        // 校验key
        if (StringUtils.isBlank(request.getKey()) || !request.getKey().equals(secretKey)) {
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getStudentId, request.getUsername()));
        if (user != null) {
            user.setPassword(BCrypt.hashpw(request.getPassword()));
            user.setRole(request.getUserType());
            userMapper.updateById(user);
            return;
        }
        user = User.builder()
                .nickname(userManager.generateRandomNickname())
                .realname("测试账号")
                .studentId(request.getUsername())
                .password(BCrypt.hashpw(request.getPassword()))
                .collegeId("000000")
                .gender(GenderEnum.UNKNOWN)
                .role(request.getUserType())
                .reportCount(0)
                .resolvedReportCount(0).build();
        userMapper.insert(user);
        userManager.insertUserDetail(user.getId());
    }
}
