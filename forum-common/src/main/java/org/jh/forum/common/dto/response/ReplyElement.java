package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.dto.AttachmentInfoDTO;
import org.jh.forum.common.dto.UserInfoDTO;

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
public class ReplyElement {
    @Schema(description = "回复ID")
    @NotNull
    @JsonProperty("reply_id")
    private Long replyId;

    @Schema(description = "回复人信息")
    @JsonProperty("publisher_info")
    private UserInfoDTO publisherInfo;

    @Schema(description = "被回复的用户信息")
    @NotNull
    @JsonProperty("target_user")
    private UserInfoDTO targetUser;

    @Schema(description = "回复内容")
    @NotBlank
    private String content;

    @Schema(description = "附件列表")
    @NotBlank
    private List<AttachmentInfoDTO> attachments;

    @Schema(description = "是否置顶")
    @NotNull
    @JsonProperty("is_pinned")
    private Boolean isPinned;

    @Schema(description = "是否为帖主")
    @NotNull
    @JsonProperty("is_author")
    private Boolean isAuthor;

    @Schema(description = "是否被删除")
    @NotNull
    @JsonProperty("is_deleted")
    private Boolean isDeleted;

    @Schema(description = "创建时间")
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