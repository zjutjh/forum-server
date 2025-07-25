package org.jh.forum.common.dto.response.FAQ;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FAQ分类响应对象
 *
 * @author ZeroHzzzz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "FAQ分类响应数据")
public class FAQCategoryResponse {    
    @Schema(description = "分类名称", example = "账号问题", required = true)
    private String category;
}
