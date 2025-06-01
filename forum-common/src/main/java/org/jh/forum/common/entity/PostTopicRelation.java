package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author SugarMGP
 * @TableName post_topic_relation
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "post_topic_relation")
@Data
public class PostTopicRelation extends BaseEntity {
    private Long postId;

    private Long topicId;
}