package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author SugarMGP
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetPersonalPostRequest extends BaseListRequest {
    @Schema(description = "若是他人视角，则传他人用户ID；若是本人视角，则可以不传或者传本人ID")
    private Long id;

    @Schema(description = "搜索关键字")
    private String keyword;
}
