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
public class PersonalCommentListElement {
    @Schema(description = "评论ID")
    @NotNull
    @JsonProperty("comment_id")
    private Long commentId;

    @Schema(description = "评论内容")
    @NotBlank
    private String content;

    @Schema(description = "附件链接")
    @NotBlank
    private List<AttachmentInfoDTO> attachments;

    @Schema(description = "发表时间")
    @NotBlank
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Schema(description = "点赞数")
    @NotNull
    @JsonProperty("upvote_count")
    private Integer upvoteCount;

    @Schema(description = "回复数")
    @NotNull
    @JsonProperty("reply_count")
    private Integer replyCount;
}
