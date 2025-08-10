package org.jh.forum.common.dto.response.FAQ;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FAQ问题列表响应对象
 *
 * @author ZeroHzzzz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "FAQ问题列表响应数据")
public class FAQQuestionListResponse {

    @Schema(description = "问题ID", example = "1", required = true)
    private Long questionId;

    @Schema(description = "分类名称", example = "账号问题")
    private String category;
    
 
    @Schema(description = "问题描述", example = "如何重置密码？", required = true)
    private String question;
    
    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private java.time.LocalDateTime createdAt;
}
