package org.jh.forum.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.ReportTypeEnum;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zzb
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReportUserRequest {
    @Schema(description = "举报类型 other-其他 speech_violation-言论违规 personal_info_violation-个人信息违规")
    @NotNull
    private ReportTypeEnum type;

    @Schema(description = "举报内容 选择其他时填写")
    private String reason;

    @Schema(description = "被举报的用户ID")
    @JsonProperty("user_id")
    @NotNull
    private Long userId;

    @Schema(description = "要绑定的附件ID列表")
    @NotNull
    @JsonProperty("attachment_ids")
    private List<Long> attachmentIds;
}
