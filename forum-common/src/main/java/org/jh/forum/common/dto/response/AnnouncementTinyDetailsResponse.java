package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jh.forum.common.constants.AnnouncementTypeEnum;

import java.time.LocalDateTime;

/**
 * 公告详情响应DTO（用户版本）
 *
 * @author SituChengxiang
 */
@Data
@Schema(description = "公告详情响应")
public class AnnouncementTinyDetailsResponse {
    @Schema(description = "公告ID", example = "1")
    private Long id;

    @Schema(description = "公告标题", example = "重要系统维护通知")
    private String title;

    @Schema(description = "公告内容", example = "系统将于今晚进行维护升级...")
    private String content;

    @Schema(description = "公告类型", example = "system")
    private AnnouncementTypeEnum type;

    @Schema(description = "创建用户的昵称", example = "admin")
    private String creator;

    @Schema(description = "更新用户的昵称", example = "su_admin")
    private String updater;

    @JsonProperty("updated_at")
    @Schema(description = "更新时间", example = "2025-06-07T09:00:00.000Z")
    private LocalDateTime updatedAt;

    @JsonProperty("published_at")
    @Schema(description = "实际发布时间", example = "2025-06-07T09:00:00.000Z")
    private LocalDateTime publishedAt;

    @Schema(description = "是否置顶", example = "false")
    private Boolean sticky;
}