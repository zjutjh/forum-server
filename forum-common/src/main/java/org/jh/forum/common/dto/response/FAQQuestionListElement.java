package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.FAQCategoryEnum;

import java.time.LocalDateTime;

/**
 * FAQ问题列表响应对象
 *
 * @author ZeroHzzzz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "FAQ问题列表响应数据")
public class FAQQuestionListElement {
    @Schema(description = "问题ID")
    private Long questionId;

    @Schema(description = "分类名称")
    private FAQCategoryEnum category;

    @Schema(description = "问题描述")
    private String question;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
