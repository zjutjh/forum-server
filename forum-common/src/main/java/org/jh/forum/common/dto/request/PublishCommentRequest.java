package org.jh.forum.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author qianqianzyk
 */
@Data
public class PublishCommentRequest {
    @Schema(description = "帖子ID")
    @NotNull
    @JsonProperty("post_id")
    private Long postId;

    @Schema(description = "父评论ID，即最顶层评论ID，无值即传0")
    @NotNull
    @JsonProperty("parent_id")
    private Long parentId;

    @Schema(description = "回复评论ID，无值即传0")
    @NotNull
    @JsonProperty("target_id")
    private Long targetId;

    @Schema(description = "评论内容，禁止发空评论")
    @NotBlank
    private String content;

    @Schema(description = "评论附件")
    @JsonProperty("attachment_url")
    @NotBlank
    private String attachmentUrl;

    @Schema(description = "@列表，，无值即传[]")
    @NotNull
    @JsonProperty("at_list")
    private Long[] atList;
}
