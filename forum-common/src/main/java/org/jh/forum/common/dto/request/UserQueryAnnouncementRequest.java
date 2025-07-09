package org.jh.forum.common.dto.request;

import com.alibaba.nacos.shaded.javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户公告列表查询请求DTO
 * 
 * @author SituChengxiang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "用户公告列表查询请求")
public class UserQueryAnnouncementRequest extends BaseListRequest {

    /**
     * 公告类型筛选：systematic,scholastic
     */
    @Nullable
    @Schema(description = "类型筛选 (systematic, scholastic,all)", allowableValues = {
            "systematic", "scholastic",
            "all" }, example = "systematic", defaultValue = "all", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String type;

    @Nullable
    @Schema(description = "查询关键字", example = "节日", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String keywords;
    // 可选，查询关键字(Manager层里头实际上是title，因为是标题的关键字)
}