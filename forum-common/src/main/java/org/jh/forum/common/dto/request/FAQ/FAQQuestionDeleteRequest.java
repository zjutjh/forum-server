package org.jh.forum.common.dto.request.FAQ;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    
    @Schema(description = "问题ID", required = true, example = "1")
    @NotNull(message = "问题ID不能为空")
    private Long questionId;
}
