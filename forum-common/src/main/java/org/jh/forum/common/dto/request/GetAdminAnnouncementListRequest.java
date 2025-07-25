package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jh.forum.common.constants.AnnouncementStatusEnum;
import org.jh.forum.common.constants.AnnouncementTypeEnum;

import jakarta.validation.constraints.NotNull;


/**
 * 管理员公告列表查询请求DTO
 *
 * @author SituChengxiang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "管理员公告列表查询请求")
public class GetAdminAnnouncementListRequest extends BaseListRequest {
    @Schema(description = "类型筛选 (空则全部)")
    private AnnouncementTypeEnum type;

    @Schema(description = "状态筛选 (空则全部)")
    private AnnouncementStatusEnum status;

    @NotNull
    @Schema(description = "排序方向（desc：从新到旧，asc：从旧到新）", allowableValues = {"desc", "asc"})
    private String order;

    @Schema(description = "查询关键字")
    private String keyword;
}