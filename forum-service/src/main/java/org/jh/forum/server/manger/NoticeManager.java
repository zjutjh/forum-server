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
    public BaseListResponse<GetNoticeListElement> getNoticeList(Long receiverId, Integer page, Integer pageSize, NoticeTypeEnum type) {
        Page<Notice> noticePage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getReceiverId, receiverId)
                .eq(type != null, Notice::getType, type)
                .eq(Notice::getDeleted, false)
                .orderByDesc(Notice::getCreatedAt);
        noticeMapper.selectPage(noticePage, queryWrapper);
        List<GetNoticeListElement> list = new ArrayList<>();
        for (Notice notice : noticePage.getRecords()) {
            UserInfoDTO senderInfo = userManager.getUserInfo(notice.getSenderId());

            list.add(GetNoticeListElement.builder()
                    .id(notice.getId())
                    .senderInfo(senderInfo)
                    .type(notice.getType())
                    .positionType(notice.getPositionType())
                    .positionId(notice.getPositionId())
                    .commentId(notice.getCommentId())
                    .createdAt(notice.getCreatedAt())
                    .build());
        }
        return BaseListResponse.<GetNoticeListElement>builder()
                .list(list)
                .total(noticePage.getTotal())
                .page(page)
                .pageSize(pageSize)
                .build();
    }

    public void createNotice(CreateNoticeRequest request) {
        Notice notice = Notice.builder()
                .receiverId(request.getReceiverId())
                .senderId(StpUtil.getLoginIdAsLong())
                .type(request.getType())
                .positionType(request.getPositionType())
                .positionId(request.getPositionId())
                .commentId(request.getCommentId())
                .attribute(request.getAttribute())
                .deleted(false)
                .createUid(StpUtil.getLoginIdAsLong())
                .updateUid(StpUtil.getLoginIdAsLong())
                .build();
        noticeMapper.insert(notice);
    }
}
