package org.jh.forum.server.manger;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.dto.UserInfoDTO;
import org.jh.forum.common.dto.request.CreateNoticeRequest;
import org.jh.forum.common.dto.response.UnreadNoticeCheckResponse;
import org.jh.forum.server.mapper.NoticeMapper;
import org.jh.forum.common.entity.Notice;
import org.springframework.stereotype.Service;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.GetNoticeListElement;

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
     * 获取用户的通知列表
     * 根据接收者ID分页查询通知，可按类型筛选，并自动标记未读通知为已读
     *
     * @param receiverId 接收者用户ID
     * @param page 当前页码
     * @param pageSize 每页数量
     * @param type 通知类型(1:赞/收藏, 2:评论/at)
     * @return 分页的通知列表响应，包含通知详情和分页信息
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

    /**
     * 创建新的通知
     * 根据请求参数构建通知实体并插入数据库
     *
     * @param request 创建通知的请求参数，包含接收者ID、通知类型、位置信息等
     */
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

    /**
     * 检查当前登录用户的未读通知数量
     * 通过查询数据库获取当前用户所有未读且未删除的通知数量
     *
     * @return UnreadNoticeCheckResponse 包含未读通知数量的响应对象
     */
    public UnreadNoticeCheckResponse checkUnreadNotices() {
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getReceiverId, StpUtil.getLoginIdAsLong())
                .eq(Notice::getIsRead, false)
                .eq(Notice::getDeleted, false);

        int currentCount = Math.toIntExact(noticeMapper.selectCount(queryWrapper));

        return UnreadNoticeCheckResponse.builder()
                .unreadCount(currentCount)
                .build();
    }
}
