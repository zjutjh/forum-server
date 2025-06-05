package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 查询公告列表请求DTO
 * 
 * @author SituChengxiang
 */
@Data
@Schema(description = "查询公告列表请求")
public class ListAnnouncementRequest {

    @Min(value = 1, message = "页码不能小于1")
    @Schema(description = "页码", example = "1", defaultValue = "1")
    private Integer page = 1;

    @Schema(description = "每页大小，固定为8条", example = "8", defaultValue = "8", hidden = true)
    private Integer size = 8;

    @Schema(description = "状态筛选：0草稿、1已发布、2待发布", example = "1", allowableValues = { "0", "1", "2" })
    private Integer status;

    @Schema(description = "公告类型筛选", example = "系统公告", allowableValues = { "系统公告", "学校公告" })
    private String type;
}
