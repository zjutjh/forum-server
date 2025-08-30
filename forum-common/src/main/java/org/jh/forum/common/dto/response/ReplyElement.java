package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.dto.PictureInfoDTO;
import org.jh.forum.common.dto.UserInfoDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 回复列表元素
 *
 * @author qianqianzyk
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReplyElement {
    @Schema(description = "回复ID")
    private Long replyId;

    @Schema(description = "回复人信息")
    private UserInfoDTO publisherInfo;

    @Schema(description = "被回复的用户信息")
    private UserInfoDTO targetUser;

    @Schema(description = "回复内容")
    private String content;

    @Schema(description = "图片列表")
    private List<PictureInfoDTO> pictures;

    @Schema(description = "是否置顶")
    private Boolean isPinned;

    @Schema(description = "是否为帖主")
    private Boolean isAuthor;

    @Schema(description = "是否被删除")
    private Boolean isDeleted;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "点赞数")
    private Integer upvoteCount;

    @Schema(description = "回复数")
    private Integer replyCount;

    @Schema(description = "是否已点赞")
    private Boolean isLiked;
}