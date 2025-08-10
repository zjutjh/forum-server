package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author SugarMGP
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class GetCommentReplyListResponse extends BaseListResponse<ReplyElement> {
    @Schema(description = "楼主评论")
    private CommentInfoResponse commentInfo;
}
