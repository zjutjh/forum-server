package org.jh.forum.api.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 基础注解，用于 Dubbo Filter 获取 uid
 * @author Patrick_Star
 * @version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UidRef {
    String value() default "";
}
