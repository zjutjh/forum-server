package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公告详情响应DTO
 * 
 * @author SituChengxiang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "公告详情响应")
public class AnnouncementDetailResponse extends AnnouncementTinyDetailsResponse{

    @Schema(description = "状态", example = "0")
    private Integer status;

    @Schema(description = "定时发布时间", example = "2025-06-07T09:00:00.000Z")
    private String scheduledAt;

    @Schema(description = "附加属性", example = "{\"sticky\": true}")
    private Object attribute;
}