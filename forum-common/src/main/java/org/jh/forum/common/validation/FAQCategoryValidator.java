package org.jh.forum.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jh.forum.common.constants.FAQCategoryEnum;

/**
 * FAQ分类验证器
 *
 * @author ZeroHzzzz
 */
public class FAQCategoryValidator implements ConstraintValidator<ValidFAQCategory, String> {
    
    @Override
    public void initialize(ValidFAQCategory constraintAnnotation) {
        // 初始化方法，可以为空
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        
        return FAQCategoryEnum.getByDescription(value.trim()) != null;
    }
}
