package org.jh.forum.common.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 创建公告请求DTO
 * @author SituChengxiang
 */
@Data
@Schema(description = "创建公告请求")
public class CreateAnnouncementRequest {

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

    @NotNull(message = "创建用户ID获取失败")
    @Schema(description = "创建用户ID", example = "123", required = true)
    @JsonProperty("creator_id") // 显式声明JSON字段名称
    private Integer creatorId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "定时发布时间", example = "2025-05-30 09:00:00")
    private LocalDateTime scheduled_at;

    @Schema(
        description = "状态：0草稿、1已发布、2待发布",
        example = "0",
        allowableValues = { "0", "1", "2" }
    )
    private Integer status;

    @Schema(description = "附加属性", example = "{\"sticky\": true}")
    private String attribute;
}
