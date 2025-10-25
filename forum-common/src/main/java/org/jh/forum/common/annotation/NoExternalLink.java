package org.jh.forum.common.annotation;

import org.jh.forum.common.validator.NoExternalLinkValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * 禁止外链注解，在请求入参中使用
 *
 * @author SugarMGP
 * @see NoExternalLinkValidator
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoExternalLinkValidator.class)
@Documented
public @interface NoExternalLink {
    String message() default "must not contain external links";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
