package org.jh.forum.common.annotation;

import java.lang.annotation.*;

/**
 * @author SugarMGP
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckMuted {
}
