package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author zzb
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetReportListElement {
    @Schema(description = "举报ID")
    private Long id;

    @Schema(description = "举报目标类型 评论/帖子/用户")
    @JsonProperty("target_type")
    private String targetType;

    @Schema(description = "举报类型")
    @JsonProperty("type")
    private String type;

    @Schema(description = "举报描述")
    @JsonProperty("reason")
    private String reason;

    @Schema(description = "被举报用户ID")
    @JsonProperty("user_id")
    private Long userId;

    @Schema(description = "举报时间")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
