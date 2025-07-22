package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;

/**
 * @author qianqianzyk
 */
public interface CommentService {
    PublishCommentResponse publishComment(PublishCommentRequest request);

    UpvoteCommentResponse upvoteComment(Long commentId);

    PinCommentResponse pinComment(Long commentId);

    void removeComment(Long commentId);

    GetCommentListResponse getCommentList(GetCommentListRequest request);

    BaseListResponse<ReplyElement> getReplyList(GetReplyListRequest request);

    BaseListResponse<MyCommentElement> getMyCommentList(BaseListRequest request);

    BaseListResponse<CommentElement> getAdminCommentList(GetCommentListAdminRequest request);

    BaseListResponse<ReplyElement> getAdminReplyList(GetReplyListAdminRequest request);

    void adminChangeCommentStatus(ChangeCommentStatusRequest request);
}
