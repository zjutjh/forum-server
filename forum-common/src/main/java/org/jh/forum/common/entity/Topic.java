package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 话题实体类
 *
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