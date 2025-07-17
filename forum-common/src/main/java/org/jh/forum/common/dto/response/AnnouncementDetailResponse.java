package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jh.forum.common.constants.AnnouncementStatusEnum;

import java.time.LocalDateTime;

/**
 * 公告详情响应DTO
 *
 * @author SituChengxiang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "公告详情响应")
public class AnnouncementDetailResponse extends AnnouncementTinyDetailsResponse {

    @Schema(description = "状态", example = "draft")
    private AnnouncementStatusEnum status;

    @JsonProperty("created_at")
    @Schema(description = "创建时间", example = "2025-06-07T09:00:00.000Z")
    private LocalDateTime createdAt;

    @JsonProperty("scheduled_at")
    @Schema(description = "定时发布时间", example = "2025-06-07T09:00:00.000Z")
    private LocalDateTime scheduledAt;

    @Schema(description = "附加属性", example = "{\"test\": true}")
    private String attribute;
}