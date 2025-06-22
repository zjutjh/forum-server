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
import org.jh.forum.common.constants.UserTypeEnum;
import org.jh.forum.common.entity.User;
import org.jh.forum.common.exceptions.ForumServiceException;
import org.jh.forum.server.mapper.UserMapper;

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

    @Override
    public ServiceResult login(LoginReq request) {
        // 学生登录
        // TODO 接入用户中心
        // TODO 处理统一密码修改之后数据库同步问题
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getStudentId, request.getUsername()));
        if (Objects.isNull(user)) {
            // 首次登录, 数据库创建对象
            // 统一登录,下面的字段从统一拿
            User newUser = User.builder()
                    .nickname("default")
                    .realname("default")
                    .studentId(request.getUsername())
                    .password(BCrypt.hashpw(request.getPassword()))
                    .collegeId(1)
                    .gender("男")
                    .role(UserTypeEnum.STUDENT.getValue()).build();
            userMapper.insert(newUser);
            Long userId = newUser.getId();
            StpUtil.login(userId);
            LoginResp resp = LoginResp.newBuilder()
                    .setUserType(newUser.getRole())
                    .build();
            return ServiceResult.newBuilder()
                    .setIsSuccess(true)
                    .setData(Any.pack(resp)).build();
        }

        // 非首次登录, 直接对比数据库
        if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            LoginResp resp = LoginResp.newBuilder()
                    .setUserType(user.getRole())
                    .build();
            StpUtil.login(user.getId());
            return ServiceResult.newBuilder()
                    .setIsSuccess(true)
                    .setData(Any.pack(resp)).build();
        }
        throw new ForumServiceException(ExceptionEnum.WRONG_USERNAME_OR_PASSWORD);
        // 数据库密码校验错误, 再尝试统一登录
        // TODO 统一登录

    }

    @Override
    public CompletableFuture<ServiceResult> loginAsync(LoginReq request) {
        return null;
    }
}
