package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.ReportStatusEnum;
import org.jh.forum.common.constants.ReportTypeEnum;
import org.jh.forum.common.constants.TargetTypeEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 获取举报列表元素
 *
 * @author zzb
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetReportListElement implements Serializable {
    @Schema(description = "举报ID")
    private Long id;

    @Schema(description = "处理状态 pending: 未处理, success: 举报成功, failure: 举报失败")
    private ReportStatusEnum status;

    @Schema(description = "举报目标类型 评论/帖子/用户")
    private TargetTypeEnum targetType;

    @Schema(description = "举报类型")
    private ReportTypeEnum type;

    @Schema(description = "举报描述")
    private String reason;

    @Schema(description = "被举报用户ID")
    private Long userId;

    @Schema(description = "被举报用户名称")
    private String targetNickname;

    @Schema(description = "举报时间")
    private LocalDateTime createdAt;
}
