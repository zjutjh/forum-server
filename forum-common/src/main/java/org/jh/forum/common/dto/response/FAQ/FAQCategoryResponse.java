package org.jh.forum.common.dto.response.FAQ;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * FAQ分类响应对象
 *
 * @author ZeroHzzzz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "FAQ分类响应数据")
public class FAQCategoryResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "分类名称", example = "账号问题", required = true)
    private String category;
}
