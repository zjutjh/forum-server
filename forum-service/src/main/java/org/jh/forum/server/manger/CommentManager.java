package org.jh.forum.server.manger;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.TargetTypeEnum;
import org.jh.forum.common.dto.CommentListElementDTO;
import org.jh.forum.common.dto.ReplyListElementDTO;
import org.jh.forum.common.dto.UserInfoDTO;
import org.jh.forum.common.entity.*;
import org.jh.forum.common.exceptions.ForumServiceException;
import org.jh.forum.server.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final UserMapper userMapper;
    private final AttachmentMapper attachmentMapper;
    private final FileMapper fileMapper;

    public Comment getCommentById(Long id) {
        return commentMapper.selectById(id);
    }

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
                .upvoteCount(0)
                .replyCount(0)
                .build();
        commentMapper.insert(comment);

        // 绑定附件关系
        fileManager.bindAttachment(attachmentId, TargetTypeEnum.COMMENT, comment.getId());

        // 如果是回复，父评论 reply_count + 1
        if (parentId != 0) {
            Comment parentComment = commentMapper.selectById(parentId);
            if (parentComment != null) {
                int replyCount = parentComment.getReplyCount() == null ? 0 : parentComment.getReplyCount();
                parentComment.setReplyCount(replyCount + 1);
                commentMapper.updateById(parentComment);
            }
        }

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

            int upvoteCount = comment.getUpvoteCount() == null ? 0 : comment.getUpvoteCount();
            comment.setUpvoteCount(upvoteCount + 1);
            commentMapper.updateById(comment);
        } else {
            upvote.setStatus(!upvote.getStatus());
            upvoteMapper.updateById(upvote);
            int upvoteCount = comment.getUpvoteCount() == null ? 0 : comment.getUpvoteCount();
            if (upvote.getStatus()) {
                comment.setUpvoteCount(upvoteCount + 1);
            } else {
                comment.setUpvoteCount(Math.max(0, upvoteCount - 1));
            }
            commentMapper.updateById(comment);
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

        // 更新评论置顶状态
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

    public List<CommentListElementDTO> getCommentList(Long postId, Integer page, Integer pageSize, Integer sort) {
        // 按时间顺序查询置顶评论
        List<Comment> pinned = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getPostId, postId)
                        .eq(Comment::getParentId, 0)
                        .eq(Comment::getIsPinned, true)
                        .eq(Comment::getDeleted, false)
                        .orderByDesc(Comment::getUpdatedAt)
        );

        // 根据排序规则查询非置顶评论
        Page<Comment> commentPage = new Page<>(page, pageSize);
        QueryWrapper<Comment> wrapper = new QueryWrapper<Comment>()
                .eq("post_id", postId)
                .eq("parent_id", 0)
                .eq("is_pinned", false)
                .eq("deleted", false);
        if (sort == 1) {
            wrapper.orderByDesc("upvote_count + reply_count * 2");
        } else {
            wrapper.orderByDesc("updated_at");
        }
        List<Comment> normal = commentMapper.selectPage(commentPage, wrapper).getRecords();

        // 合并置顶和普通评论
        List<Comment> allComment = new ArrayList<>();
        allComment.addAll(pinned);
        allComment.addAll(normal);

        // 转DTO
        List<CommentListElementDTO> dto = new ArrayList<>();
        for (Comment c : allComment) {
            dto.add(convertCommentToElement(c, null));
        }
        return dto;
    }

    public List<ReplyListElementDTO> getReplyList(Long commentId, Integer page, Integer pageSize, Integer sort) {
        Page<Comment> pageParam = new Page<>(page, pageSize);
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", commentId)
                .eq("deleted", false);

        if (sort == 1) {
            wrapper.orderByDesc("upvote_count + reply_count * 2");
        } else {
            wrapper.orderByDesc("updated_at");
        }

        List<Comment> replys = commentMapper.selectPage(pageParam, wrapper).getRecords();

        List<ReplyListElementDTO> result = new ArrayList<>();
        for (Comment reply : replys) {
            result.add(convertReplyToElement(reply));
        }
        return result;
    }

    public CommentListElementDTO convertCommentToElement(Comment comment, ReplyListElementDTO reply) {
        User user = userMapper.selectById(comment.getUserId());
        UserInfoDTO userInfo = user == null ? null : UserInfoDTO.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
        String attachmentUrl = getAttachmentUrl(comment.getId());

        Post post = postMapper.selectById(comment.getPostId());
        boolean isAuthor = post != null && user != null && post.getUserId().equals(user.getId());

        List<ReplyListElementDTO> replys = new ArrayList<>();
        if (reply != null) {
            replys.add(reply);
        } else {
            QueryWrapper<Comment> wrapper = new QueryWrapper<>();
            wrapper.eq("parent_id", comment.getId())
                    .eq("deleted", false)
                    .orderByDesc("upvote_count + reply_count * 2")
                    .last("limit 1");
            Comment hottestReply = commentMapper.selectOne(wrapper);
            if (hottestReply != null) {
                replys.add(convertReplyToElement(hottestReply));
            }
        }

        return CommentListElementDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .isPinned(comment.getIsPinned())
                .isDeleted(comment.getDeleted())
                .upvoteCount(comment.getUpvoteCount())
                .replyCount(comment.getReplyCount())
                .userInfo(userInfo)
                .isAuthor(isAuthor)
                .attachmentUrl(attachmentUrl)
                .replys(replys)
                .build();
    }

    public ReplyListElementDTO convertReplyToElement(Comment reply) {
        User user = userMapper.selectById(reply.getUserId());
        UserInfoDTO userInfo = user == null ? null : UserInfoDTO.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
        String attachmentUrl = getAttachmentUrl(reply.getId());

        Post post = postMapper.selectById(reply.getPostId());
        boolean isAuthor = post != null && user != null && post.getUserId().equals(user.getId());

        User targetUser = reply.getTargetId() != null ? userMapper.selectById(reply.getTargetId()) : null;

        return ReplyListElementDTO.builder()
                .id(reply.getId())
                .userInfo(userInfo)
                .content(reply.getContent())
                .attachmentUrl(attachmentUrl)
                .isPinned(reply.getIsPinned())
                .isAuthor(isAuthor)
                .isDeleted(reply.getDeleted())
                .createAt(reply.getCreatedAt() != null ? reply.getCreatedAt().toString() : "")
                .upvoteCount(reply.getUpvoteCount())
                .replyCount(reply.getReplyCount())
                .targetUserId(targetUser != null ? targetUser.getId() : null)
                .targetNickname(targetUser != null ? targetUser.getNickname() : "")
                .build();
    }

    private String getAttachmentUrl(Long commentId) {
        List<Attachment> attachments = attachmentMapper.selectList(
                new LambdaQueryWrapper<Attachment>()
                        .eq(Attachment::getTargetId, commentId)
                        .eq(Attachment::getTargetType, TargetTypeEnum.COMMENT)
                        .eq(Attachment::getDeleted, false)
        );
        if (!attachments.isEmpty()) {
            File file = fileMapper.selectById(attachments.get(0).getFileId());
            if (file != null) {
                return file.getObjectKey();
            }
        }
        return "";
    }
}