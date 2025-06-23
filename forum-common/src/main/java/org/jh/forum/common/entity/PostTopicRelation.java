package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author SugarMGP
 * @TableName post_topic_relation
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "post_topic_relation")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PostTopicRelation extends BaseEntity {
    private Long postId;

    private Long topicId;
}