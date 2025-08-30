package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
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
    @Max(999)
    @NotNull
    private Integer page;

    @Schema(description = "每页数量（超过20自动限制到20）")
    @Min(1)
    @NotNull
    private Integer pageSize;

    public void setPageSize(Integer pageSize) {
        if (pageSize != null && pageSize > 20) {
            this.pageSize = 20;
        } else {
            this.pageSize = pageSize;
        }
    }
}
