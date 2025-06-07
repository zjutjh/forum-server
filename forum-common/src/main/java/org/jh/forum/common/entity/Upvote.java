package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianqianzyk
 * @TableName upvote
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@TableName(value = "upvote")
public class Upvote extends BaseEntity {
    private Long userId;

    private Long postId;

    private Long commentId;

    private Boolean status;
}
