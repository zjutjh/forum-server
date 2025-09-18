package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.jh.forum.common.annotation.NoExternalLink;
import org.jh.forum.common.constants.PostCategoryEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 发帖请求
 *
 * @author SugarMGP
 */
@Data
public class PublishPostRequest implements Serializable {
    @Schema(description = "帖子标题")
    @NotBlank
    @Size(max = 30)
    @NoExternalLink
    private String title;

    @Schema(description = "帖子内容")
    @NotBlank
    @Size(max = 1000)
    @NoExternalLink
    private String content;

    @Schema(description = "帖子板块")
    @NotNull
    private PostCategoryEnum category;

    @Schema(description = "帖子话题列表")
    @NotNull
    @Size(max = 10)
    private List<@NotBlank @Length(max = 30) @NoExternalLink String> topics;

    @Schema(description = "要绑定的图片url列表")
    @NotNull
    @Size(max = 9)
    private List<String> pictures;
}
