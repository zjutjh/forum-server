package org.jh.forum.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * FAQ分类验证注解
 *
 * @author ZeroHzzzz
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FAQCategoryValidator.class)
@Documented
public @interface ValidFAQCategory {
    
    String message() default "无效的FAQ分类";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
