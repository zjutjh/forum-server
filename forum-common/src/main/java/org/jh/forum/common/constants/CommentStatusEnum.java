package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评论状态枚举
 *
 * @author qianqianzyk
 */
@Getter
@AllArgsConstructor
public enum CommentStatusEnum {
    ALL("all", "全部"),
    DELETED("deleted", "已删"),
    NORMAL("normal", "未删");

    @EnumValue
    @JsonValue
    private final String value;

    private final String desc;
}
