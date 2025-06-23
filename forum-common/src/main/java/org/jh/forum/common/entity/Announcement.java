package org.jh.forum.common.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 公告实体类（MyBatis-Plus Entity）
 *
 * @author SituChengxiang
 * @TableName announcement
 */
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = false) // 解决继承类的equals/hashCode问题
@NoArgsConstructor(force = true)
@AllArgsConstructor
@TableName("announcement") // MyBatis-Plus表名映射
public class Announcement extends BaseEntity {

    /**
     * 公告标题 - 必填，最大50字符
     */
    @TableField("title")
    private String title;

    /**
     * 公告内容 - 必填，最大500字符
     */
    @TableField("content")
    private String content;

    /**
     * 公告类型 - 必填，系统公告/学校公告
     */
    @TableField("type")
    private Integer type;    /**
     * 预定发布时间 - 可空
     */
    @TableField("scheduled_at")
    private LocalDateTime scheduledAt;

    /**
     * 状态 - 必填：0草稿、1已发布、2待发布
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否置顶 - 默认false
     */
    @TableField("sticky")
    private Boolean sticky;
}