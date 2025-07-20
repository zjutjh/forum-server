package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.annotation.Nullable;


/**
 * 管理员公告列表查询请求DTO
 *
 * @author SituChengxiang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "管理员公告列表查询请求")
public class AdminQueryAnnouncementRequest extends BaseListRequest {

    /**
     * 公告类型筛选：systematic,scholastic
     */
    @Nullable
    @Schema(description = "类型筛选 (systematic, scholastic,all)", allowableValues = {
            "systematic", "scholastic",
            "all"}, example = "systematic", defaultValue = "all", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String type;

    /**
     * 公告状态筛选：draft, published, scheduled, all
     */
    @Nullable
    @Schema(description = "状态筛选 (draft, published, scheduled, all)", allowableValues = {"draft", "published",
            "scheduled",
            "all"}, example = "all", defaultValue = "all", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String status = "all";

    /**
     * 排序方向：0=正序， 1=逆序
     */
    @Nullable
    @Schema(description = "排序方向 (0=正序, 1=逆序）", allowableValues = {"0",
            "1"}, example = "0", defaultValue = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer orderType = 0;

    /**
     * 是否包括已删除数据
     */
    @Nullable
    @Schema(description = "是否包括已删除数据", allowableValues = {"true",
            "false"}, example = "false", defaultValue = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean deleted = false;

    /**
     * 查询关键字, 可选，查询关键字(Manager层里头实际上是title，因为是标题的关键字)
     */
    @Nullable
    @Schema(description = "查询关键字", example = "节日", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String keywords;

    public int orderType() {
        return orderType == null ? 0 : orderType;
    }
}