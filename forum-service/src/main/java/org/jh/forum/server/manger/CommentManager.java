package org.jh.forum.server.manger;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.api.dubbo.PublishCommentReq;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.entity.Comment;
import org.jh.forum.common.entity.Post;
import org.jh.forum.common.entity.Upvote;
import org.jh.forum.common.exceptions.ForumServiceException;
import org.jh.forum.server.mapper.CommentMapper;
import org.jh.forum.server.mapper.PostMapper;
import org.jh.forum.server.mapper.UpvoteMapper;
import org.springframework.stereotype.Service;

/**
 * @author qianqianzyk
 * @date 2025/6/5
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CommentManager {
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final UpvoteMapper upvoteMapper;

    public Long publishComment(PublishCommentReq request) {
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
        } else {
            if (request.getTargetId() != 0) {
                throw new ForumServiceException(ExceptionEnum.INVALID_PARAMETER);
            }
        }

        Comment comment = Comment.builder()
                .userId(StpUtil.getLoginIdAsLong())
                .postId(request.getPostId())
                .parentId(request.getParentId())
                .targetId(request.getTargetId())
                .content(request.getContent())
                .isPinned(false)
                .build();

        commentMapper.insert(comment);

        return comment.getId();
    }

    public Boolean upvoteComment(Long commentId) {
        // 检查 comment_id 合法性
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getDeleted()) {
            throw new ForumServiceException(ExceptionEnum.INVALID_PARAMETER);
        }

        Upvote upvote = upvoteMapper.selectOne(new LambdaQueryWrapper<Upvote>()
                .eq(Upvote::getCommentId, commentId)
                .eq(Upvote::getUserId, StpUtil.getLoginIdAsLong())
                .last("LIMIT 1"));

        // 如果表中无该记录，说明本次操作是在给评论点赞；否则点赞状态取反更新
        if (upvote == null) {
            upvote = Upvote.builder()
                    .userId(StpUtil.getLoginIdAsLong())
                    .postId(comment.getPostId())
                    .commentId(commentId)
                    .status(true)
                    .build();

            upvoteMapper.insert(upvote);
            return upvote.getStatus();
        } else {
            upvote.setStatus(!upvote.getStatus());

            upvoteMapper.updateById(upvote);
            return upvote.getStatus();
        }
    }

    public Boolean pinComment(Long commentId) {
        // 检查 comment_id 合法性
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getDeleted()) {
            throw new ForumServiceException(ExceptionEnum.INVALID_PARAMETER);
        }

        // 检查用户权限(仅帖主进行置顶操作)
        Post post = postMapper.selectById(comment.getPostId());
        if (post == null || post.getDeleted()) {
            throw new ForumServiceException(ExceptionEnum.INVALID_PARAMETER);
        }
        if (!post.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new ForumServiceException(ExceptionEnum.PERMISSION_DENIED);
        }

        comment.setIsPinned(!comment.getIsPinned());
        commentMapper.updateById(comment);

        return comment.getIsPinned();
    }
}