package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author SugarMGP
 * @TableName post
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "post")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Post extends BaseEntity {
    private Long userId;

    private String title;

    private String content;

    private Long categoryId;

    private Boolean isPinned;
}