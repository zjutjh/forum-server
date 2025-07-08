package org.jh.forum.server.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.CommentService;
import org.jh.forum.common.dto.request.PinCommentRequest;
import org.jh.forum.common.dto.request.PublishCommentRequest;
import org.jh.forum.common.dto.request.RemoveCommentRequest;
import org.jh.forum.common.dto.request.UpvoteCommentRequest;
import org.jh.forum.server.manger.CommentManager;

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
    public Long publishComment(PublishCommentRequest request) {
        Long commentId = commentManager.publishComment(request.getPostId(), request.getParentId(), request.getTargetId(), request.getContent(), request.getAttachmentId());

        // TODO 发送评论消息

        return commentId;
    }

    @Override
    public Boolean upvoteComment(UpvoteCommentRequest request) {
        Boolean status = commentManager.upvoteComment(request.getCommentId());

        // TODO 发送点赞消息

        return status;
    }

    @Override
    public Boolean pinComment(PinCommentRequest request) {
        return commentManager.pinComment(request.getCommentId());
    }

    @Override
    public void removeComment(RemoveCommentRequest request) {
        commentManager.removeComment(request.getCommentId());
    }
}
