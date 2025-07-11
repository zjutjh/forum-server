package org.jh.forum.common.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author qianqianzyk
 */
@Data
@Builder
public class MyCommentListElementDTO {
    private Long commentId;
    private String content;
    private String attachmentUrl;
    private LocalDateTime createAt;
    private Integer upvoteCount;
    private Integer replyCount;
}
