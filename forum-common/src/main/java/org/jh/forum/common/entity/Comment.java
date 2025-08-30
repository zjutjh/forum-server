package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 评论实体类
 *
 * @author qianqianzyk
 * @TableName comment
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "comment")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity {
    private Long userId;

    private Long postId;

    private Long parentId;

    private Long targetId;

    private Long targetUserId;

    private String content;

    private Boolean isPinned;

    private Integer upvoteCount;

    private Integer replyCount;
}