package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.HandleReportEnum;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * @author zzb
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HandleReportRequest {
    @Schema(description = "举报ID")
    @NotNull
    private Long reportId;

    @Schema(description = "判定结果：success-举报成立，failure-举报不成立")
    @NotNull
    private String status;

    @Schema(description = "是否删除原帖子/评论")
    @NotNull
    private Boolean shouldDelete;

    @Schema(description = "处罚类型：no_punishment-无处罚，short_mute-短期禁言(1天)，" +
            "long_mute-长期禁言(7天)，custom_mute-自定义禁言时长")
    @NotNull
    private HandleReportEnum type;

    @Schema(description = "自定义禁言天数，仅当处罚类型为custom_mute时有效")
    @Min(1)
    private Integer days;

    @Schema(description = "反馈信息")
    @NotBlank
    @Size(max = 300)
    private String result;
}
