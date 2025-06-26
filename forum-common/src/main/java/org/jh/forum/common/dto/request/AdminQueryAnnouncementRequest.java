package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 管理员公告列表查询请求DTO
 * 
 * @author SituChengxiang
 */
@Data
@Schema(description = "管理员公告列表查询请求")
public class AdminQueryAnnouncementRequest {

    @Min(value = 1, message = "页码不能小于1")
    @Schema(description = "分页页码", example = "1", defaultValue = "1")
    private Integer page = 1;

    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能超过100")
    @Schema(description = "每页数量", example = "8", defaultValue = "8")
    private Integer size = 8;

    /**
     * 公告状态筛选：0=草稿，1=已发布，2=未发布，可以留一个3作为全部，但是现在还没写，就不妨再allowvalue里头了
     */
    @Schema(description = "状态筛选（0=草稿，1=已发布，2=未发布）", example = "1", defaultValue = "2", allowableValues = { "0", "1", "2" })
    private Integer status = 0;

    // 这里本来预留了一个排序字段的，目前先删掉了，三类按照各自的排序规则来处理

    /**
     * 排序方向：0=正序，1=逆序
     */
    @Schema(description = "排序方向（0=正序，1=逆序）", example = "0", defaultValue = "0", allowableValues = { "0", "1" })
    private Integer orderType = 0;
    @Schema(description = "是否查询已删除数据", example = "false", defaultValue = "false")
    private Boolean deleted = false;

    public int orderType() {
        return orderType == null ? 0 : orderType;
    }
}