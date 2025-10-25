package org.jh.forum.common.converter;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 枚举转换器
 * 用于设置 SpringBoot 转换枚举时优先使用 getValue()
 *
 * @author SugarMGP
 */
@Component
@Slf4j
public class EnumConverter implements GenericConverter {

    /**
     * 获取可转换的枚举类型
     * 声明：String -> Enum 任意子类
     */
    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Set.of(new ConvertiblePair(String.class, Enum.class));
    }

    @Override
    public Object convert(Object source, @NotNull TypeDescriptor sourceType, @NotNull TypeDescriptor targetType) {
        if (!(source instanceof String value)) {
            return null;
        }

        Class<?> targetClass = targetType.getType();
        if (!targetClass.isEnum()) {
            return null;
        }

        Object[] enumConstants = targetClass.getEnumConstants();
        if (enumConstants == null || enumConstants.length == 0) {
            return null;
        }

        try {
            // 尝试获取 getValue 方法
            Method getValueMethod = null;
            for (Method method : targetClass.getMethods()) {
                if ("getValue".equals(method.getName()) && method.getParameterCount() == 0) {
                    getValueMethod = method;
                    break;
                }
            }

            if (getValueMethod != null) {
                for (Object constant : enumConstants) {
                    Object enumValue = getValueMethod.invoke(constant);
                    if (value.equals(String.valueOf(enumValue))) {
                        return constant;
                    }
                }
            } else {
                // 没有 getValue 方法，使用 name() 匹配
                for (Object constant : enumConstants) {
                    if (value.equalsIgnoreCase(((Enum<?>) constant).name())) {
                        return constant;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Enum deserialization failed, input value = {}", value, e);
            return null;
        }
        return null;
    }
}
