package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zzb
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetReportListRequest extends BaseListRequest {
    @Schema(description = "处理状态: 1-待处理, 2-已处理")
    @NotNull
    @Min(1)
    @Max(2)
    private Integer status;

    @Schema(description = "升序/降序: 1-升序, 2-降序")
    @NotNull
    @Min(1)
    @Max(2)
    private Integer order;
}
