package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author SugarMGP
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetAdminListRequest extends BaseListRequest {
    @Schema(description = "搜索关键词（用户名）")
    private String keyword;
}
