package org.jh.forum.common.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qianqianzyk
 */
@Data
@Builder
public class CommentListElementDTO {
    private Long commentId;
    private String content;
    private LocalDateTime createdAt;
    private Integer upvoteCount;
    private Integer replyCount;
    private Boolean isPinned;
    private Boolean isAuthor;
    private Boolean isDeleted;
    private UserInfoDTO userInfo;
    private String attachmentUrl;
    private List<ReplyListElementDTO> replies;
} 