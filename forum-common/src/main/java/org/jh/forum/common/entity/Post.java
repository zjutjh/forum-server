package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jh.forum.common.constants.CategoryEnum;

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

    private CategoryEnum category;

    private Boolean isPinned;

    private Boolean isTopped;
}