package org.jh.forum.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zzb
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReportContentRequest {
    @Schema(description = "举报对象类型 1-帖子 2-评论")
    @NotNull
    @Min(1)
    @Max(2)
    private Integer target;

    @Schema(description = "举报类型 1-其他 2-色情低俗 3-网络暴力 4-内容侵权 5-违法违规 6-政治相关 7-恶意引战 8-造谣传谣",
            allowableValues = {"1", "2", "3", "4", "5", "6", "7", "8"})
    @NotNull
    private Integer type;

    @Schema(description = "举报详情")
    private String reason;

    @Schema(description = "被举报的帖子/评论ID")
    @JsonProperty("target_id")
    @NotNull
    private Long targetId;
}
