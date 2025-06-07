package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * @author qianqianzyk
 */
@Data
public class PersonalCommentResponse {
    @Schema(description = "帖子ID")
    @NotNull
    @JsonProperty("post_id")
    private Long postId;

    @Schema(description = "帖子标题")
    @NotBlank
    private String title;

    @Schema(description = "帖子正文")
    @NotBlank
    private String content;

    @Schema(description = "帖子图片")
    @NotBlank
    @JsonProperty("image_url")
    private String imageUrl;

    @Schema(description = "个人评论列表")
    @NotNull
    @JsonProperty("personal_comment_list")
    private List<SimpleCommentResponse> personalCommentList;
}
