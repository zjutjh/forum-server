package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author SugarMGP
 * @TableName post
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "post")
@Data
public class Post extends BaseEntity {
    private Long userId;

    private String title;

    private String content;

    private Long categoryId;

    private Boolean isPinned;
}