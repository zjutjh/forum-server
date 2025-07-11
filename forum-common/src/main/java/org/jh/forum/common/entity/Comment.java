package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author qianqianzyk
 * @TableName comment
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "comment")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity {
    private Long userId;

    private Long postId;

    private Long parentId;

    private Long targetId;

    private String content;

    private Boolean isPinned;

    private Integer upvoteCount;
    
    private Integer replyCount;
}