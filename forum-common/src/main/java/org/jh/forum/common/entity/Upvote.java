package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
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
    private Long userId;

    private Long postId;

    private Long commentId;

    private Boolean status;
}
