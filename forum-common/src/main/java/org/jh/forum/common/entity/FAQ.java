package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jh.forum.common.constants.FAQCategoryEnum;

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
    private FAQCategoryEnum category;

    private String question;

    private String answer;

    private Integer viewCount;

    private Boolean isPicked;
}