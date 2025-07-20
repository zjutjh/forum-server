package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;

/**
 * 用户公告列表查询请求DTO
 *
 * @author SituChengxiang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "用户公告列表查询请求")
public class GetAnnouncementListRequest extends BaseListRequest {
    @NotNull
    @Schema(description = "类型筛选 (systematic, scholastic,all)",
            allowableValues = {"systematic", "scholastic", "all"})
    private String type;
}