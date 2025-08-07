package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;

/**
 * @author lyyzzz
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetNoticeListRequest extends BaseListRequest {
    @Schema(description = "通知类型（0-全部 1-赞 2-收藏 3-评论/At）")
    @NotNull
    private Integer type;
}
