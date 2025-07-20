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
public class GetAdminAnnouncementListElement {
    @Schema(description = "公告ID", example = "1")
    private Long id;

    @Schema(description = "公告标题", example = "重要系统维护通知")
    private String title;

    @Schema(description = "类型", example = "systematic")
    private AnnouncementTypeEnum type;

    @JsonProperty("published_at")
    @Schema(description = "发布时间", example = "2025-06-07T09:00:00")
    private LocalDateTime publishedAt;

    @JsonProperty("updated_at")
    @Schema(description = "最后编辑时间", example = "2025-06-07T09:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "是否置顶", example = "false")
    private Boolean sticky;

    @Schema(description = "状态", example = "published")
    private AnnouncementStatusEnum status;
}