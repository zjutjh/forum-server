package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author qianqianzyk
 */
@Data
public class PersonalCommentResponse {
    @Schema(description = "帖子ID")
    private Long postId;

    @Schema(description = "帖子标题")
    private String title;

    @Schema(description = "帖子正文")
    private String content;

    @Schema(description = "帖子图片")
    private String imageUrl;

    @Schema(description = "个人评论列表")
    private List<SimpleCommentResponse> personalCommentList;
}
