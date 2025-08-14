package org.jh.forum.server.manager;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jh.forum.common.annotation.IgnoreLogicDelete;
import org.jh.forum.common.constants.*;
import org.jh.forum.common.dto.PictureInfoDTO;
import org.jh.forum.common.dto.UserInfoDTO;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.common.entity.Attachment;
import org.jh.forum.common.entity.Comment;
import org.jh.forum.common.entity.Post;
import org.jh.forum.common.entity.Upvote;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.mapper.AttachmentMapper;
import org.jh.forum.server.mapper.CommentMapper;
import org.jh.forum.server.mapper.PostMapper;
import org.jh.forum.server.mapper.UpvoteMapper;
import org.jh.forum.server.utils.AsyncUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    private final AttachmentMapper attachmentMapper;
    private final FileManager fileManager;
    private final UserManager userManager;
    private final PostRankManager postRankManager;
    private final NoticeManager noticeManager;
    private final PostManager postManager;

    @Transactional
    public void publishComment(Long postId, Long parentId, Long targetId, String content, String pictureUrl) {
        // 检查 post_id 合法性
        Post post = postManager.getPostOrThrow(postId);

        // 检查父评论链完整性检查
        Comment parentComment;
        Comment targetComment;
        if (parentId != 0) {
            parentComment = commentMapper.selectById(parentId);
            if (parentComment == null || !parentComment.getPostId().equals(postId)) {
                throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
            }

            if (targetId != 0) {
                targetComment = commentMapper.selectById(targetId);
                if (targetComment == null || !targetComment.getPostId().equals(postId)
                        || !targetComment.getParentId().equals(parentId)) {
                    throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
                }
            } else {
                targetComment = null;
            }
        } else {
            parentComment = null;
            targetComment = null;
            if (targetId != 0) {
                throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
            }
        }

        // 创建评论
        Comment comment = Comment.builder()
                .userId(StpUtil.getLoginIdAsLong())
                .postId(postId)
                .parentId(parentId)
                .targetId(targetId)
                .targetUserId(targetComment == null ? 0 : targetComment.getUserId())
                .content(content)
                .isPinned(false)
                .upvoteCount(0)
                .replyCount(0)
                .build();
        commentMapper.insert(comment);

        // 绑定附件关系
        if (StringUtils.isNotBlank(pictureUrl)) {
            fileManager.bindAttachment(pictureUrl, TargetTypeEnum.COMMENT, comment.getId());
        }

        AsyncUtil.runAsyncWithLogging(() -> {
            postRankManager.recordAction(postId, post.getCategory(), postRankManager.COMMENT);
            if (parentId != 0) {
                if (targetId != 0) {
                    noticeManager.createNotice(targetComment.getUserId(), NoticeTypeEnum.COMMENT, NoticePositionTypeEnum.COMMENT, targetId, comment.getId());
                } else {
                    noticeManager.createNotice(parentComment.getUserId(), NoticeTypeEnum.COMMENT, NoticePositionTypeEnum.COMMENT, parentId, comment.getId());
                }
            } else {
                noticeManager.createNotice(post.getUserId(), NoticeTypeEnum.COMMENT, NoticePositionTypeEnum.POST, postId, comment.getId());
            }
        });

        if (parentId != 0) {
            commentMapper.incrementReplyCount(parentId);
            if (targetId != 0) {
                commentMapper.incrementReplyCount(targetId);
            }
        }
    }

    public UpvoteCommentResponse upvoteComment(Long commentId) {
        Comment comment = getCommentOrThrow(commentId);
        Long userId = StpUtil.getLoginIdAsLong();

        Upvote upvote = upvoteMapper.selectOne(new LambdaQueryWrapper<Upvote>()
                .eq(Upvote::getCommentId, commentId)
                .eq(Upvote::getUserId, userId));

        // 如果表中无该记录，说明本次操作是在给评论点赞；否则点赞状态取反更新
        if (upvote == null) {
            upvote = Upvote.builder()
                    .userId(userId)
                    .commentId(commentId)
                    .status(true)
                    .build();
            upvoteMapper.insert(upvote);
            commentMapper.incrementUpvoteCount(commentId);
        } else {
            boolean newStatus = !upvote.getStatus();
            upvote.setStatus(newStatus);
            upvoteMapper.updateById(upvote);
            if (newStatus) {
                commentMapper.incrementUpvoteCount(commentId);
            } else {
                commentMapper.decrementUpvoteCount(commentId);
            }
        }

        Boolean status = upvote.getStatus();

        if (Boolean.TRUE.equals(status)) {
            AsyncUtil.runAsyncWithLogging(() ->
                    noticeManager.createNotice(comment.getUserId(), NoticeTypeEnum.LIKE, NoticePositionTypeEnum.COMMENT, commentId, null)
            );
        } else {
            AsyncUtil.runAsyncWithLogging(() ->
                    noticeManager.cancelLike(comment.getUserId(), NoticePositionTypeEnum.COMMENT, commentId)
            );
        }

        return new UpvoteCommentResponse(status);
    }

    public PinCommentResponse pinComment(Long commentId) {
        // 检查 comment_id 合法性
        Comment comment = getCommentOrThrow(commentId);

        // 检查用户权限(仅帖主进行置顶操作)
        Post post = postManager.getPostOrThrow(comment.getPostId());
        if (!post.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }

        if (comment.getParentId() != 0) {
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
        }

        // 一个帖子下仅允许置顶一个评论
        if (!comment.getIsPinned()) {
            LambdaQueryWrapper<Comment> pinnedCountWrapper = new LambdaQueryWrapper<>();
            pinnedCountWrapper.eq(Comment::getPostId, comment.getPostId())
                    .eq(Comment::getIsPinned, true);
            if (commentMapper.exists(pinnedCountWrapper)) {
                throw new ApiException(ExceptionEnum.COMMENT_PINNED_LIMIT_REACHED);
            }
        }

        // 更新评论置顶状态
        comment.setIsPinned(!comment.getIsPinned());
        commentMapper.updateById(comment);

        return PinCommentResponse.builder()
                .status(comment.getIsPinned())
                .build();
    }

    public void removeComment(Long commentId) {
        // 检查comment_id合法性及用户权限
        Comment comment = getCommentOrThrow(commentId);
        if (!comment.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }
        if (comment.getParentId() != 0) {
            commentMapper.decrementReplyCount(comment.getParentId());
            if (comment.getTargetId() != 0) {
                commentMapper.decrementReplyCount(comment.getTargetId());
            }
        }
        commentMapper.deleteById(commentId);
    }

    public BaseListResponse<CommentElement> getCommentList(Long postId, Integer page, Integer pageSize, String sort, Long highlightCommentId) {
        Post post = postManager.getPostOrThrow(postId);
        Long excludeId = null;
        if (highlightCommentId != null && highlightCommentId != 0) {
            Comment highlight = commentMapper.selectById(highlightCommentId);
            if (highlight != null) {
                if (highlight.getParentId() == 0) {
                    excludeId = highlightCommentId;
                } else {
                    excludeId = highlight.getParentId();
                }
            }
        }

        // 根据排序规则查询评论
        Page<Comment> commentPage = new Page<>(page, pageSize);
        QueryWrapper<Comment> wrapper = new QueryWrapper<Comment>()
                .eq("post_id", postId)
                .eq("parent_id", 0);

        if (excludeId != null) {
            if (page == 1) {
                wrapper.orderByAsc("CASE WHEN id = " + excludeId + " THEN 0 ELSE 1 END");
            } else {
                // 其他页排除高亮评论，防止重复
                wrapper.ne("id", excludeId);
            }
        }

        wrapper.orderByDesc("is_pinned");
        if ("hot".equals(sort)) {
            wrapper.orderByDesc("upvote_count + reply_count * 2");
        }
        wrapper.orderByAsc("created_at");
        commentMapper.selectPage(commentPage, wrapper);

        List<CommentElement> commentElements = commentPage.getRecords().stream()
                .map(comment -> buildCommentElement(comment, getHotReplyAsList(comment, post.getUserId()), post.getUserId()))
                .toList();
        return BaseListResponse.<CommentElement>builder()
                .page(page)
                .pageSize(pageSize)
                .total(commentPage.getTotal())
                .list(commentElements)
                .build();
    }

    private List<ReplyElement> getHotReplyAsList(Comment comment, Long postAuthorId) {
        List<Comment> hottestReply = commentMapper.selectList(new QueryWrapper<Comment>()
                .eq("parent_id", comment.getId())
                .orderByDesc("upvote_count + reply_count * 2")
                .orderByAsc("created_at")
                .last("limit 2"));
        return hottestReply.stream()
                .map(reply -> buildReplyElement(reply, postAuthorId))
                .toList();
    }

    public GetCommentReplyListResponse getReplyList(Long commentId, Integer page, Integer pageSize, String sort, Long highlightReplyId) {
        Comment parent = getCommentOrThrow(commentId);

        Long excludeId = null;
        if (highlightReplyId != null && highlightReplyId != 0) {
            Comment highlight = commentMapper.selectById(highlightReplyId);
            if (highlight != null) {
                if (Objects.equals(highlight.getParentId(), parent.getId())) {
                    excludeId = highlightReplyId;
                }
            }
        }

        Page<Comment> replyPage = new Page<>(page, pageSize);
        QueryWrapper<Comment> wrapper = new QueryWrapper<Comment>()
                .eq("parent_id", commentId);

        if (excludeId != null) {
            if (page == 1) {
                wrapper.orderByAsc("CASE WHEN id = " + excludeId + " THEN 0 ELSE 1 END");
            } else {
                // 其他页排除高亮回复，防止重复
                wrapper.ne("id", excludeId);
            }
        }
        if ("hot".equals(sort)) {
            wrapper.orderByDesc("upvote_count + reply_count * 2");
        }
        wrapper.orderByAsc("created_at");

        commentMapper.selectPage(replyPage, wrapper);
        Post post = postMapper.selectById(parent.getPostId());
        Long postAuthorId = post != null ? post.getUserId() : null;

        List<ReplyElement> replyElements = replyPage.getRecords().stream()
                .map(reply -> buildReplyElement(reply, postAuthorId))
                .toList();
        return GetCommentReplyListResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .total(replyPage.getTotal())
                .list(replyElements)
                .commentInfo(buildCommentInfo(parent, postAuthorId))
                .build();
    }

    public BaseListResponse<PersonalCommentListElement> getPersonalComment(Integer page, Integer pageSize) {
        Long realUserId = StpUtil.getLoginIdAsLong();

        // 按时间降序查询当前用户的评论
        Page<Comment> commentPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getUserId, realUserId)
                .orderByDesc(Comment::getCreatedAt);
        commentMapper.selectPage(commentPage, queryWrapper);
        List<PersonalCommentListElement> list = commentPage.getRecords().stream()
                .map(comment -> PersonalCommentListElement
                        .builder()
                        .postId(comment.getPostId())
                        .parentId(comment.getParentId())
                        .commentId(comment.getId())
                        .replyContent(getReplyContent(comment))
                        .content(comment.getContent())
                        .pictures(getCommentPictures(comment.getId()))
                        .createdAt(comment.getCreatedAt())
                        .upvoteCount(comment.getUpvoteCount())
                        .replyCount(comment.getReplyCount())
                        .build()
                ).toList();
        return BaseListResponse.<PersonalCommentListElement>builder()
                .page(page)
                .pageSize(pageSize)
                .total(commentPage.getTotal())
                .list(list)
                .build();
    }

    private String getReplyContent(Comment comment) {
        Long parentId = comment.getParentId();
        Long targetId = comment.getTargetId();

        // 如果是直接评论帖子
        if (parentId == 0) {
            Post post = postMapper.selectById(comment.getPostId());
            if (post != null) {
                return StringUtils.left(StringUtils.deleteWhitespace(post.getTitle() + post.getContent()), 30);
            }
        }
        // 如果是回复某个评论（优先 targetId，否则用 parentId）
        else {
            Long commentId = targetId != 0 ? targetId : parentId;
            Comment targetComment = commentMapper.selectById(commentId);
            if (targetComment != null) {
                return StringUtils.left(StringUtils.deleteWhitespace(targetComment.getContent()), 30);
            }
        }
        return "内容不存在";
    }

    @IgnoreLogicDelete
    public BaseListResponse<CommentElement> getAdminCommentList(Long postId, CommentStatusEnum status, Integer page, Integer pageSize) {
        // 根据状态筛选评论
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPostId, postId)
                .eq(Comment::getParentId, 0)
                .orderByDesc(Comment::getIsPinned)
                .orderByAsc(Comment::getCreatedAt);

        if (CommentStatusEnum.DELETED.equals(status)) {
            List<Long> allCommentIds = commentMapper.getDeletedOrHasDeletedReplyCommentIds(postId);
            if (allCommentIds.isEmpty()) {
                return BaseListResponse.emptyListResponse(page, pageSize);
            }

            wrapper.in(Comment::getId, allCommentIds);
        } else if (CommentStatusEnum.NORMAL.equals(status)) {
            wrapper.eq(Comment::getDeleted, false);
        }

        Page<Comment> commentPage = new Page<>(page, pageSize);
        Page<Comment> resultPage = commentMapper.selectPage(commentPage, wrapper);
        List<Comment> comments = resultPage.getRecords();

        Post post = postMapper.selectById(postId);
        Long postAuthorId = post != null ? post.getUserId() : null;

        List<CommentElement> list = new ArrayList<>();
        for (Comment comment : comments) {
            LambdaQueryWrapper<Comment> replyWrapper = new LambdaQueryWrapper<>();
            replyWrapper.eq(Comment::getParentId, comment.getId())
                    .orderByAsc(Comment::getCreatedAt)
                    .last("limit 5");
            if (CommentStatusEnum.DELETED.equals(status)) {
                replyWrapper.eq(Comment::getDeleted, true);
            } else if (CommentStatusEnum.NORMAL.equals(status)) {
                replyWrapper.eq(Comment::getDeleted, false);
            }
            List<Comment> replies = commentMapper.selectList(replyWrapper);
            List<ReplyElement> replyElements = replies.stream()
                    .map(reply -> buildReplyElement(reply, postAuthorId))
                    .toList();
            list.add(buildCommentElement(comment, replyElements, postAuthorId));
        }

        return BaseListResponse.<CommentElement>builder()
                .page(page)
                .pageSize(pageSize)
                .total(commentPage.getTotal())
                .list(list)
                .build();
    }

    @IgnoreLogicDelete
    public BaseListResponse<ReplyElement> getAdminReplyList(Long commentId, Integer page, Integer pageSize, CommentStatusEnum status) {
        Page<Comment> replyPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getParentId, commentId)
                .orderByAsc(Comment::getCreatedAt);
        if (CommentStatusEnum.DELETED.equals(status)) {
            wrapper.eq(Comment::getDeleted, true);
        } else if (CommentStatusEnum.NORMAL.equals(status)) {
            wrapper.eq(Comment::getDeleted, false);
        }

        commentMapper.selectPage(replyPage, wrapper);
        List<Comment> replies = replyPage.getRecords();
        if (replies.isEmpty()) {
            return BaseListResponse.emptyListResponse(page, pageSize);
        }
        Post post = postMapper.selectById(replies.get(0).getPostId());
        Long postAuthorId = post != null ? post.getUserId() : null;
        List<ReplyElement> replyElements = replies.stream()
                .map(reply -> buildReplyElement(reply, postAuthorId))
                .toList();
        return BaseListResponse.<ReplyElement>builder()
                .page(page)
                .pageSize(pageSize)
                .total(replyPage.getTotal())
                .list(replyElements)
                .build();
    }

    @IgnoreLogicDelete
    public void adminChangeCommentStatus(Long commentId, CommentOperationEnum operation) {
        Comment comment = getCommentOrThrow(commentId);

        if (comment.getParentId() != 0) {
            Comment parent = commentMapper.selectById(comment.getParentId());
            if (parent == null || parent.getDeleted()) {
                throw new ApiException(ExceptionEnum.PARENT_COMMENT_DELETED);
            }
        }

        if (CommentOperationEnum.DELETE.equals(operation)) {
            commentMapper.deleteById(commentId);
        } else {
            commentMapper.restoreComment(commentId);
        }
    }

    private ReplyElement buildReplyElement(Comment reply, Long postAuthorId) {
        UserInfoDTO targetUser = null;
        if (reply.getTargetUserId() != 0) {
            targetUser = userManager.getUserInfo(reply.getTargetUserId());
        }
        return ReplyElement.builder()
                .replyId(reply.getId())
                .publisherInfo(userManager.getUserInfo(reply.getUserId()))
                .targetUser(targetUser)
                .content(reply.getContent())
                .pictures(getCommentPictures(reply.getId()))
                .isPinned(reply.getIsPinned())
                .isAuthor(reply.getUserId().equals(postAuthorId))
                .isDeleted(reply.getDeleted())
                .createdAt(reply.getCreatedAt())
                .upvoteCount(reply.getUpvoteCount())
                .replyCount(reply.getReplyCount())
                .isLiked(checkIsLiked(reply.getId()))
                .build();
    }

    private CommentElement buildCommentElement(Comment comment, List<ReplyElement> replies, Long postAuthorId) {
        return CommentElement.builder()
                .commentId(comment.getId())
                .publisherInfo(userManager.getUserInfo(comment.getUserId()))
                .content(comment.getContent())
                .pictures(getCommentPictures(comment.getId()))
                .isPinned(comment.getIsPinned())
                .isAuthor(comment.getUserId().equals(postAuthorId))
                .isDeleted(comment.getDeleted())
                .createdAt(comment.getCreatedAt())
                .upvoteCount(comment.getUpvoteCount())
                .replyCount(comment.getReplyCount())
                .replies(replies)
                .isLiked(checkIsLiked(comment.getId()))
                .build();
    }

    private boolean checkIsLiked(Long commentId) {
        LambdaQueryWrapper<Upvote> queryWrapper = new LambdaQueryWrapper<Upvote>()
                .eq(Upvote::getUserId, StpUtil.getLoginIdAsLong())
                .eq(Upvote::getCommentId, commentId);
        Upvote upvote = upvoteMapper.selectOne(queryWrapper);
        return upvote != null && upvote.getStatus();
    }

    private CommentInfoResponse buildCommentInfo(Comment comment, Long postAuthorId) {
        return CommentInfoResponse.builder()
                .commentId(comment.getId())
                .publisherInfo(userManager.getUserInfo(comment.getUserId()))
                .content(comment.getContent())
                .pictures(getCommentPictures(comment.getId()))
                .isPinned(comment.getIsPinned())
                .isAuthor(comment.getUserId().equals(postAuthorId))
                .isDeleted(comment.getDeleted())
                .createdAt(comment.getCreatedAt())
                .upvoteCount(comment.getUpvoteCount())
                .replyCount(comment.getReplyCount())
                .isLiked(checkIsLiked(comment.getId()))
                .build();
    }

    private List<PictureInfoDTO> getCommentPictures(Long targetId) {
        return attachmentMapper.selectList(new LambdaQueryWrapper<Attachment>()
                .eq(Attachment::getType, AttachmentTypeEnum.PICTURE)
                .eq(Attachment::getTargetId, targetId)
                .eq(Attachment::getTargetType, TargetTypeEnum.COMMENT)
        ).stream().map(attachment -> PictureInfoDTO.builder()
                .url(fileManager.getFileUrl(attachment.getFileId()))
                .build()).toList();
    }

    public Comment getCommentOrThrow(Long id) {
        return Optional.ofNullable(commentMapper.selectById(id))
                .orElseThrow(() -> new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND));
    }
}