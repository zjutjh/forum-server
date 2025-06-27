package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author SugarMGP
 */
@Getter
@AllArgsConstructor
public enum TargetTypeEnum {
    POST("post", "帖子"),

    COMMENT("comment", "评论"),

    // 仅在 Report 中被使用
    USER("user", "用户");

    @EnumValue
    private final String value;

    private final String desc;
}