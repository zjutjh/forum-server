package org.jh.forum.common.dto.response;

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

        @Schema(description = "公告类型", example = "0")
        private Integer type;

        @Schema(description = "状态", example = "1")
        private Integer status;

        @Schema(description = "创建用户名称", example = "admin")
        private String creator;

        @Schema(description = "更新用户名称", example = "suadmin")
        private String updator;

        @Schema(description = "创建时间", example = "2025-06-07T09:00:00.000Z")
        private String createdAt;

        @Schema(description = "更新时间", example = "2025-06-07T09:00:00.000Z")
        private String updatedAt;

        @Schema(description = "预定发布时间", example = "2025-06-07T09:00:00.000Z")
        private String scheduledAt;

        @Schema(description = "是否置顶", example = "false")
        private boolean sticky;
    }
}