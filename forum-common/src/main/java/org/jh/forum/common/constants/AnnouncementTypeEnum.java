package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公告类型枚举
 *
 * @author SituChengxiang
 */
@AllArgsConstructor
@Getter
public enum AnnouncementTypeEnum {
    SCHOLASTIC("scholastic", "学校公告"),
    SYSTEMATIC("systematic", "系统公告/系统通知");

    @EnumValue
    @JsonValue
    private final String value;
    private final String desc;
}
