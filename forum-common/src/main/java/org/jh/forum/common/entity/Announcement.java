package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jh.forum.common.constants.AnnouncementStatusEnum;
import org.jh.forum.common.constants.AnnouncementTypeEnum;

import java.time.LocalDateTime;


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
     * 公告类型
     */
    @TableField("type")
    private AnnouncementTypeEnum type;

    /**
     * 发布时间
     */
    @TableField("published_at")
    private LocalDateTime publishedAt;

    /**
     * 公告状态
     */
    @TableField("status")
    private AnnouncementStatusEnum status;
    
    /**
     * （委托）发布人签名
     */
    @TableField("signatory")
    private String signatory;

    /**
     * 是否置顶
     */
    @TableField("sticky")
    private Boolean sticky;
}