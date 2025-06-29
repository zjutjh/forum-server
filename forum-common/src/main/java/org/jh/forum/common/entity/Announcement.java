package org.jh.forum.common.entity;

import java.time.ZonedDateTime;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 公告实体类（MyBatis-Plus Entity）
 *
 * @author SituChengxiang
 */
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = false) // 解决继承类的equals/hashCode问题
@NoArgsConstructor(force = true)
@AllArgsConstructor
@TableName("announcement") // MyBatis-Plus表名映射

public class Announcement extends BaseEntity {

    /**
     * 公告标题 - 必填，最大50个字符
     */
    @TableField("title")
    private String title;

    /**
     * 公告内容 - 必填，最大500个字符
     */
    @TableField("content")
    private String content;

    /**
     * 公告类型 - 必填：系统公告/学校公告
     */
    @TableField("type")
    private Integer type;

    /**
     * 预定发布时间 - 可空
     */
    @TableField("scheduled_at")
    private ZonedDateTime scheduledAt;

    /**
     * 状态 - 必填：0:草稿、1:已发布、2:待发布
     */
    @TableField("status")
    private AnnouncementStatus status;

    /**
     * 是否置顶 - 默认false
     */
    @TableField("sticky")
    private Boolean sticky;

    /**
     * 公告状态枚举
     * <p>
     * 状态说明：
     * - DRAFT(0): 草稿
     * - PUBLISHED(1): 已发布
     * - SCHEDULED(2): 待发布
     */
    @AllArgsConstructor
    @Getter
    public enum AnnouncementStatus {
        DRAFT(0, "草稿"),
        PUBLISHED(1, "已发布"),
        SCHEDULED(2, "待发布");

        @EnumValue
        private final int code;
        private final String description;

        /**
         * 根据状态码获取对应的枚举值
         *
         * @param code 状态码
         * @return 对应的枚举值
         */
        public static AnnouncementStatus fromCode(int code) {
            for (AnnouncementStatus status : values()) {
                if (status.getCode() == code) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid status code: " + code);
        }

    }
}