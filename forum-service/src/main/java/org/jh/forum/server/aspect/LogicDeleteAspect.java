package org.jh.forum.server.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jh.forum.server.interceptor.LogicDeleteInterceptor;
import org.springframework.stereotype.Component;

/**
 * 忽略逻辑删除切片
 *
 * @author SugarMGP
 * @see org.jh.forum.common.annotation.IgnoreLogicDelete
 */
@Aspect
@Component
public class LogicDeleteAspect {
    @Around("@annotation(org.jh.forum.common.annotation.IgnoreLogicDelete)")
    public Object ignoreLogicDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        LogicDeleteInterceptor.ignore();
        try {
            return joinPoint.proceed();
        } finally {
            LogicDeleteInterceptor.clear();
        }
    }
}
