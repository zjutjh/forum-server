package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author qianqianzyk
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetCommentListResponse extends BaseListResponse<CommentResponse>{
    @Schema(description = "高亮评论")
    private CommentResponse highlightComment;
}