package org.jh.forum.server.manger;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.TargetTypeEnum;
import org.jh.forum.common.entity.Comment;
import org.jh.forum.common.entity.Post;
import org.jh.forum.common.entity.Upvote;
import org.jh.forum.common.exceptions.ForumServiceException;
import org.jh.forum.server.mapper.CommentMapper;
import org.jh.forum.server.mapper.PostMapper;
import org.jh.forum.server.mapper.UpvoteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    private final FileManager fileManager;

    public Long publishComment(Long postId, Long parentId, Long targetId, String content, Long attachmentId) {
        // 检查 post_id 合法性
        Post post = postMapper.selectById(postId);
        if (post == null || post.getDeleted()) {
            throw new ForumServiceException(ExceptionEnum.INVALID_PARAMETER);
        }

        // 检查父评论链完整性检查
        if (parentId != 0) {
            Comment parentComment = commentMapper.selectById(parentId);
            if (parentComment == null || parentComment.getDeleted() || !parentComment.getPostId().equals(postId)) {
                throw new ForumServiceException(ExceptionEnum.INVALID_PARAMETER);
            }

            if (targetId != 0) {
                Comment targetComment = commentMapper.selectById(targetId);
                if (targetComment == null || targetComment.getDeleted()
                        || !targetComment.getPostId().equals(postId)
                        || !targetComment.getParentId().equals(parentId)) {
                    throw new ForumServiceException(ExceptionEnum.INVALID_PARAMETER);
                }
            }
        } else {
            if (targetId != 0) {
                throw new ForumServiceException(ExceptionEnum.INVALID_PARAMETER);
            }
        }

        // 创建评论
        Comment comment = Comment.builder()
                .userId(StpUtil.getLoginIdAsLong())
                .postId(postId)
                .parentId(parentId)
                .targetId(targetId)
                .content(content)
                .isPinned(false)
                .build();
        commentMapper.insert(comment);

        // 绑定附件关系
        fileManager.bindAttachment(attachmentId, TargetTypeEnum.COMMENT, comment.getId());

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
                .eq(Upvote::getUserId, StpUtil.getLoginIdAsLong()));

        // 如果表中无该记录，说明本次操作是在给评论点赞；否则点赞状态取反更新
        if (upvote == null) {
            upvote = Upvote.builder()
                    .userId(StpUtil.getLoginIdAsLong())
                    .postId(comment.getPostId())
                    .commentId(commentId)
                    .status(true)
                    .build();
            upvoteMapper.insert(upvote);
        } else {
            upvote.setStatus(!upvote.getStatus());
            upvoteMapper.updateById(upvote);
        }
        return upvote.getStatus();
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
            throw new ForumServiceException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }

        comment.setIsPinned(!comment.getIsPinned());
        commentMapper.updateById(comment);

        return comment.getIsPinned();
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeComment(Long commentId) {
        // 检查comment_id合法性及用户权限
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getDeleted()) {
            throw new ForumServiceException(ExceptionEnum.INVALID_PARAMETER);
        }
        if (!comment.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new ForumServiceException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }

        // 获取评论链
        List<Long> commentIds;
        if (comment.getParentId() == 0) {
            commentIds = commentMapper.selectList(
                    new LambdaQueryWrapper<Comment>()
                            .eq(Comment::getParentId, commentId)
                            .select(Comment::getId)
            ).stream().map(Comment::getId).collect(Collectors.toList());
            commentIds.add(commentId);
        } else {
            commentIds = commentMapper.getCommentIdsByTargetId(commentId);
        }

        // 删除评论
        commentMapper.delete(new LambdaQueryWrapper<Comment>().in(Comment::getId, commentIds));
        // 删除点赞记录
        upvoteMapper.delete(new LambdaQueryWrapper<Upvote>().in(Upvote::getCommentId, commentIds));

        // TODO 附件相关处理
    }
}