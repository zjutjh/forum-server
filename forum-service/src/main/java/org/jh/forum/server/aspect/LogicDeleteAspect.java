package org.jh.forum.server.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jh.forum.server.interceptor.LogicDeleteInterceptor;
import org.springframework.stereotype.Component;

/**
 * @author SugarMGP
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
