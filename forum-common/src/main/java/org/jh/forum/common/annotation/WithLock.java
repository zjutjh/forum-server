package org.jh.forum.common.annotation;

import java.lang.annotation.*;

/**
 * 加锁注解，请确保加本注解的方法是 public 的，且是从类的外部进行调用，并保证 params 的参数均出现在函数入参中 且 toString 可以正常返回
 * 在切片层面实现了锁的获取、回旋和释放
 * @author Patrick_Star
 * @date 2025/4/19
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WithLock {

    // 请确保 params 的参数均出现在函数入参中 且 toString 可以正常返回
    String[] params() default {};

    // 前缀
    String prefix() default "";
}
