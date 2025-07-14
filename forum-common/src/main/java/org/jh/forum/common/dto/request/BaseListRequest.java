package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 分页请求体基类
 *
 * @author MangoGovo
 */
@Data
public class BaseListRequest {
    @Schema(description = "页码")
    @Min(1)
    @NotNull
    private Integer page;

    @Schema(description = "每页数量")
    @Min(1)
    @NotNull
    private Integer pageSize;
}
