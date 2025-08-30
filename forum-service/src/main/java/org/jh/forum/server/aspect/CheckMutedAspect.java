package org.jh.forum.server.aspect;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.entity.User;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 检查用户禁言切片
 *
 * @author SugarMGP
 * @see org.jh.forum.common.annotation.CheckMuted
 */
@Aspect
@Component
@RequiredArgsConstructor
public class CheckMutedAspect {

    private final UserMapper userMapper;

    @Before("@annotation(org.jh.forum.common.annotation.CheckMuted)")
    public void checkMuted() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.selectById(userId);
        if (user == null) {
            StpUtil.logout();
            throw new ApiException(ExceptionEnum.NOT_LOGIN);
        }

        LocalDateTime mutedUntil = user.getMutedUntil();
        if (mutedUntil != null && mutedUntil.isAfter(LocalDateTime.now())) {
            throw new ApiException(ExceptionEnum.USER_MUTED);
        }
    }
}

