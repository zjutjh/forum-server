package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.HandleReportEnum;
import org.jh.forum.common.constants.ReportStatusEnum;
import org.jh.forum.common.constants.ReportTypeEnum;
import org.jh.forum.common.constants.TargetTypeEnum;
import org.jh.forum.common.dto.PictureInfoDTO;

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

    @Schema(description = "原贴ID 非评论类型时为null")
    private Long postId;

    @Schema(description = "评论位置 非评论类型时为null")
    private Integer commentPosition;

    @Schema(description = "举报类型")
    private ReportTypeEnum type;

    @Schema(description = "详细描述")
    private String reason;

    @Schema(description = "处理状态")
    private ReportStatusEnum status;

    @Schema(description = "处理结论")
    private String result;

    @Schema(description = "举报图片列表")
    private List<PictureInfoDTO> pictures;

    @Schema(description = "用户被举报历史统计")
    private UserHistoryStatsResponse userHistoryStats;

    @Schema(description = "是否删除原帖子/评论")
    private Boolean shouldDelete;

    @Schema(description = "处罚类型：no_punishment-无处罚，short_mute-短期禁言(1天)，" +
            "long_mute-长期禁言(7天)，custom_mute-自定义禁言时长，ban_account-封禁账号")
    private HandleReportEnum punishmentType;

    @Schema(description = "自定义禁言天数，仅当处罚类型为custom_mute时有效")
    private Integer muteDays;

    @Schema(description = "举报对象发布时间")
    private LocalDateTime targetTypeCreatedAt;
}
