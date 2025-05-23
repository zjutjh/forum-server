package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * 分页响应体基类
 *
 * @param <T> 列表元素类型
 * @author MangoGovo
 */
@Data
public class BaseListResponse<T> {
    @Schema(description = "页码")
    private Integer page;

    @Schema(description = "每页数量")
    private Integer pageSize;

    @Schema(description = "总数量")
    private Integer total;

    @Schema(description = "列表")
    private List<T> list;

    @Schema(description = "分页游标")
    private Long nextCursor;
}
