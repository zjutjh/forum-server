package org.jh.forum.common.dto.response.FAQ;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * FAQ操作结果响应对象
 *
 * @author ZeroHzzzz
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "FAQ操作结果响应数据")
public class FAQOperationResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "是否操作成功", example = "true", required = true)
    private Boolean success;
    
    @Schema(description = "操作影响的问题ID", example = "1")
    private Long questionId;
    
    @Schema(description = "操作结果消息", example = "创建成功")
    private String message;
}
