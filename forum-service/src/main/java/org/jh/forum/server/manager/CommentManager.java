package org.jh.forum.server.manager;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.annotation.IgnoreLogicDelete;
import org.jh.forum.common.constants.CommentStatusEnum;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.TargetTypeEnum;
import org.jh.forum.common.dto.AttachmentInfoDTO;
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
import org.springframework.stereotype.Service;

import java.util.*;
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
    private final AttachmentMapper attachmentMapper;
    private final FileManager fileManager;
    private final UserManager userManager;

    public PublishCommentResponse publishComment(Long postId, Long parentId, Long targetId, String content, Long attachmentId) {
        // 检查 post_id 合法性
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }

        Comment parentComment = null;
        Comment targetComment = null;

        // 检查父评论链完整性检查
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
            }
        } else {
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
                .content(content)
                .isPinned(false)
                .upvoteCount(0)
                .replyCount(0)
                .build();
        commentMapper.insert(comment);

        // 绑定附件关系
        if (attachmentId != null && attachmentId != 0) {
            fileManager.bindAttachment(attachmentId, TargetTypeEnum.COMMENT, comment.getId());
        }

        if (parentId != 0) {
            parentComment.setReplyCount(parentComment.getReplyCount() + 1);
            commentMapper.updateById(parentComment);

            if (targetId != 0) {
                targetComment.setReplyCount(targetComment.getReplyCount() + 1);
                commentMapper.updateById(targetComment);
            }
        }

        return PublishCommentResponse.builder()
                .commentId(comment.getId())
                .build();
    }

    public UpvoteCommentResponse upvoteComment(Long commentId) {
        // 检查 comment_id 合法性
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        Long userId = StpUtil.getLoginIdAsLong();

        Upvote upvote = upvoteMapper.selectOne(new LambdaQueryWrapper<Upvote>()
                .eq(Upvote::getCommentId, commentId)
                .eq(Upvote::getUserId, userId));

        // 如果表中无该记录，说明本次操作是在给评论点赞；否则点赞状态取反更新
        if (upvote == null) {
            upvote = Upvote.builder()
                    .userId(userId)
                    .postId(comment.getPostId())
                    .commentId(commentId)
                    .status(true)
                    .build();
            upvoteMapper.insert(upvote);
            comment.setUpvoteCount(comment.getUpvoteCount() + 1);
            commentMapper.updateById(comment);
        } else {
            upvote.setStatus(!upvote.getStatus());
            upvoteMapper.updateById(upvote);
            int upvoteCount = comment.getUpvoteCount();
            if (upvote.getStatus()) {
                comment.setUpvoteCount(upvoteCount + 1);
            } else {
                comment.setUpvoteCount(Math.max(0, upvoteCount - 1));
            }
            commentMapper.updateById(comment);
        }

        return UpvoteCommentResponse.builder()
                .status(upvote.getStatus())
                .build();
    }

    public PinCommentResponse pinComment(Long commentId) {
        // 检查 comment_id 合法性
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }

        // 检查用户权限(仅帖主进行置顶操作)
        Post post = postMapper.selectById(comment.getPostId());
        if (post == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        if (!post.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }

        if (comment.getParentId() != 0) {
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
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
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        if (!comment.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
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
        if (!commentIds.isEmpty()) {
            commentMapper.delete(new LambdaQueryWrapper<Comment>()
                    .in(Comment::getId, commentIds));
        }
    }

    public GetCommentListResponse getCommentList(Long postId, Integer page, Integer pageSize, Integer sort, Long highlightCommentId) {
        // 按时间顺序查询置顶评论
        List<Comment> pinned = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getPostId, postId)
                        .eq(Comment::getParentId, 0)
                        .eq(Comment::getIsPinned, true)
                        .orderByDesc(Comment::getCreatedAt)
        );

        Long excludeId = null;
        Comment highlight = null;
        if (highlightCommentId != null && highlightCommentId != 0) {
            highlight = commentMapper.selectById(highlightCommentId);
            if (highlight != null) {
                if (highlight.getParentId() == 0) {
                    excludeId = highlightCommentId;
                } else {
                    excludeId = highlight.getParentId();
                }
            }
        }

        // 根据排序规则查询非置顶评论
        Page<Comment> commentPage = new Page<>(page, pageSize);
        QueryWrapper<Comment> wrapper = new QueryWrapper<Comment>()
                .eq("post_id", postId)
                .eq("parent_id", 0)
                .eq("is_pinned", false);
        if (excludeId != null) {
            wrapper.ne("id", excludeId);
        }
        if (sort == 1) {
            wrapper.orderByDesc("upvote_count + reply_count * 2")
                    .orderByDesc("created_at");
        } else {
            wrapper.orderByDesc("created_at");
        }
        List<Comment> normal = commentMapper.selectPage(commentPage, wrapper).getRecords();

        // 合并置顶和普通评论
        List<Comment> allComments = new ArrayList<>();
        allComments.addAll(pinned);
        allComments.addAll(normal);

        if (allComments.isEmpty()) {
            return GetCommentListResponse.builder()
                    .page(page)
                    .pageSize(pageSize)
                    .total(0L)
                    .list(Collections.emptyList())
                    .highlightComment(null)
                    .build();
        }

        Post post = postMapper.selectById(postId);
        Long postAuthorId = post != null ? post.getUserId() : null;

        List<CommentElement> commentElements = new ArrayList<>();
        for (Comment comment : allComments) {
            List<Comment> hottestReplyList = commentMapper.selectList(new QueryWrapper<Comment>()
                    .eq("parent_id", comment.getId())
                    .orderByDesc("upvote_count + reply_count * 2")
                    .orderByDesc("created_at")
                    .last("limit 1"));

            List<ReplyElement> replyElements = new ArrayList<>();
            if (!hottestReplyList.isEmpty()) {
                Comment hottestReply = hottestReplyList.get(0);
                replyElements.add(buildReplyElement(hottestReply, postAuthorId));
            }

            commentElements.add(buildCommentElement(comment, replyElements, postAuthorId));
        }

        return GetCommentListResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .total(commentPage.getTotal() + pinned.size())
                .list(commentElements)
                .highlightComment(highlight != null ? getHighlightCommentElement(highlight) : null)
                .build();
    }

    public CommentElement getHighlightCommentElement(Comment highlight) {
        Post post = postMapper.selectById(highlight.getPostId());
        Long postAuthorId = post != null ? post.getUserId() : null;

        if (highlight.getParentId() == 0) {
            List<Comment> replyList = commentMapper.selectList(new QueryWrapper<Comment>()
                    .eq("parent_id", highlight.getId())
                    .orderByDesc("upvote_count + reply_count * 2")
                    .orderByDesc("created_at")
                    .last("limit 1"));

            List<ReplyElement> replies = new ArrayList<>();
            if (!replyList.isEmpty()) {
                Comment hottestReply = replyList.get(0);
                replies.add(buildReplyElement(hottestReply, postAuthorId));
            }

            return buildCommentElement(highlight, replies, postAuthorId);
        } else {
            Comment parent = commentMapper.selectById(highlight.getParentId());
            if (parent == null) {
                return null;
            }

            ReplyElement replyElement = buildReplyElement(highlight, postAuthorId);
            return buildCommentElement(parent, Collections.singletonList(replyElement), postAuthorId);
        }
    }

    public BaseListResponse<ReplyElement> getReplyList(Long commentId, Integer page, Integer pageSize, Integer sort, Long[] excludeCommentIds) {
        Page<Comment> pageParam = new Page<>(page, pageSize);
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", commentId);
        if (excludeCommentIds != null && excludeCommentIds.length > 0) {
            wrapper.notIn("id", Arrays.asList(excludeCommentIds));
        }
        if (sort == 1) {
            wrapper.orderByDesc("upvote_count + reply_count * 2")
                    .orderByDesc("created_at");
        } else {
            wrapper.orderByDesc("created_at");
        }
        Page<Comment> replyPage = commentMapper.selectPage(pageParam, wrapper);
        List<Comment> replies = replyPage.getRecords();

        if (replies.isEmpty()) {
            return BaseListResponse.<ReplyElement>builder()
                    .page(page)
                    .pageSize(pageSize)
                    .total(0L)
                    .list(Collections.emptyList())
                    .build();
        }

        Long postId = replies.get(0).getPostId();
        Post post = postMapper.selectById(postId);
        Long postAuthorId = post != null ? post.getUserId() : null;

        List<ReplyElement> replyElements = new ArrayList<>();
        for (Comment reply : replies) {
            replyElements.add(buildReplyElement(reply, postAuthorId));
        }

        return BaseListResponse.<ReplyElement>builder()
                .page(page)
                .pageSize(pageSize)
                .total(replyPage.getTotal())
                .list(replyElements)
                .build();
    }

    public BaseListResponse<MyCommentElement> getMyComment(Integer page, Integer pageSize) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 按时间降序查询当前用户的评论
        Page<Comment> pageParam = new Page<>(page, pageSize);
        Page<Comment> commentPage = commentMapper.selectPage(
                pageParam,
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getUserId, userId)
                        .orderByDesc(Comment::getCreatedAt)
        );
        List<Comment> commentList = commentPage.getRecords();
        if (commentList.isEmpty()) {
            return BaseListResponse.<MyCommentElement>builder()
                    .page(page)
                    .pageSize(pageSize)
                    .total(0L)
                    .list(Collections.emptyList())
                    .build();
        }

        // 查询所有相关帖子的详情
        Set<Long> postIds = commentList.stream()
                .map(Comment::getPostId)
                .collect(Collectors.toSet());
        if (postIds.isEmpty()) {
            return BaseListResponse.<MyCommentElement>builder()
                    .page(page)
                    .pageSize(pageSize)
                    .total(0L)
                    .list(Collections.emptyList())
                    .build();
        }

        List<Post> posts = postMapper.selectList(
                new LambdaQueryWrapper<Post>()
                        .in(Post::getId, postIds)
        );
        Map<Long, Post> postMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, p -> p));

        List<MyCommentElement> resultList = commentList.stream().map(comment -> {
            Post post = postMap.get(comment.getPostId());
            if (post == null) {
                return null;
            }

            return MyCommentElement.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .content(truncateContent(post.getContent()))
                    .attachments(getAttachments(post.getId(), TargetTypeEnum.POST))
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .personalCommentList(Collections.singletonList(
                            MyCommentListElement.builder()
                                    .commentId(comment.getId())
                                    .content(comment.getContent())
                                    .attachments(getAttachments(comment.getId(), TargetTypeEnum.COMMENT))
                                    .createdAt(comment.getCreatedAt())
                                    .upvoteCount(comment.getUpvoteCount())
                                    .replyCount(comment.getReplyCount())
                                    .build()
                    ))
                    .build();
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return BaseListResponse.<MyCommentElement>builder()
                .page(page)
                .pageSize(pageSize)
                .total(commentPage.getTotal())
                .list(resultList)
                .build();
    }

    @IgnoreLogicDelete
    public BaseListResponse<CommentElement> getAdminCommentList(Long postId, CommentStatusEnum status, Integer page, Integer pageSize) {
        // 根据状态筛选评论
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPostId, postId)
                .eq(Comment::getParentId, 0)
                .orderByDesc(Comment::getCreatedAt);
        if (status == CommentStatusEnum.DELETED) {
            wrapper.eq(Comment::getDeleted, true);
        } else if (status == CommentStatusEnum.NORMAL) {
            wrapper.eq(Comment::getDeleted, false);
        }

        Page<Comment> commentPage = new Page<>(page, pageSize);
        Page<Comment> resultPage = commentMapper.selectPage(commentPage, wrapper);
        List<Comment> comments = resultPage.getRecords();

        Post post = postMapper.selectById(postId);
        Long postAuthorId = post != null ? post.getUserId() : null;

        List<CommentElement> list = new ArrayList<>();
        for (Comment comment : comments) {
            // 查询评论的前 5 条回复
            LambdaQueryWrapper<Comment> replyWrapper = new LambdaQueryWrapper<>();
            replyWrapper.eq(Comment::getParentId, comment.getId())
                    .orderByDesc(Comment::getCreatedAt)
                    .last("limit 5");
            if (status == CommentStatusEnum.DELETED) {
                replyWrapper.eq(Comment::getDeleted, true);
            } else if (status == CommentStatusEnum.NORMAL) {
                replyWrapper.eq(Comment::getDeleted, false);
            }
            List<Comment> replies = commentMapper.selectList(replyWrapper);

            List<ReplyElement> replyElements = new ArrayList<>();
            for (Comment reply : replies) {
                replyElements.add(buildReplyElement(reply, postAuthorId));
            }

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
    public BaseListResponse<ReplyElement> getAdminReplyList(Long commentId, Integer page, Integer pageSize, CommentStatusEnum status, Long[] excludeCommentIds) {
        Page<Comment> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getParentId, commentId)
                .orderByDesc(Comment::getCreatedAt);
        if (excludeCommentIds != null && excludeCommentIds.length > 0) {
            wrapper.notIn(Comment::getId, Arrays.asList(excludeCommentIds));
        }
        if (status == CommentStatusEnum.DELETED) {
            wrapper.eq(Comment::getDeleted, true);
        } else if (status == CommentStatusEnum.NORMAL) {
            wrapper.eq(Comment::getDeleted, false);
        }

        Page<Comment> replyPage = commentMapper.selectPage(pageParam, wrapper);
        List<Comment> replies = replyPage.getRecords();
        if (replies.isEmpty()) {
            return BaseListResponse.<ReplyElement>builder()
                    .page(page)
                    .pageSize(pageSize)
                    .total(0L)
                    .list(Collections.emptyList())
                    .build();
        }

        Long postId = replies.get(0).getPostId();
        Post post = postMapper.selectById(postId);
        Long postAuthorId = post != null ? post.getUserId() : null;

        List<ReplyElement> replyElements = new ArrayList<>();
        for (Comment reply : replies) {
            replyElements.add(buildReplyElement(reply, postAuthorId));
        }

        return BaseListResponse.<ReplyElement>builder()
                .page(page)
                .pageSize(pageSize)
                .total(replyPage.getTotal())
                .list(replyElements)
                .build();
    }

    @IgnoreLogicDelete
    public void adminChangeCommentStatus(Long commentId, Integer status) {
        Comment comment = commentMapper.selectOne(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getId, commentId)
        );
        if (comment == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }

        List<Long> commentIds;
        if (comment.getParentId() == 0) {
            commentIds = commentMapper.selectList(
                    new LambdaQueryWrapper<Comment>()
                            .eq(Comment::getParentId, commentId)
                            .select(Comment::getId)
            ).stream().map(Comment::getId).collect(Collectors.toList());
            commentIds.add(commentId);
        } else {
            commentIds = commentMapper.getAllCommentIdsByTargetId(commentId);
        }
        if (commentIds.isEmpty()) {
            return;
        }

        if (status == 1) {
            commentMapper.delete(new LambdaQueryWrapper<Comment>()
                    .in(Comment::getId, commentIds));
        } else if (status == 2) {
            commentMapper.restoreComments(commentIds);
        }
    }

    private ReplyElement buildReplyElement(Comment reply, Long postAuthorId) {
        UserInfoDTO targetUser = null;
        if (reply.getTargetId() != 0) {
            Comment targetComment = commentMapper.selectById(reply.getTargetId());
            if (targetComment != null) {
                targetUser = userManager.getUserInfo(targetComment.getUserId());
            }
        }
        return ReplyElement.builder()
                .replyId(reply.getId())
                .publisherInfo(userManager.getUserInfo(reply.getUserId()))
                .targetUser(targetUser)
                .content(reply.getContent())
                .attachments(getAttachments(reply.getId(), TargetTypeEnum.COMMENT))
                .isPinned(reply.getIsPinned())
                .isAuthor(reply.getUserId().equals(postAuthorId))
                .isDeleted(reply.getDeleted())
                .createdAt(reply.getCreatedAt())
                .upvoteCount(reply.getUpvoteCount())
                .replyCount(reply.getReplyCount())
                .build();
    }

    private CommentElement buildCommentElement(Comment comment, List<ReplyElement> replies, Long postAuthorId) {
        return CommentElement.builder()
                .commentId(comment.getId())
                .publisherInfo(userManager.getUserInfo(comment.getUserId()))
                .content(comment.getContent())
                .attachments(getAttachments(comment.getId(), TargetTypeEnum.COMMENT))
                .isPinned(comment.getIsPinned())
                .isAuthor(comment.getUserId().equals(postAuthorId))
                .isDeleted(comment.getDeleted())
                .createdAt(comment.getCreatedAt())
                .upvoteCount(comment.getUpvoteCount())
                .replyCount(comment.getReplyCount())
                .replies(replies)
                .build();
    }

    private List<AttachmentInfoDTO> getAttachments(Long targetId, TargetTypeEnum type) {
        List<Attachment> attachments = attachmentMapper.selectList(
                new LambdaQueryWrapper<Attachment>()
                        .eq(Attachment::getTargetId, targetId)
                        .eq(Attachment::getTargetType, type)
        );

        return attachments.stream()
                .map(attachment -> AttachmentInfoDTO.builder()
                        .url(fileManager.getFileUrl(attachment.getFileId()))
                        .type(attachment.getType())
                        .filename(attachment.getFilename())
                        .build())
                .collect(Collectors.toList());
    }

    private String truncateContent(String content) {
        if (content == null || content.length() <= 50) {
            return content;
        }
        return content.substring(0, 50);
    }
}