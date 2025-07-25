package org.jh.forum.common.dto.request.FAQ;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "FAQ问题列表请求参数")
public class FAQQuestionListRequest {    
    
    @Schema(description = "FAQ类别", example = "账号问题")
    private String category;
}