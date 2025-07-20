package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author SituChengxiang
 */

@AllArgsConstructor
@Getter
public enum AnnouncementTypeEnum {
    SYSTEMATIC("systematic", "系统公告"),
    SCHOLASTIC("scholastic", "学校公告");

    @EnumValue
    @JsonValue
    private final String value;
    private final String desc;
}
