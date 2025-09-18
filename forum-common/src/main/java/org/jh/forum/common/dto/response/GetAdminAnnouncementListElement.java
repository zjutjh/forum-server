package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.AnnouncementStatusEnum;
import org.jh.forum.common.constants.AnnouncementTypeEnum;

import java.io.Serializable;
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
public class GetAdminAnnouncementListElement implements Serializable {
    @Schema(description = "公告ID")
    private Long id;

    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "类型")
    private AnnouncementTypeEnum type;

    @Schema(description = "发布时间 yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime publishedAt;

    @Schema(description = "最后编辑时间 yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(description = "是否置顶")
    private Boolean sticky;

    @Schema(description = "状态")
    private AnnouncementStatusEnum status;

    @Schema(description = "有无权限编辑")
    private boolean editable;
}