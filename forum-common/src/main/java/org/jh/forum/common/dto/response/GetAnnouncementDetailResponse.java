package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.jh.forum.common.constants.AnnouncementTypeEnum;

import java.time.LocalDateTime;

/**
 * @author SituChengxiang
 */
@Data
@Builder
@Schema(description = "用户公告详情响应")
public class GetAnnouncementDetailResponse {
    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "公告内容")
    private String content;

    @Schema(description = "公告类型")
    private AnnouncementTypeEnum type;

    @Schema(description = "发布人")
    private String publisher;

    @JsonProperty("published_at")
    @Schema(description = "发布时间 yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime publishedAt;

    @Schema(description = "是否置顶")
    private Boolean sticky;
}