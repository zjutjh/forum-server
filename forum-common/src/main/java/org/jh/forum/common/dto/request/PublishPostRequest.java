package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.jh.forum.common.constants.CategoryEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(max = 30)
    private String title;

    @Schema(description = "帖子内容")
    @NotBlank
    @Size(max = 1000)
    private String content;

    @Schema(description = "帖子板块")
    @NotNull
    private CategoryEnum category;

    @Schema(description = "帖子话题列表")
    @NotNull
    private List<@NotBlank @Length(max = 30) String> topics;

    @Schema(description = "要绑定的图片url列表")
    @NotNull
    @Size(max = 9)
    private List<String> pictures;
}
