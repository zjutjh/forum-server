package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对象类型枚举
 *
 * @author SugarMGP
 */
@Getter
@AllArgsConstructor
public enum TargetTypeEnum {
    POST("post", "帖子"),
    COMMENT("comment", "评论"),
    USER("user", "用户"),
    REPORT("report", "举报");

    @EnumValue
    @JsonValue
    private final String value;

    private final String desc;
}