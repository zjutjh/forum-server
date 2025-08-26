package org.jh.forum.server.manager;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.NoticePositionTypeEnum;
import org.jh.forum.common.constants.NoticeTypeEnum;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.GetNoticeListElement;
import org.jh.forum.common.dto.response.UnreadCheckResponse;
import org.jh.forum.common.entity.*;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.mapper.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lyyzzz
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeManager {

    private final NoticeMapper noticeMapper;
    private final UserManager userManager;
    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final UpvoteMapper upvoteMapper;
    private final UserMapper userMapper;
    private final AnnouncementMapper announcementMapper;

    /**
     * 获取用户的通知列表
     * 根据接收者ID分页查询通知，可按类型筛选
     */
    public BaseListResponse<GetNoticeListElement> getNoticeList(Integer page, Integer pageSize, Integer type) {
        Long receiverId = StpUtil.getLoginIdAsLong();

        Page<Notice> noticePage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getReceiverId, receiverId);
        switch (type) {
            case 0 -> {
            }
            case 1 -> queryWrapper.eq(Notice::getType, NoticeTypeEnum.LIKE);
            case 2 -> queryWrapper.eq(Notice::getType, NoticeTypeEnum.COLLECT);
            case 3 -> queryWrapper.nested(w -> w.eq(Notice::getType, NoticeTypeEnum.COMMENT)
                    .or()
                    .eq(Notice::getType, NoticeTypeEnum.AT));
            default -> throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
        }
        queryWrapper.orderByDesc(Notice::getUpdatedAt);
        noticeMapper.selectPage(noticePage, queryWrapper);
        List<Notice> notices = noticePage.getRecords();
        List<GetNoticeListElement> list = notices.stream()
                .map(notice -> {
                    Boolean isLiked = null;
                    if (notice.getType() == NoticeTypeEnum.COMMENT) {
                        LambdaQueryWrapper<Upvote> upvoteWrapper = new LambdaQueryWrapper<Upvote>()
                                .eq(Upvote::getUserId, receiverId)
                                .eq(Upvote::getCommentId, notice.getCommentId());
                        Upvote upvote = upvoteMapper.selectOne(upvoteWrapper);
                        isLiked = upvote != null && upvote.getStatus();
                    }
                    String positionContent = switch (notice.getPositionType()) {
                        case POST -> getContent(NoticePositionTypeEnum.POST, notice.getPostId());
                        case COMMENT -> getContent(NoticePositionTypeEnum.COMMENT, notice.getCommentId());
                        case REPLY -> getContent(NoticePositionTypeEnum.REPLY, notice.getReplyId());
                    };
                    return GetNoticeListElement.builder()
                            .id(notice.getId())
                            .senderInfo(userManager.getUserInfo(notice.getSenderId()))
                            .type(notice.getType())
                            .positionType(notice.getPositionType())
                            .postId(notice.getPostId())
                            .commentId(notice.getCommentId())
                            .replyId(notice.getReplyId())
                            .positionContent(positionContent)
                            .newCommentId(notice.getNewCommentId())
                            .newCommentContent(notice.getNewCommentId() == 0 ? null : getContent(NoticePositionTypeEnum.COMMENT, notice.getNewCommentId()))
                            .updatedAt(notice.getUpdatedAt())
                            .isLiked(isLiked)
                            .build();
                }).toList();

        userMapper.update(
                new LambdaUpdateWrapper<User>()
                        .eq(User::getId, receiverId)
                        .set(User::getLastNoticeReadAt, LocalDateTime.now())
        );
        return BaseListResponse.<GetNoticeListElement>builder()
                .list(list)
                .total(noticePage.getTotal())
                .page(page)
                .pageSize(pageSize)
                .build();
    }

    private String getContent(NoticePositionTypeEnum positionType, long positionId) {
        String content = null;
        if (positionType == NoticePositionTypeEnum.POST) {
            Post post = postMapper.selectById(positionId);
            if (post != null) {
                content = post.getTitle() + "：" + post.getContent();
            }
        } else {
            Comment comment = commentMapper.selectById(positionId);
            if (comment != null) {
                Post post = postMapper.selectById(comment.getPostId());
                if (post != null) {
                    // 判断是否没有父评论或者父评论没被删除
                    if (comment.getParentId() == 0 || commentMapper.selectById(comment.getParentId()) != null) {
                        content = comment.getContent();
                    }
                }
            }
        }
        return StringUtils.left(StringUtils.deleteWhitespace(content), 60);
    }

    /**
     * 创建新的通知
     * 根据请求参数构建通知实体并插入数据库
     */
    public void createNotice(
            long receiverId,
            NoticeTypeEnum type,
            NoticePositionTypeEnum positionType,
            long postId,
            long commentId,
            long replyId,
            long newCommentId
    ) {
        long senderId = StpUtil.getLoginIdAsLong();
        if (senderId == receiverId) {
            return;
        }

        User receiver = userMapper.selectById(receiverId);
        if (type == NoticeTypeEnum.LIKE && !receiver.getUpvoteNotice()) {
            return;
        }
        if (type == NoticeTypeEnum.COMMENT && !receiver.getCommentNotice()) {
            return;
        }

        // 查询相同通知
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<Notice>()
                .eq(Notice::getReceiverId, receiverId)
                .eq(Notice::getSenderId, senderId)
                .eq(Notice::getType, type)
                .eq(Notice::getPositionType, positionType)
                .eq(Notice::getPostId, postId)
                .eq(Notice::getCommentId, commentId)
                .eq(Notice::getReplyId, replyId)
                .eq(Notice::getNewCommentId, newCommentId);

        Notice notice = noticeMapper.selectOne(wrapper.last("LIMIT 1"));
        if (notice != null) {
            noticeMapper.updateById(notice);
        } else {
            notice = Notice.builder()
                    .receiverId(receiverId)
                    .senderId(senderId)
                    .type(type)
                    .positionType(positionType)
                    .postId(postId)
                    .commentId(commentId)
                    .replyId(replyId)
                    .newCommentId(newCommentId)
                    .build();
            noticeMapper.insert(notice);
        }
    }

    /**
     * 撤回点赞通知
     */
    public void cancelLike(
            long receiverId,
            NoticePositionTypeEnum positionType,
            long postId,
            long commentId,
            long replyId
    ) {
        Long senderId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<Notice>()
                .eq(Notice::getReceiverId, receiverId)
                .eq(Notice::getSenderId, senderId)
                .eq(Notice::getType, NoticeTypeEnum.LIKE)
                .eq(Notice::getPositionType, positionType)
                .eq(Notice::getPostId, postId)
                .eq(Notice::getCommentId, commentId)
                .eq(Notice::getReplyId, replyId);
        Notice notice = noticeMapper.selectOne(wrapper.last("LIMIT 1"));
        if (notice != null && notice.getUpdatedAt().isAfter(LocalDateTime.now().minusSeconds(5))) {
            noticeMapper.deleteById(notice.getId());
        }
    }

    /**
     * 检查当前登录用户的未读通知数量
     * 通过查询数据库获取当前用户所有未读且未删除的通知数量
     */
    public UnreadCheckResponse unreadCheck() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.selectById(userId);

        LambdaQueryWrapper<Notice> noticeQueryWrapper = new LambdaQueryWrapper<>();
        noticeQueryWrapper.eq(Notice::getReceiverId, userId)
                .ge(Notice::getUpdatedAt, user.getLastNoticeReadAt());
        int noticeCount = Math.toIntExact(noticeMapper.selectCount(noticeQueryWrapper));

        LambdaQueryWrapper<Announcement> announcementQueryWrapper = new LambdaQueryWrapper<>();
        announcementQueryWrapper.nested(w -> w.eq(Announcement::getTargetUid, userId)
                        .or()
                        .eq(Announcement::getTargetUid, -1))
                .ge(Announcement::getPublishedAt, user.getLastAnnouncementReadAt())
                .le(Announcement::getPublishedAt, LocalDateTime.now());
        int announcementCount = Math.toIntExact(announcementMapper.selectCount(announcementQueryWrapper));

        return UnreadCheckResponse.builder()
                .unreadNoticeCount(noticeCount)
                .unreadAnnouncementCount(announcementCount)
                .build();
    }
}
