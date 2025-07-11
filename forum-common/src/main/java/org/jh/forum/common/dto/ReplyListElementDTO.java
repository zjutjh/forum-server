package org.jh.forum.common.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author qianqianzyk
 */
@Data
@Builder
public class ReplyListElementDTO {
    private Long id;
    private UserInfoDTO userInfo;
    private String content;
    private String attachmentUrl;
    private Boolean isPinned;
    private Boolean isAuthor;
    private Boolean isDeleted;
    private String createAt;
    private Integer upvoteCount;
    private Integer replyCount;
    private Long targetUserId;
    private String targetNickname;
}
