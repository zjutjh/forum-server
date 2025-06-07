package org.jh.forum.common.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公告实体类（MyBatis-Plus Entity）
 *
 * @author SituChengxiang
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("announcement") // MyBatis-Plus表名映射
public class Announcement {

    /**
     * 主键ID - 自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

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
    private Integer type;

    /**
     * 创建时间 - 自动填充（仅插入时生效）
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;    /**
     * 更新时间 - 自动填充（插入和更新时均生效）
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
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

    /**
     * 创建人ID - 必填
     */
    @TableField(value = "create_uid", fill = FieldFill.INSERT)
    private Long createUid;

    /**
     * 更新人ID - 必填
     */
    @TableField(value = "update_uid", fill = FieldFill.INSERT_UPDATE)
    private Long updateUid;

    /**
     * 是否删除 - 必填：true=已删除，false=未删除
     */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;

    /**
     * 属性列 - 可空，JSON字符串
     */
    @TableField("attribute")
    private String attribute; // 建议改为String类型，而不是Object
}