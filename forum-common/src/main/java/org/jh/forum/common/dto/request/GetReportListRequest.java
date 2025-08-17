package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;

/**
 * @author zzb
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetReportListRequest extends BaseListRequest {
    @Schema(description = "处理状态: all-全部, pending-待处理, processed-已处理")
    @NotBlank
    private String status;

    @Schema(description = "排序: desc-降序, asc-升序")
    @NotBlank
    private String order;

    @Schema(description = "目标ID")
    private Long reportId;
}
