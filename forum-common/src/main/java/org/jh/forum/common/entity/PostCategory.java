package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author SugarMGP
 * @TableName post_category
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "post_category")
@Data
public class PostCategory extends BaseEntity {
    private String name;
}