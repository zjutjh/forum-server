package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.dto.AttachmentInfoDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qianqianzyk
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MyCommentElement {
    @Schema(description = "帖子ID")
    @NotNull
    @JsonProperty("post_id")
    private Long postId;

    @Schema(description = "帖子标题")
    @NotBlank
    private String title;

    @Schema(description = "帖子正文（截取50字）")
    @NotBlank
    private String content;

    @Schema(description = "帖子附件列表")
    @NotBlank
    private List<AttachmentInfoDTO> attachments;

    @Schema(description = "帖子创建时间")
    @NotBlank
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "帖子更新时间")
    @NotBlank
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @Schema(description = "个人评论列表")
    @NotNull
    @JsonProperty("personal_comment_list")
    private List<MyCommentListElement> personalCommentList;
}