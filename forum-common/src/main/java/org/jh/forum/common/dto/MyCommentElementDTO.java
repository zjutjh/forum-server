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
public class MyCommentElementDTO {
    private Long postId;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private List<MyCommentListElementDTO> myCommentList;
}
