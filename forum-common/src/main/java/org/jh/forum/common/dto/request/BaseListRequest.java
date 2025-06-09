package org.jh.forum.common.dto.request;

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
    private Integer page = 1;

    @Schema(description = "每页数量")
    private Integer pageSize = 10;
}
