package org.jh.forum.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页请求体基类
 *
 * @author MangoGovo
 */
@Data
public class BaseListRequest {
    @Schema(description = "页码")
    private Integer page;

    @Schema(description = "每页数量")
    @JsonProperty("page_size")
    private Integer pageSize;
}
