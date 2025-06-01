package org.jh.forum.common.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建公告请求DTO
 * @author SituChengxiang
 */
@Data
@Schema(description = "修改公告")
public class EditAnnouncementRequest {

    @NotBlank(message = "公告标题不能为空")
    @Size(min = 2, max = 100, message = "公告标题必须在2~50个字符之间")
    @Schema(
        description = "公告标题",
        example = "重要系统维护通知",
        required = true
    )
    private String title;

    @NotBlank(message = "公告内容不能为空")
    @Size(min = 1, max = 1000, message = "公告内容不能超过500个字符")
    @Schema(
        description = "公告内容",
        example = "系统将于今晚进行维护升级...",
        required = true
    )
    private String content;

    @NotBlank(message = "公告类型不能为空")
    @Schema(
        description = "公告类型",
        example = "系统公告",
        required = true,
        allowableValues = { "系统公告", "学校公告" }
    )
    private String type;

    @NotNull(message = "修改用户ID获取失败")
    @Schema(description = "修改用户ID", example = "123", required = true)
    @JsonProperty("updator_id") // 显式声明JSON字段名称
    private Integer updatorId;

    @JsonProperty("scheduled_at") // 显式声明JSON字段名称
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC+8")
    @Schema(description = "定时发布时间", example = "1955-09-06T13:10:21.927Z")
    private LocalDateTime scheduledAt;

    @Schema(
        description = "状态：0草稿、1已发布、2待发布",
        example = "0",
        allowableValues = { "0", "1", "2" }
    )
    private Integer status;

    @Schema(description = "附加属性", example = "{\"sticky\": true}")
    private String attribute;

    // Getter 和 Setter 方法
    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
}
