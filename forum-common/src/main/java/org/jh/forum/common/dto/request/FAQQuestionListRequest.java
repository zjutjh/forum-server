package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.FAQCategoryEnum;

/**
 * @author ZeroHzzzz
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "FAQ问题列表请求参数")
public class FAQQuestionListRequest extends BaseListRequest {
    @Schema(description = "FAQ类别（空字符串则为猜你想问）")
    private FAQCategoryEnum category;
}