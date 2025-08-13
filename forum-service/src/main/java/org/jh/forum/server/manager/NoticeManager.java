package org.jh.forum.server.manager;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        User user = userMapper.selectById(receiverId);
        user.setLastNoticeReadAt(LocalDateTime.now());
        userMapper.updateById(user);

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
                    return GetNoticeListElement.builder()
                            .id(notice.getId())
                            .senderInfo(userManager.getUserInfo(notice.getSenderId()))
                            .type(notice.getType())
                            .positionType(notice.getPositionType())
                            .positionId(notice.getPositionId())
                            .positionContent(getContent(notice.getPositionType(), notice.getPositionId()))
                            .newCommentId(notice.getCommentId())
                            .newCommentContent(notice.getCommentId() == null ? null : getContent(NoticePositionTypeEnum.COMMENT, notice.getCommentId()))
                            .updatedAt(notice.getUpdatedAt())
                            .isLiked(isLiked)
                            .build();
                }).toList();
        return BaseListResponse.<GetNoticeListElement>builder()
                .list(list)
                .total(noticePage.getTotal())
                .page(page)
                .pageSize(pageSize)
                .build();
    }

    private String getContent(NoticePositionTypeEnum positionType, Long positionId) {
        String content;
        if (positionType == NoticePositionTypeEnum.POST) {
            Post post = postMapper.selectById(positionId);
            if (post == null) {
                content = "帖子不存在";
            } else {
                content = post.getTitle() + post.getContent();
            }
        } else {
            Comment comment = commentMapper.selectById(positionId);
            if (comment == null) {
                content = "评论不存在";
            } else {
                content = comment.getContent();
            }
        }
        return StringUtils.left(StringUtils.deleteWhitespace(content), 30);
    }

    /**
     * 创建新的通知
     * 根据请求参数构建通知实体并插入数据库
     */
    public void createNotice(Long receiverId, NoticeTypeEnum type, NoticePositionTypeEnum positionType, Long positionId, Long newCommentId) {
        Long senderId = StpUtil.getLoginIdAsLong();
        if (senderId.equals(receiverId)) {
            return;
        }

        User sender = userMapper.selectById(senderId);
        if (type == NoticeTypeEnum.LIKE && !sender.getUpvoteNotice()) {
            return;
        }
        if (type == NoticeTypeEnum.COMMENT && !sender.getCommentNotice()) {
            return;
        }

        // 查询相同通知
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<Notice>()
                .eq(Notice::getReceiverId, receiverId)
                .eq(Notice::getSenderId, senderId)
                .eq(Notice::getType, type)
                .eq(Notice::getPositionType, positionType)
                .eq(Notice::getPositionId, positionId);
        if (newCommentId != null) {
            wrapper.eq(Notice::getCommentId, newCommentId);
        } else {
            wrapper.isNull(Notice::getCommentId);
        }

        Notice notice = noticeMapper.selectOne(wrapper);
        if (notice != null) {
            noticeMapper.updateById(notice);
        } else {
            notice = Notice.builder()
                    .receiverId(receiverId)
                    .senderId(senderId)
                    .type(type)
                    .positionType(positionType)
                    .positionId(positionId)
                    .commentId(newCommentId)
                    .build();
            noticeMapper.insert(notice);
        }
    }

    /**
     * 撤回点赞通知
     */
    public void cancelLike(Long receiverId, NoticePositionTypeEnum positionType, Long positionId) {
        Long senderId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<Notice>()
                .eq(Notice::getReceiverId, receiverId)
                .eq(Notice::getSenderId, senderId)
                .eq(Notice::getType, NoticeTypeEnum.LIKE)
                .eq(Notice::getPositionType, positionType)
                .eq(Notice::getPositionId, positionId);
        Notice notice = noticeMapper.selectOne(wrapper);
        if (notice != null && notice.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(5))) {
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
