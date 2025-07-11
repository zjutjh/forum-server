package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.dto.CommentListElementDTO;
import org.jh.forum.common.dto.ReplyListElementDTO;
import org.jh.forum.common.dto.request.*;

import java.util.List;

/**
 * @author qianqianzyk
 */
public interface CommentService {
    Long publishComment(PublishCommentRequest request);

    Boolean upvoteComment(UpvoteCommentRequest request);

    Boolean pinComment(PinCommentRequest request);

    void removeComment(RemoveCommentRequest request);

    List<CommentListElementDTO> getCommentList(GetCommentListRequest request);

    CommentListElementDTO getHighlightCommentElement(GetCommentListRequest request);

    List<ReplyListElementDTO> getReplyList(GetReplyListRequest request);
}
