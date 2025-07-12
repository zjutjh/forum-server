package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author SugarMGP
 */
@Getter
@AllArgsConstructor
public enum PostStatusEnum {
    NORMAL("normal", "正常"),
    PENDING("pending", "待审"),
    DELETED("deleted", "已删");

    @EnumValue
    @JsonValue
    private final String value;

    private final String desc;
}
