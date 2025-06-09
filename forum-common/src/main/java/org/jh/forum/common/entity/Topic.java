package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author SugarMGP
 * @TableName topic
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "topic")
@Data
@Builder
public class Topic extends BaseEntity {
    private String name;
}