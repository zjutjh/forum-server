package org.jh.forum.server.aspect;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jh.forum.common.annotation.WithLock;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.exceptions.ForumServiceException;
import org.jh.forum.server.config.service.ForumSwitchService;
import org.jh.forum.server.utils.RedisUtil;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * Redis 加锁切片
 * @author Patrick_Star
 * @date 2025/4/19
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
@Slf4j
public class RedisLockAspect {

    @Resource
    private RedisUtil redisUtil;

    @Around("@annotation(org.jh.forum.common.annotation.WithLock)")
    public Object lock(ProceedingJoinPoint point) throws Throwable {
        String lockKey = getLockKey(point);
        boolean getLock = false;
        try {
            if (StringUtils.isEmpty(lockKey)) {
                log.error("[RedisLockAspect] cannot get lock key for point: {}", point);
                throw new ForumServiceException(ExceptionEnum.INVALID_PARAMETER.getErrorCode(),
                        ExceptionEnum.INVALID_PARAMETER.getErrorMsg());
            }
            int lockCount = 0;
            // 防止未来变更导致无穷循环，禁止 while(true) 语法
            int iter = 0;
            while (iter < 10) {
                iter ++;
                getLock = redisUtil.tryLockWithReentrant(lockKey);
                if (getLock) {
                    log.info("[RedisLockAspect] get lock success, lockKey: {}", lockKey);
                    break;
                }
                lockCount ++;
                int weight = iter + 1;
                Thread.sleep(weight * ForumSwitchService.forumSwitch.redisLockRetryInterval);
                if (lockCount > ForumSwitchService.forumSwitch.redisLockMaxRetryCount) {
                    throw new ForumServiceException(ExceptionEnum.EXCEED_MAX_GET_LOCK_COUNT.getErrorCode(),
                            ExceptionEnum.EXCEED_MAX_GET_LOCK_COUNT.getErrorMsg());
                }
            }
            return point.proceed();
        } finally {
            if (getLock) {
                log.info("[RedisLockAspect] release lock, lockKey: {}", lockKey);
                redisUtil.releaseLockWithReentrant(lockKey);
            }
        }
    }

    private String getLockKey(ProceedingJoinPoint point) {
        StringBuilder sb = new StringBuilder();
        MethodSignature signature = (MethodSignature) point.getSignature();
        String[] params = signature.getParameterNames();
        Object[] values = point.getArgs();
        if (Objects.nonNull(signature.getMethod().getAnnotation(WithLock.class))) {
            WithLock annotation = signature.getMethod().getAnnotation(WithLock.class);
            if (StringUtils.isNotEmpty(annotation.prefix())) {
                sb.append(annotation.prefix());
            }
            for (String param : annotation.params()) {
                for (int i = 0; i < params.length; i++) {
                    if (params[i].equals(param)) {
                        if (!sb.isEmpty()) {
                            sb.append("-");
                        }
                        sb.append(values[i].toString());
                    }
                }
            }
            if (!sb.isEmpty()) {
                return ForumSwitchService.forumSwitch.redisLockGlobalPrefix + "-" + sb.toString();
            }
        }
        return "";
    }
}
