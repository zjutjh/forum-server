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
@DubboService
@Slf4j
public class CommentServerImpl implements CommentService {
    @Resource
    private CommentManager commentManager;

    @Override
    public void publishComment(PublishCommentRequest request) {
        commentManager.publishComment(request.getPostId(), request.getParentId(), request.getTargetId(), request.getContent(), request.getPicture());

        // TODO 发送评论消息
    }

    @Override
    public UpvoteCommentResponse upvoteComment(Long id) {
        UpvoteCommentResponse response = commentManager.upvoteComment(id);

        // TODO 发送点赞消息

        return response;
    }

    @Override
    public PinCommentResponse pinComment(Long id) {
        return commentManager.pinComment(id);
    }

    @Override
    public void removeComment(Long id) {
        commentManager.removeComment(id);
    }

    @Override
    public GetCommentListResponse getCommentList(GetCommentListRequest request) {
        return commentManager.getCommentList(request.getId(), request.getPage(), request.getPageSize(), request.getSortType(), request.getHighlightCommentId());
    }

    @Override
    public BaseListResponse<ReplyElement> getReplyList(GetReplyListRequest request) {
        return commentManager.getReplyList(request.getId(), request.getPage(), request.getPageSize(), request.getSortType(), request.getExcludeCommentIds());
    }

    @Override
    public BaseListResponse<PersonalCommentElement> getPersonalCommentList(GetPersonalCommentRequest request) {
        return commentManager.getPersonalComment(request.getPage(), request.getPageSize(), request.getId());
    }

    @Override
    public BaseListResponse<CommentElement> getAdminCommentList(GetCommentListAdminRequest request) {
        return commentManager.getAdminCommentList(request.getId(), request.getStatus(), request.getPage(), request.getPageSize());
    }

    @Override
    public BaseListResponse<ReplyElement> getAdminReplyList(GetReplyListAdminRequest request) {
        return commentManager.getAdminReplyList(request.getId(), request.getPage(), request.getPageSize(), request.getStatus(), request.getExcludeCommentIds());
    }

    @Override
    public void adminChangeCommentStatus(ChangeCommentStatusRequest request) {
        commentManager.adminChangeCommentStatus(request.getId(), request.getOperation());
    }
}
