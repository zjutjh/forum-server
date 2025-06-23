package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author SugarMGP
 * @TableName post_topic_relation
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "post_topic_relation")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostTopicRelation extends BaseEntity {
    private Long postId;

    private Long topicId;
}