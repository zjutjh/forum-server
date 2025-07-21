package org.jh.forum.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jh.forum.common.constants.AnnouncementStatusEnum;
import org.jh.forum.common.constants.AnnouncementTypeEnum;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 编辑公告请求DTO
 *
 * @author SituChengxiang
 */
@Data
@Schema(description = "修改公告")
public class EditAnnouncementRequest {
    @NotNull
    @Min(1)
    @Schema(description = "公告ID")
    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    @Schema(description = "公告标题", example = "重要系统维护通知")
    private String title;

    @NotBlank
    @Size(min = 1, max = 500)
    @Schema(description = "公告内容", example = "系统将于今晚进行维护升级...")
    private String content;

    @NotNull
    @Schema(description = "公告类型")
    private AnnouncementTypeEnum type;

    @NotNull
    @Schema(description = "公告状态")
    private AnnouncementStatusEnum status;

    @JsonProperty("published_at")
    @Schema(description = "定时发布时间(yyyy-MM-dd'T'HH:mm:ss, 前端发送UTC+8本地时间)")
    private LocalDateTime publishedAt;
}
