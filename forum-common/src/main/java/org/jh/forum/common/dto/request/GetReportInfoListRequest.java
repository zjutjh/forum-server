package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zzb
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetReportInfoListRequest extends BaseListRequest {
    @Schema(description = "举报ID")
    private Long reportId;
}
