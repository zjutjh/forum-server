package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.ReportTargetTypeEnum;
import org.jh.forum.common.constants.ReportTypeEnum;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zzb
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetReportDetailResponse {
    @Schema(description = "举报人ID")
    @JsonProperty("create_uid")
    private Long createUid;

    @Schema(description = "被举报人用户ID")
    @JsonProperty("user_id")
    private Long userId;

    @Schema(description = "被举报用户名称")
    @JsonProperty("nickname")
    private String nickname;

    @Schema(description = "举报时间")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "举报对象类型")
    @JsonProperty("target_type")
    private ReportTargetTypeEnum targetType;

    @Schema(description = "举报对象ID")
    @JsonProperty("target_id")
    private Long targetId;

    @Schema(description = "举报类型")
    private ReportTypeEnum type;

    @Schema(description = "详细描述")
    private String reason;

    @Schema(description = "附件图片列表")
    @JsonProperty("attach_images")
    private List<String> attachImages;

    @Schema(description = "处理状态")
    private String status;

    @Schema(description = "处理结论")
    private String result;
}
