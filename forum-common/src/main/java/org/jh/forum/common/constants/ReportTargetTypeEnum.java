package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zzb
 */
@Getter
@AllArgsConstructor
public enum ReportTargetTypeEnum {
    POST("post", "帖子"),
    COMMENT("comment", "评论"),
    USER("user", "用户");


    @EnumValue
    @JsonValue
    private final String value;

    private final String desc;

}
