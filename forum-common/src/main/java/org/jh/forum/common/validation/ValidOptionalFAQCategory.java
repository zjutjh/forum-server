package org.jh.forum.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * FAQ分类验证注解（允许null值）
 *
 * @author ZeroHzzzz
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OptionalFAQCategoryValidator.class)
@Documented
public @interface ValidOptionalFAQCategory {
    
    String message() default "无效的FAQ分类";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
