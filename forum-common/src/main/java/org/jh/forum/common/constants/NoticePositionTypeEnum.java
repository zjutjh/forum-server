package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lyyzzz6
 */
@Getter
@AllArgsConstructor
public enum NoticePositionTypeEnum {
    POST("post", "帖子"),
    COMMENT("comment", "评论"),
    REPLY("reply", "回复");

    @EnumValue
    @JsonValue
    private final String value;

    private final String desc;
}
