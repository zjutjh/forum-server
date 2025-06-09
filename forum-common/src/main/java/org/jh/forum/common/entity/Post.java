package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author SugarMGP
 * @TableName post
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "post")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post extends BaseEntity {
    private Long userId;

    private String title;

    private String content;

    private Long categoryId;

    private Boolean isPinned;
}