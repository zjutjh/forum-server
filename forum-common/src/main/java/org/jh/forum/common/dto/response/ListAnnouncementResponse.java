package org.jh.forum.common.dto.response;

import org.jh.forum.common.constants.AnnouncementStatusEnum;
import org.jh.forum.common.constants.AnnouncementTypeEnum;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询公告列表响应DTO（管理员版本）
 * 
 * @author SituChengiang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询公告列表响应")
public class ListAnnouncementResponse extends BaseListResponse<ListAnnouncementResponse.AnnouncementItemResponse> {

    @Data
    @Schema(description = "公告列表项")
    public static class AnnouncementItemResponse {
        @Schema(description = "公告ID", example = "1")
        private Long id;

        @Schema(description = "公告标题", example = "重要系统维护通知")
        private String title;

        @Schema(description = "公告类型", example = "systematic")
        private AnnouncementTypeEnum type;

        @Schema(description = "状态", example = "published")
        private AnnouncementStatusEnum status;

        @Schema(description = "创建用户昵称", example = "admin")
        private String creator;

        @Schema(description = "更新用户昵称", example = "suadmin")
        private String updator;

        @JsonProperty("created_at")
        @Schema(description = "创建时间", example = "2025-06-07T09:00:00.000Z")
        private String createdAt;

        @JsonProperty("updated_at")
        @Schema(description = "更新时间", example = "2025-06-07T09:00:00.000Z")
        private String updatedAt;

        @JsonProperty("scheduled_at")
        @Schema(description = "预定发布时间", example = "2025-06-07T09:00:00.000Z")
        private String scheduledAt;

        @JsonProperty("published_at")
        @Schema(description = "实际发布时间", example = "2025-06-07T09:00:00.000Z")
        private String publishedAt;

        @Schema(description = "是否置顶", example = "false")
        private boolean sticky;
    }
}