package org.jh.forum.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.ReportTypeEnum;
import org.jh.forum.common.constants.TargetTypeEnum;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zzb
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReportContentRequest {
    @Schema(description = "举报对象类型 post-帖子 comment-评论")
    @NotNull
    private TargetTypeEnum target;

    @Schema(description = "举报类型 other-其他 pornography-色情低俗 cyberbullying-网络暴力 content_infringement-内容侵权 " +
            "illegal_activity-违法违规 politics_related-政治相关 troll_behavior-恶意引战 rumor_spreading-造谣传谣")
    @NotNull
    private ReportTypeEnum type;

    @Schema(description = "举报原因")
    @NotNull
    private String reason;

    @Schema(description = "被举报的帖子/评论ID")
    @JsonProperty("target_id")
    @NotNull
    private Long targetId;

    @Schema(description = "要绑定的附件ID列表")
    @NotNull
    @JsonProperty("attachment_ids")
    private List<Long> attachmentIds;
}
