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
    @Schema(description = "分页页码", example = "1", defaultValue = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer page = 1;

    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能超过100")
    @Schema(description = "每页数量", example = "8", defaultValue = "8", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer size = 8;

    /**
     * 公告状态筛选：0=草稿， 1=已发布， 2=定时发布， 3=全部
     */
    @Schema(description = "状态筛选 (0=草稿， 1=已发布， 2=定时发布, 3=全部)", example = "1", defaultValue = "2", allowableValues = {
            "0", "1", "2", "3" }, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer status = 0;

    /**
     * 公告类型筛选：1=系统公告(db:type=0)， 2=学校公告(db:type=1)， 3=全部
     */
    @Schema(description = "类型筛选 (1=系统公告， 2=学校公告， 3=全部）", example = "1", defaultValue = "3", allowableValues = { "1", "2",
            "3" }, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer type = 3;

    /**
     * 排序方向：0=正序， 1=逆序
     */
    @Schema(description = "排序方向 (0=正序, 1=逆序）", example = "0", defaultValue = "0", allowableValues = { "0",
            "1" }, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer orderType = 0;
    @Schema(description = "是否包括已删除数据", example = "false", defaultValue = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean deleted = false;

    /**
     * 查询关键字, 可选，查询关键字(Manager层里头实际上是title，因为是标题的关键字)
     */
    @Schema(description = "查询关键字", example = "公告标题", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String keyword;

    public int orderType() {
        return orderType == null ? 0 : orderType;
    }
}