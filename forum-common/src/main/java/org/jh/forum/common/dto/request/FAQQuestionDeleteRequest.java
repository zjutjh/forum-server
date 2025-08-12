package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * FAQ问题删除请求
 *
 * @author ZeroHzzzz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "FAQ问题删除请求参数")
public class FAQQuestionDeleteRequest {
    @Schema(description = "问题ID")
    @NotNull
    private Long questionId;
}
