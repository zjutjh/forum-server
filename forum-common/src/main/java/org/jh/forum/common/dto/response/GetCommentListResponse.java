package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotNull;

/**
 * @author qianqianzyk
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class GetCommentListResponse extends BaseListResponse<CommentElement> {
    @Schema(description = "高亮评论")
    @NotNull
    private CommentElement highlightComment;
}