package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * @author ZeroHzzzz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "FAQ问题详情请求参数")
public class FAQQuestionDetailRequest {
    @Schema(description = "FAQ问题ID")
    @NotNull
    private Long questionId;
}