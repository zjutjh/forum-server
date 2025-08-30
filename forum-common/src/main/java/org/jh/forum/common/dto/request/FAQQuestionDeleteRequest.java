package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * FAQ问题删除请求
 *
 * @author ZeroHzzzz
 */
@Data
public class FAQQuestionDeleteRequest {
    @Schema(description = "问题ID")
    @NotNull
    private Long questionId;
}
