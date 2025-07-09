package org.jh.forum.common.entity;

import java.time.ZonedDateTime;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jh.forum.common.constants.AnnouncementStatusEnum;
import org.jh.forum.common.constants.AnnouncementTypeEnum;


/**
 * 公告实体类（MyBatis-Plus Entity）
 *
 * @author SituChengxiang
 */
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true) 
@NoArgsConstructor(force = true)
@AllArgsConstructor
@TableName("announcement") 

public class Announcement extends BaseEntity {

    /**
     * 公告标题 - 必填， 最大50个字符
     */
    @TableField("title")
    private String title;

    /**
     * 公告内容 - 必填, 最大500个字符
     */
    @TableField("content")
    private String content;

    /**
     * 公告类型 - 必填：0(系统公告)/1(学校公告)
     */
    @TableField(value = "type")
    private AnnouncementTypeEnum type;

    /**
     * 预定发布时间 - 可空
     */
    @TableField("scheduled_at")
    private ZonedDateTime scheduledAt;

    /**
     * 实际发布时间 - 可空
     */
    @TableField("published_at")
    private ZonedDateTime publishedAt;

    /**
     * 状态 - 必填：0:草稿、1:已发布、2:待发布
     */
    @TableField("status")
    private AnnouncementStatusEnum status;

    /**
     * 是否置顶 - 默认false
     */
    @TableField("sticky")
    private Boolean sticky;

}