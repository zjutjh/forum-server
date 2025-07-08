package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.dto.request.PinCommentRequest;
import org.jh.forum.common.dto.request.PublishCommentRequest;
import org.jh.forum.common.dto.request.RemoveCommentRequest;
import org.jh.forum.common.dto.request.UpvoteCommentRequest;

/**
 * @author qianqianzyk
 */
public interface CommentService {
    Long publishComment(PublishCommentRequest request);

    Boolean upvoteComment(UpvoteCommentRequest request);

    Boolean pinComment(PinCommentRequest request);

    void removeComment(RemoveCommentRequest request);
}
