package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author qianqianzyk
 * @TableName upvote
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "upvote")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Upvote extends BaseEntity {
    private Long userId;

    private Long postId;

    private Long commentId;

    private Boolean status;
}
