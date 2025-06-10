package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author SugarMGP
 * @TableName topic
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "topic")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Topic extends BaseEntity {
    private String name;
}