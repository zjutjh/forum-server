package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoticeTypeEnum {
    UNKNOWN(0, "未知类型"),
    LIKE(1, "点赞"),
    COLLECT(2, "收藏"),
    COMMENT(3, "评论"),
    AT(4, "@提及");

    @EnumValue
    @JsonValue
    private final Integer value;

    private final String desc;

    public static NoticeTypeEnum getByCode(Integer code) {
        for (NoticeTypeEnum type : values()) {
            if (type.value.equals(code)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
