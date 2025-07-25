package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.ReportStatusEnum;
import org.jh.forum.common.constants.ReportTypeEnum;
import org.jh.forum.common.constants.TargetTypeEnum;
import org.jh.forum.common.dto.AttachmentInfoDTO;

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
    private Long userId;

    @Schema(description = "被举报人ID")
    private Long targetUserId;

    @Schema(description = "被举报用户名称")
    private String targetNickname;

    @Schema(description = "举报时间")
    private LocalDateTime createdAt;

    @Schema(description = "举报对象类型")
    private TargetTypeEnum targetType;

    @Schema(description = "举报对象ID")
    private Long targetId;

    @Schema(description = "举报类型")
    private ReportTypeEnum type;

    @Schema(description = "详细描述")
    private String reason;

    @Schema(description = "处理状态")
    private ReportStatusEnum status;

    @Schema(description = "处理结论")
    private String result;

    @Schema(description = "举报附件列表")
    private List<AttachmentInfoDTO> attachments;
}
