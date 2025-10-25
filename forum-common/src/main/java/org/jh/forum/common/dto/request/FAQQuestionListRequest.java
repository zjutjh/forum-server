package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jh.forum.common.constants.FAQCategoryEnum;

/**
 * FAQ问题列表请求参数
 *
 * @author ZeroHzzzz
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FAQQuestionListRequest extends BaseListRequest {
    @Schema(description = "FAQ类别（空字符串则为猜你想问）")
    private FAQCategoryEnum category;
}