package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.AnnouncementTypeEnum;

import java.time.LocalDateTime;

/**
 * 查询公告列表响应DTO
 *
 * @author SituChengiang
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "公告列表项（用户版）")
public class GetAnnouncementListElement {
    @Schema(description = "公告ID")
    private Long id;

    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "类型")
    private AnnouncementTypeEnum type;

    @Schema(description = "发布人")
    private String publisher;

    @Schema(description = "发布时间 yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime publishedAt;

    @Schema(description = "是否置顶")
    private boolean sticky;
}
