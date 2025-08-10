package org.jh.forum.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jh.forum.common.constants.FAQCategoryEnum;

/**
 * FAQ分类验证器（允许null值）
 *
 * @author ZeroHzzzz
 */
public class OptionalFAQCategoryValidator implements ConstraintValidator<ValidOptionalFAQCategory, String> {
    
    @Override
    public void initialize(ValidOptionalFAQCategory constraintAnnotation) {
        // 初始化方法，可以为空
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 允许null值（更新时可选）
        if (value == null) {
            return true;
        }
        
        // 如果不为null，验证是否为有效分类
        if (value.trim().isEmpty()) {
            return false;
        }
        
        return FAQCategoryEnum.getByDescription(value.trim()) != null;
    }
}
