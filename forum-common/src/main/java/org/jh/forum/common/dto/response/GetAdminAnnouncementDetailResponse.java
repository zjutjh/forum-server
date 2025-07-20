package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.jh.forum.common.constants.AnnouncementStatusEnum;
import org.jh.forum.common.constants.AnnouncementTypeEnum;

import java.time.LocalDateTime;

/**
 * @author SituChengxiang
 */
@Data
@Builder
@Schema(description = "管理员公告详情响应")
public class GetAdminAnnouncementDetailResponse {
    @Schema(description = "公告标题", example = "重要系统维护通知")
    private String title;

    @Schema(description = "公告内容", example = "系统将于今晚进行维护升级...")
    private String content;

    @Schema(description = "公告类型", example = "system")
    private AnnouncementTypeEnum type;

    @Schema(description = "发布人", example = "admin")
    private String publisher;

    @JsonProperty("updated_at")
    @Schema(description = "最后编辑时间", example = "2025-06-07T09:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "是否置顶")
    private Boolean sticky;

    @Schema(description = "公告状态")
    private AnnouncementStatusEnum status;

    @JsonProperty("published_at")
    @Schema(description = "发布时间", example = "2025-06-07T09:00:00")
    private LocalDateTime publishedAt;
}