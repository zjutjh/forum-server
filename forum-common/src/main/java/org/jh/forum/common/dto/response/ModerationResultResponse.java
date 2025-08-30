package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 机器审核结果响应
 *
 * @author SugarMGP
 */
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModerationResultResponse {
    @Schema(description = "审核是否通过")
    private Boolean pass;

    @Schema(description = "风险标签列表")
    private List<Label> labels;

    @Schema(description = "审核请求ID（可用来追溯记录）")
    private String requestId;

    public static ModerationResultResponse success() {
        return new ModerationResultResponse(true, null, null);
    }

    public static ModerationResultResponse fail(String requestId, List<Label> labels) {
        return new ModerationResultResponse(false, labels, requestId);
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    public static class Label {
        @Schema(description = "标签名称")
        private String description;

        @Schema(description = "关键词列表，用逗号分隔（可能为 null）")
        private String keywords;
    }
}
