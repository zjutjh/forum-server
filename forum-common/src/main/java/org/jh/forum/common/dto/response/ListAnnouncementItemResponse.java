package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.AnnouncementStatusEnum;
import org.jh.forum.common.constants.AnnouncementTypeEnum;

import java.time.LocalDateTime;

/**
 * 查询公告列表响应DTO（管理员版本）
 *
 * @author SituChengiang
 */


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "公告列表项（管理员版）")
public class ListAnnouncementItemResponse {

    @Schema(description = "公告ID", example = "1")
    private Long id;

    @Schema(description = "公告标题", example = "重要系统维护通知")
    private String title;

    @Schema(description = "类型", example = "systematic")
    private AnnouncementTypeEnum type;

    @Schema(description = "创建用户昵称", example = "admin")
    private String creator;

    @Schema(description = "更新用户昵称", example = "su_admin")
    private String updater;

    @JsonProperty("updated_at")
    @Schema(description = "更新时间", example = "2025-06-07T09:00:00.000Z")
    private LocalDateTime updatedAt;

    @JsonProperty("published_at")
    @Schema(description = "实际发布时间", example = "2025-06-07T09:00:00.000Z")
    private LocalDateTime publishedAt;

    @Schema(description = "是否置顶", example = "false")
    private boolean sticky;

    @Schema(description = "状态", example = "published")
    private AnnouncementStatusEnum status;

    @JsonProperty("created_at")
    @Schema(description = "创建时间", example = "2025-06-07T09:00:00.000Z")
    private LocalDateTime createdAt;

    @JsonProperty("scheduled_at")
    @Schema(description = "预定发布时间", example = "2025-06-07T09:00:00.000Z")
    private LocalDateTime scheduledAt;
}