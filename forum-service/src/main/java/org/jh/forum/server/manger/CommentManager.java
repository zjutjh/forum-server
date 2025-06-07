package org.jh.forum.server.manger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.api.dubbo.PublishCommentReq;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.entity.Comment;
import org.jh.forum.common.entity.Post;
import org.jh.forum.common.entity.mapper.CommentMapper;
import org.jh.forum.common.entity.mapper.PostMapper;
import org.jh.forum.common.exceptions.ForumServiceException;
import org.springframework.stereotype.Service;

/**
 * @author qianqianzyk
 * @date 2025/6/5
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class CommentManager {
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    public Long publishComment(PublishCommentReq request, Long userId) {
        // 检查 post_id 合法性
        Post post = postMapper.selectById(request.getPostId());
        if (post == null || post.getDeleted()) {
            throw new ForumServiceException(ExceptionEnum.INVALID_PARAMETER);
        }

        // 检查父评论链完整性检查
        if (request.getParentId() != 0) {
            Comment parentComment = commentMapper.selectById(request.getParentId());
            if (parentComment == null || parentComment.getDeleted() || !parentComment.getPostId().equals(request.getPostId())) {
                throw new ForumServiceException(ExceptionEnum.INVALID_PARAMETER);
            }

            if (request.getTargetId() != 0) {
                Comment targetComment = commentMapper.selectById(request.getTargetId());
                if (targetComment == null || targetComment.getDeleted()
                        || !targetComment.getPostId().equals(request.getPostId())
                        || !targetComment.getParentId().equals(request.getParentId())) {
                    throw new ForumServiceException(ExceptionEnum.INVALID_PARAMETER);
                }
            }
        }

        // 准备评论实体
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setPostId(request.getPostId());
        comment.setParentId(request.getParentId());
        comment.setTargetId(request.getTargetId());
        comment.setContent(request.getContent());
        comment.setIsPinned(false);

        // 创建评论记录
        commentMapper.insert(comment);

        // 返回评论id
        return comment.getId();
    }
}