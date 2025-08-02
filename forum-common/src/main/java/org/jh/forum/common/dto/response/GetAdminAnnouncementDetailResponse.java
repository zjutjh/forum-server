package org.jh.forum.common.dto.response;

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
    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "公告内容")
    private String content;

    @Schema(description = "公告类型")
    private AnnouncementTypeEnum type;

    @Schema(description = "发布人")
    private String publisher;

    @Schema(description = "（委托）发布人签名")
    private String signatory;

    @Schema(description = "最后编辑时间 yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(description = "是否置顶")
    private Boolean sticky;

    @Schema(description = "公告状态")
    private AnnouncementStatusEnum status;

    @Schema(description = "发布时间 yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime publishedAt;

    @Schema(description = "有无权限编辑")
    private boolean canEdit;
}