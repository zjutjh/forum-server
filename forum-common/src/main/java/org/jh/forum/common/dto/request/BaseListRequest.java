package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * 分页请求体基类
 *
 * @author MangoGovo
 */
@Data
public class BaseListRequest {
    @Parameter(description = "页码")
    private Integer page = 1;
    @Parameter(description = "每页数量")
    private Integer size = 20;
    @Parameter(description = "查询偏移")
    private Long offset = 0L;
}
