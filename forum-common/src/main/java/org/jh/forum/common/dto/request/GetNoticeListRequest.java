package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author lyyzzz
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetNoticeListRequest extends BaseListRequest {
    @Schema(description = "通知类型（1-赞/收藏 2-评论/At）")
    private Integer type;
}
