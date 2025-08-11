package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FAQ分类枚举
 *
 * @author ZeroHzzzz
 */
@Getter
@AllArgsConstructor
public enum FAQCategoryEnum {
    ACCOUNT("account", "账号问题"),
    COLLEGE("college", "学院问题"),
    POST("post", "帖子问题"),
    OTHER("other", "其他问题"),
    GUESS("guess", "猜你想问");

    @JsonValue
    @EnumValue
    private final String value;

    private final String desc;
}
