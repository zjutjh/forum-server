package org.jh.forum.server.manger;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.NoticeTypeEnum;
import org.jh.forum.common.dto.UserInfoDTO;
import org.jh.forum.common.dto.request.CreateNoticeRequest;
import org.jh.forum.server.mapper.NoticeMapper;
import org.jh.forum.common.entity.Notice;
import org.springframework.stereotype.Service;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.GetNoticeListElement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author lyyzzz
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeManager {

    private final NoticeMapper noticeMapper;
    private final UserManager userManager;

    /**
     * 获取通知列表
     */
    public BaseListResponse<GetNoticeListElement> getNoticeList(Long receiverId, Integer page, Integer pageSize, Integer type) {
        Page<Notice> noticePage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getReceiverId, receiverId)
                .and(type != null, wrapper -> {
                    if (type == 1) {
                        // 赞/收藏
                        wrapper.eq(Notice::getType, 1).or().eq(Notice::getType, 2);
                    } else if (type == 2) {
                        // 评论/at
                        wrapper.eq(Notice::getType, 3).or().eq(Notice::getType, 4);
                    }
                })
                .eq(Notice::getDeleted, false)
                .orderByDesc(Notice::getCreatedAt);
        noticeMapper.selectPage(noticePage, queryWrapper);
        List<Notice> notices = noticePage.getRecords();
        List<Long> unreadNoticeIds = notices.stream()
                .filter(notice -> !notice.getIsRead())
                .map(Notice::getId)
                .collect(Collectors.toList());
        List<GetNoticeListElement> list = notices.stream()
                .map(notice -> {
                    UserInfoDTO senderInfo = userManager.getUserInfo(notice.getSenderId());
                    return GetNoticeListElement.builder()
                            .id(notice.getId())
                            .senderInfo(senderInfo)
                            .type(notice.getType())
                            .positionType(notice.getPositionType())
                            .positionId(notice.getPositionId())
                            .commentId(notice.getCommentId())
                            .createdAt(notice.getCreatedAt())
                            .isRead(notice.getIsRead())
                            .build();
                })
                .collect(Collectors.toList());
        BaseListResponse<GetNoticeListElement> response = BaseListResponse.<GetNoticeListElement>builder()
                .list(list)
                .total(noticePage.getTotal())
                .page(page)
                .pageSize(pageSize)
                .build();
        if (!unreadNoticeIds.isEmpty()) {
            CompletableFuture.runAsync(() -> {
                try {
                    noticeMapper.batchMarkAsRead(unreadNoticeIds);
                    log.info("异步标记通知为已读成功，ids:{}", unreadNoticeIds);
                } catch (Exception e) {
                    log.error("异步标记通知为已读失败，ids:{}", unreadNoticeIds, e);
                }
            });
        }
        return response;
    }

    public void createNotice(CreateNoticeRequest request) {
        Notice notice = Notice.builder()
                .receiverId(request.getReceiverId())
                .senderId(StpUtil.getLoginIdAsLong())
                .type(request.getType())
                .positionType(request.getPositionType())
                .positionId(request.getPositionId())
                .commentId(request.getCommentId())
                .isRead(false)
                .build();
        noticeMapper.insert(notice);
    }
}
