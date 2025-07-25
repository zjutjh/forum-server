package org.jh.forum.common.dto.response;

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
    private Long replyId;

    @Schema(description = "回复人信息")
    private UserInfoDTO publisherInfo;

    @Schema(description = "被回复的用户信息")
    @NotNull
    private UserInfoDTO targetUser;

    @Schema(description = "回复内容")
    @NotBlank
    private String content;

    @Schema(description = "附件列表")
    @NotBlank
    private List<AttachmentInfoDTO> attachments;

    @Schema(description = "是否置顶")
    @NotNull
    private Boolean isPinned;

    @Schema(description = "是否为帖主")
    @NotNull
    private Boolean isAuthor;

    @Schema(description = "是否被删除")
    @NotNull
    private Boolean isDeleted;

    @Schema(description = "创建时间")
    @NotBlank
    private LocalDateTime createdAt;

    @Schema(description = "点赞数")
    @NotNull
    private Integer upvoteCount;

    @Schema(description = "回复数")
    @NotNull
    private Integer replyCount;
}