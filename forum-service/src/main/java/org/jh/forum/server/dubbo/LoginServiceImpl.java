package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.protobuf.Any;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.LoginReq;
import org.jh.forum.api.dubbo.LoginResp;
import org.jh.forum.api.dubbo.LoginService;
import org.jh.forum.api.dubbo.ServiceResult;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.GenderEnum;
import org.jh.forum.common.constants.UserTypeEnum;
import org.jh.forum.common.entity.User;
import org.jh.forum.common.exceptions.ForumServiceException;
import org.jh.forum.server.manger.UserManager;
import org.jh.forum.server.mapper.UserMapper;
import org.jh.forum.server.utils.EnumUtil;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author MangoGovo
 */
@DubboService(version = "1.0.0")
@Slf4j
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {
    private UserMapper userMapper;
    private UserManager userManager;

    /**
     * 学生登录
     *  TODO 接入用户中心
     *  TODO 处理统一密码修改之后数据库同步问题
     */
    @Override
    public ServiceResult login(LoginReq request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getStudentId, request.getUsername()));
        if (Objects.isNull(user)) {
            // 首次登录, 数据库创建对象
            // 统一登录,下面的字段从统一拿
            user = User.builder()
                    .nickname("default")
                    .realname("default")
                    .studentId(request.getUsername())
                    .password(BCrypt.hashpw(request.getPassword()))
                    .collegeId(1L)
                    .gender(EnumUtil.getEnumByField(GenderEnum.class, GenderEnum::getDesc, "男"))
                    .role(UserTypeEnum.STUDENT).build();
            userMapper.insert(user);
            userManager.insertUserDetail(user.getId());
        } else if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            // 数据库密码校验错误, 再尝试统一登录
            throw new ForumServiceException(ExceptionEnum.WRONG_USERNAME_OR_PASSWORD);
        }
        StpUtil.login(user.getId());
        LoginResp resp = LoginResp.newBuilder()
                .setUserType(user.getRole().getValue())
                .build();
        return ServiceResult.newBuilder()
                .setIsSuccess(true)
                .setData(Any.pack(resp)).build();
    }

    @Override
    public CompletableFuture<ServiceResult> loginAsync(LoginReq request) {
        return CompletableFuture.supplyAsync(() -> login(request));
    }
}
