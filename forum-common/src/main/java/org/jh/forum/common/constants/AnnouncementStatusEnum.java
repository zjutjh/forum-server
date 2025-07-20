package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author SituChengxiang
 */
@AllArgsConstructor
@Getter
public enum AnnouncementStatusEnum {
    /**
     * 公告状态枚举
     * 状态说明：
     * - DRAFT: 草稿
     * - PUBLISHED: 已发布（注意，现行代码会使过期一小时以上的定时发布的status转为已发布，此调整仅影响管理员视图）
     * - SCHEDULED: 定时发布
     */
    DRAFT("draft", "草稿"),
    PUBLISHED("published", "已发布"),
    SCHEDULED("scheduled", "待发布");

    @EnumValue
    private final String code;
    private final String description;

    public static AnnouncementStatusEnum fromCode(String code) {
        for (AnnouncementStatusEnum status : AnnouncementStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("公告状态异常: " + code);
    }
}
