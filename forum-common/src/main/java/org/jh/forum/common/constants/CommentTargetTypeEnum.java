package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评论对象类型枚举
 *
 * @author SugarMGP
 */
@Getter
@AllArgsConstructor
public enum CommentTargetTypeEnum {
    POST("post", "帖子"),
    COMMENT("comment", "评论");

    @EnumValue
    @JsonValue
    private final String value;

    private final String desc;
}
