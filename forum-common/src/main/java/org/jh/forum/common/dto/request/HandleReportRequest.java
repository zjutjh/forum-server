package org.jh.forum.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class HandleReportRequest {
    @Schema(description = "举报ID")
    @JsonProperty("report_id")
    @NotNull
    private Long reportId;

    @Schema(description = "判定结果：1-举报成立，2-举报不成立")
    @NotNull
    @Min(1)
    @Max(2)
    private Integer status;

    @Schema(description = "是否删除原帖子/评论：1-删除，2-不删除")
    @NotNull
    @Min(1)
    @Max(2)
    private Integer delete;

    @Schema(description = "处罚类型：1-无处罚，2-短期禁言(1天)，3-长期禁言(7天)，4-自定义禁言时长，5-封禁账号",
            allowableValues = {"1", "2", "3", "4", "5"})
    @NotNull
    private Integer type;

    @Schema(description = "自定义禁言时长(小时)，仅当处罚类型为4时有效")
    private Integer hours;

    @Schema(description = "反馈信息")
    @NotBlank
    private String result;
}
