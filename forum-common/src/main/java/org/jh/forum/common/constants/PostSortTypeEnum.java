package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 帖子排序类型枚举
 *
 * @author SugarMGP
 */
@Getter
@AllArgsConstructor
public enum PostSortTypeEnum {
    HOT("hot", "最热"),
    NEW("new", "最新");

    @EnumValue
    @JsonValue
    private final String value;

    private final String desc;
}
