package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jh.forum.common.constants.ReportTypeEnum;
import org.jh.forum.common.constants.TargetTypeEnum;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 举报内容请求
 *
 * @author zzb
 */
@Data
public class ReportContentRequest {
    @Schema(description = "举报对象类型")
    @NotNull
    private TargetTypeEnum target;

    @Schema(description = "举报类型 other-其他 pornography-色情低俗 cyberbullying-网络暴力 content_infringement-内容侵权 " +
            "illegal_activity-违法违规 politics_related-政治相关 troll_behavior-恶意引战 rumor_spreading-造谣传谣")
    @NotNull
    private ReportTypeEnum type;

    @Schema(description = "举报原因")
    @NotNull
    @Size(max = 500)
    private String reason;

    @Schema(description = "被举报的帖子/评论ID")
    @NotNull
    private Long targetId;

    @Schema(description = "图片URL列表")
    @NotNull
    @Size(max = 9)
    private List<String> pictures;
}
