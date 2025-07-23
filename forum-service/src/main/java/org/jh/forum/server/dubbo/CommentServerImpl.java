package org.jh.forum.server.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.CommentService;
import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.server.manager.CommentManager;

import jakarta.annotation.Resource;

/**
 * @author qianqianzyk
 * @version 1.0
 */
@DubboService(version = "1.0.0")
@Slf4j
public class CommentServerImpl implements CommentService {
    @Resource
    private CommentManager commentManager;

    @Override
    public PublishCommentResponse publishComment(PublishCommentRequest request) {
        PublishCommentResponse response = commentManager.publishComment(request.getPostId(), request.getParentId(), request.getTargetId(), request.getContent(), request.getAttachmentId());

        // TODO 发送评论消息

        return response;
    }

    @Override
    public UpvoteCommentResponse upvoteComment(Long commentId) {
        UpvoteCommentResponse response = commentManager.upvoteComment(commentId);

        // TODO 发送点赞消息

        return response;
    }

    @Override
    public PinCommentResponse pinComment(Long commentId) {
        return commentManager.pinComment(commentId);
    }

    @Override
    public void removeComment(Long commentId) {
        commentManager.removeComment(commentId);
    }

    @Override
    public GetCommentListResponse getCommentList(GetCommentListRequest request) {
        return commentManager.getCommentList(request.getPostId(), request.getPage(), request.getPageSize(), request.getSortType(), request.getHighlightCommentId());
    }

    @Override
    public BaseListResponse<ReplyElement> getReplyList(GetReplyListRequest request) {
        return commentManager.getReplyList(request.getCommentId(), request.getPage(), request.getPageSize(), request.getSortType(), request.getExcludeCommentIds());
    }

    @Override
    public BaseListResponse<MyCommentElement> getMyCommentList(BaseListRequest request) {
        return commentManager.getMyComment(request.getPage(), request.getPageSize());
    }

    @Override
    public BaseListResponse<CommentElement> getAdminCommentList(GetCommentListAdminRequest request) {
        return commentManager.getAdminCommentList(request.getPostId(), request.getStatus(), request.getPage(), request.getPageSize());
    }

    @Override
    public BaseListResponse<ReplyElement> getAdminReplyList(GetReplyListAdminRequest request) {
        return commentManager.getAdminReplyList(request.getCommentId(), request.getPage(), request.getPageSize(), request.getStatus(), request.getExcludeCommentIds());
    }

    @Override
    public void adminChangeCommentStatus(ChangeCommentStatusRequest request) {
        commentManager.adminChangeCommentStatus(request.getCommentId(), request.getOperation());
    }
}
