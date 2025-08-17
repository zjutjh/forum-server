package org.jh.forum.common.dto.response;

import com.aliyun.green20220302.models.TextModerationPlusResponseBody;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
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

    public static ModerationResultResponse success() {
        return new ModerationResultResponse(true, null);
    }

    public static ModerationResultResponse fail(List<TextModerationPlusResponseBody.TextModerationPlusResponseBodyDataResult> results) {
        List<Label> labels = results.stream().map(result ->
                Label.builder()
                        .description(result.getDescription())
                        .keywords(result.getRiskWords())
                        .build()
        ).toList();
        return new ModerationResultResponse(false, labels);
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    static class Label {
        @Schema(description = "标签名称")
        private String description;

        @Schema(description = "关键词列表，用逗号分隔（可能为 null）")
        private String keywords;
    }
}
