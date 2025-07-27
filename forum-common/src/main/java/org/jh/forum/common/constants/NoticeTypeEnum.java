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
public enum NoticeTypeEnum {
    LIKE("like", "点赞"),
    COLLECT("collect", "收藏"),
    COMMENT("comment", "评论"),
    AT("at", "@提及");

    @EnumValue
    @JsonValue
    private final String value;

    private final String desc;
}
