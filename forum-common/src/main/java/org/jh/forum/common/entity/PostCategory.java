package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author SugarMGP
 * @TableName post_category
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "post_category")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PostCategory extends BaseEntity {
    private String name;
}