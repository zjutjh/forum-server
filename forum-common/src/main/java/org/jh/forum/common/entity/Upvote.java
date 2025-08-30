package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 点赞实体类
 *
 * @author qianqianzyk
 * @TableName upvote
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "upvote")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Upvote extends BaseEntity {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 帖子ID（可为null）
     */
    private Long postId;

    /**
     * 评论ID（可为null）
     */
    private Long commentId;

    /**
     * 点赞状态
     */
    private Boolean status;
}
