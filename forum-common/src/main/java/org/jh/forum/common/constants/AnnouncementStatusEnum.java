package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公告状态枚举
 *
 * @author SituChengxiang
 */
@AllArgsConstructor
@Getter
public enum AnnouncementStatusEnum {
    DRAFT("draft", "草稿"),
    PUBLISHED("published", "立即发布"),
    SCHEDULED("scheduled", "定时发布");

    @EnumValue
    @JsonValue
    private final String value;
    private final String desc;
}
