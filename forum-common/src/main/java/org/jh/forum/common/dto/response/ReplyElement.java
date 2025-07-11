package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.jh.forum.common.dto.UserInfoDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author qianqianzyk
 */
@Data
@Builder
public class ReplyElement {
    @Schema(description = "回复ID")
    @NotNull
    @JsonProperty("id")
    private Long id;

    @Schema(description = "回复人信息")
    @JsonProperty("publisher_info")
    private UserInfoDTO publisherInfo;

    @Schema(description = "回复内容")
    @NotBlank
    private String content;

    @Schema(description = "附件链接")
    @NotBlank
    @JsonProperty("attachment_url")
    private String attachmentUrl;

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
    @JsonProperty("create_at")
    private String createAt;

    @Schema(description = "点赞数")
    @NotNull
    @JsonProperty("upvote_count")
    private Integer upvoteCount;

    @Schema(description = "回复数")
    @NotNull
    @JsonProperty("reply_count")
    private Integer replyCount;

    @Schema(description = "被回复的用户ID")
    @NotNull
    @JsonProperty("target_user_id")
    private Long targetUserId;

    @Schema(description = "被回复的用户昵称")
    @NotBlank
    @JsonProperty("target_nickname")
    private String targetNickname;
}