package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author SugarMGP
 * @TableName topic
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "topic")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Topic extends BaseEntity {
    private String name;
}