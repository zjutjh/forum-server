package org.jh.forum.common.dto.request;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 编辑公告请求DTO
 * 
 * @author SituChengxiang
 */
@Data
@Schema(description = "修改公告")
public class EditAnnouncementRequest {

    @NotNull(message = "公告ID不能为空")
    @Min(value = 1, message = "公告ID不能小于1")
    @Schema(description = "公告ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @NotBlank(message = "公告标题不能为空")
    @Size(min = 2, max = 100, message = "公告标题必须在2~50个字符之间")
    @Schema(description = "公告标题", example = "重要系统维护通知", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "公告内容不能为空")
    @Size(min = 1, max = 1000, message = "公告内容不能超过500个字符")
    @Schema(description = "公告内容", example = "系统将于今晚进行维护升级...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;    @Schema(description = "公告类型, 0系统1学校", example = "1", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = { "0", "1" })
    private Integer type;

    /**
     * 定时发布时间
     */
    @JsonProperty("scheduled_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @Schema(description = "定时发布时间(ISO8601格式, 前端发送UTC+8本地时间)", example = "2025-06-07T09:00:00+08:00")
    private ZonedDateTime scheduledAt;

    @NotNull(message = "必须指定公告状态")
    @Schema(description = "状态：0草稿、1已发布、2待发布", example = "0", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = { "0", "1", "2" })
    private Integer status;

    @Nullable
    @Schema(description = "附加属性", example = "{\"sticky\": true}")
    private Object attribute;

    @Nullable
    @Schema(description = "是否置顶", example = "false", allowableValues = { "true", "false" })
    private Boolean sticky;
}
