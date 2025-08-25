package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jh.forum.common.constants.PostCategoryEnum;

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

    private PostCategoryEnum category;

    private Boolean isPinned;

    private Boolean isTopped;

    private Integer viewCount;

    private Integer reportCount;

    private Integer resolvedReportCount;
}