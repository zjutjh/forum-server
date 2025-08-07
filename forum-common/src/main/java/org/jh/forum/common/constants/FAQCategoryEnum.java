package org.jh.forum.common.constants;

import lombok.Getter;

/**
 * FAQ分类枚举
 *
 * @author ZeroHzzzz
 */
@Getter
public enum FAQCategoryEnum {
    ACCOUNT("账号问题"),
    COLLEGE("学院问题"),
    POST("帖子问题"),
    GUESS("猜你想问");

    private final String description;

    FAQCategoryEnum(String description) {
        this.description = description;
    }

    /**
     * 根据描述获取枚举
     */
    public static FAQCategoryEnum getByDescription(String description) {
        for (FAQCategoryEnum category : values()) {
            if (category.description.equals(description)) {
                return category;
            }
        }
        return null;
    }

    /**
     * 获取所有分类描述
     */
    public static String[] getAllDescriptions() {
        FAQCategoryEnum[] values = values();
        String[] descriptions = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            descriptions[i] = values[i].description;
        }
        return descriptions;
    }
}
