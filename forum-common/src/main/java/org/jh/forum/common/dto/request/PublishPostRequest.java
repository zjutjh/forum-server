package org.jh.forum.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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

    @Schema(description = "帖子板块ID")
    @JsonProperty("category_id")
    @NotNull
    private Long categoryId;

    @Schema(description = "帖子话题列表")
    @NotNull
    private String[] topics;
}
