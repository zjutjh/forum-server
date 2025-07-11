package org.jh.forum.server.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.CommentService;
import org.jh.forum.common.dto.CommentListElementDTO;
import org.jh.forum.common.dto.MyCommentElementDTO;
import org.jh.forum.common.dto.ReplyListElementDTO;
import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.entity.Comment;
import org.jh.forum.server.manger.CommentManager;

import jakarta.annotation.Resource;
import java.util.List;

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

    @Override
    public List<CommentListElementDTO> getCommentList(GetCommentListRequest request) {
        return commentManager.getCommentList(request.getPostId(), request.getPage(), request.getPageSize(), request.getSort());
    }

    @Override
    public CommentListElementDTO getHighlightCommentElement(GetCommentListRequest request) {
        if (request.getHighlightCommentId() == null || request.getHighlightCommentId() == 0) {
            return null;
        }
        Comment highlightComment = commentManager.getCommentById(request.getHighlightCommentId());
        if (highlightComment == null || highlightComment.getDeleted()) {
            return null;
        }
        if (highlightComment.getParentId() == 0) {
            return commentManager.convertCommentToElement(highlightComment, null);
        } else {
            Comment parentComment = commentManager.getCommentById(highlightComment.getParentId());
            if (parentComment == null || highlightComment.getDeleted()) {
                return null;
            }
            ReplyListElementDTO replyDto = commentManager.convertReplyToElement(highlightComment);
            return commentManager.convertCommentToElement(parentComment, replyDto);
        }
    }

    @Override
    public List<ReplyListElementDTO> getReplyList(GetReplyListRequest request) {
        return commentManager.getReplyList(request.getCommentId(), request.getPage(), request.getPageSize(), request.getSort());
    }

    @Override
    public List<MyCommentElementDTO> getMyCommentList() {
        return commentManager.getMyComment();
    }
}
