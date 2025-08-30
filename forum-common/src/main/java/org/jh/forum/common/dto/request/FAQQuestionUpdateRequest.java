package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jh.forum.common.constants.FAQCategoryEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * FAQ问题更新请求
 *
 * @author ZeroHzzzz
 */
@Data
public class FAQQuestionUpdateRequest {
    @Schema(description = "问题ID")
    @NotNull
    private Long questionId;

    @Schema(description = "FAQ类别")
    @NotNull
    private FAQCategoryEnum category;

    @Schema(description = "问题描述")
    @Size(max = 200)
    @NotBlank
    private String question;

    @Schema(description = "问题答案")
    @Size(max = 500)
    @NotBlank
    private String answer;

    @Schema(description = "是否精选")
    @NotNull
    private Boolean isPicked;
}
