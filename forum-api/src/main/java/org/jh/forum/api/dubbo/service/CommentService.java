package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;

/**
 * @author qianqianzyk
 */
public interface CommentService {
    void publishComment(PublishCommentRequest request);

    UpvoteCommentResponse upvoteComment(Long id);

    PinCommentResponse pinComment(Long id);

    void removeComment(Long id);

    BaseListResponse<CommentElement> getCommentList(GetCommentListRequest request);

    GetCommentReplyListResponse getReplyList(GetReplyListRequest request);

    BaseListResponse<PersonalCommentListElement> getPersonalCommentList(BaseListRequest request);

    BaseListResponse<CommentElement> getAdminCommentList(GetCommentListAdminRequest request);

    BaseListResponse<ReplyElement> getAdminReplyList(GetReplyListAdminRequest request);

    void adminChangeCommentStatus(ChangeCommentStatusRequest request);
}
