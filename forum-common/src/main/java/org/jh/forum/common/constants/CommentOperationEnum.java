package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author qianqianzyk
 */
@Getter
@AllArgsConstructor
public enum CommentOperationEnum {
    DELETE("delete", "删除"),
    RESTORE("restore", "恢复");

    @EnumValue
    @JsonValue
    private final String value;

    private final String desc;
}