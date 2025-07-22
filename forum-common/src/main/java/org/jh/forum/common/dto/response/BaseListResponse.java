package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 分页响应体基类
 *
 * @param <T> 列表元素类型
 * @author MangoGovo
 */
@Data
@SuperBuilder
public class BaseListResponse<T> {
    @Schema(description = "页码")
    private Integer page;

    @Schema(description = "每页数量")
    @JsonProperty("page_size")
    private Integer pageSize;

    @Schema(description = "总数")
    private Long total;

    @Schema(description = "列表")
    private List<T> list;
}
