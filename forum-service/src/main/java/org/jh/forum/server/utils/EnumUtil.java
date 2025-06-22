package org.jh.forum.server.utils;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author SugarMGP
 */
public class EnumUtil {
    /**
     * 根据字段值获取对应的枚举对象
     *
     * @param enumClass      枚举类型
     * @param fieldExtractor 从枚举中提取字段的函数（如 GenderEnum::getDesc）
     * @param value          要匹配的字段值
     * @param <T>            枚举类型
     * @param <R>            字段类型（如 String、Integer 等）
     * @return 匹配到的枚举对象，没有找到返回 null
     */
    public static <T extends Enum<T>, R> T getEnumByField(Class<T> enumClass,
                                                          Function<T, R> fieldExtractor,
                                                          R value) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> Objects.equals(fieldExtractor.apply(e), value))
                .findFirst()
                .orElse(null);
    }
}