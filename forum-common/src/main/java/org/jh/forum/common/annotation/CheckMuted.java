package org.jh.forum.common.annotation;

import java.lang.annotation.*;

/**
 * 检查用户是否被禁言，若被禁言则抛出业务异常
 *
 * @author SugarMGP
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckMuted {
}
