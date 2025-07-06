package org.jh.forum.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.jh.forum.common.constants.CategoryEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * @author SugarMGP
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PublishPostRequest {
    @Schema(description = "帖子标题")
    @NotBlank
    private String title;

    @Schema(description = "帖子内容")
    @NotBlank
    private String content;

    @Schema(description = "帖子板块")
    @NotNull
    private CategoryEnum category;

    @Schema(description = "帖子话题列表")
    @NotNull
    private List<@Length(min = 1, max = 30) String> topics;

    @Schema(description = "帖子附件ID列表")
    @NotNull
    @JsonProperty("attachment_ids")
    private List<Long> attachmentIds;
}
