package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Collections;

/**
 * @author qianqianzyk
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class GetCommentListResponse extends BaseListResponse<CommentElement> {
    @Schema(description = "高亮评论")
    private CommentElement highlightComment;

    public static GetCommentListResponse emptyListResponse(int page, int pageSize) {
        return GetCommentListResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .total(0L)
                .list(Collections.emptyList())
                .highlightComment(null)
                .build();
    }
}