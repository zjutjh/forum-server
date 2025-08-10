package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * FAQ实体类
 *
 * @author ZeroHzzzz
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName("faq")
public class FAQ extends BaseEntity {
    
    /**
     * 板块（账号/学院/帖子/其他）
     */
    @TableField("category")
    private String category;
    
    /**
     * 问题描述
     */
    @TableField("question")
    private String question;
    
    /**
     * 问题解答
     */
    @TableField("answer")
    private String answer;
    
    /**
     * 浏览量
     */
    @TableField("view_count")
    private Integer viewCount;
}