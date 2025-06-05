package org.jh.forum.common.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公告实体类（JPA & MyBatis-Plus Entity）
 *
 * @author SituChengxiang
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "announcement") // 映射到数据库表名
public class Announcement {

    /**
     * 主键ID - 自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 公告标题 - 必填，最大50字符
     */
    @Column(name = "title", nullable = false, length = 50)
    private String title;

    /**
     * 公告内容 - 必填，最大500字符
     */
    @Column(name = "content", nullable = false, length = 500)
    private String content;

    /**
     * 公告类型 - 必填，系统公告/学校公告
     */
    @Column(name = "type", nullable = false, length = 20)
    private int type;

    /**
     * 创建时间 - 自动填充（仅插入时生效）
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间 - 自动填充（插入和更新时均生效）
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 预定发布时间 - 可空
     */
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    /**
     * 状态 - 必填：0草稿、1已发布、2待发布
     */
    @Column(name = "status", nullable = false)
    private Integer status;

    /**
     * 创建人ID - 必填
     */
    @Column(name = "creator_id", nullable = false)
    private Integer creatorId;

    /**
     * 更新人ID - 必填
     */
    @Column(name = "updator_id", nullable = false)
    private int updatorId;

    /**
     * 是否删除 - 必填：true=已删除，false=未删除
     */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    /**
     * 属性列 - 可空，JSON字符串
     */
    @Column(name = "attribute", columnDefinition = "TEXT")
    private Object attribute;


}