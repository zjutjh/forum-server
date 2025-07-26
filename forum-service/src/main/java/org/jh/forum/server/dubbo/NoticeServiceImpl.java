package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.NoticeService;
import org.jh.forum.common.dto.request.CreateNoticeRequest;
import org.jh.forum.common.dto.request.GetNoticeListRequest;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.GetNoticeListElement;
import org.jh.forum.common.dto.response.UnreadNoticeCheckResponse;
import org.jh.forum.server.manager.NoticeManager;

import jakarta.annotation.Resource;

/**
 * @author SugarMGP
 */
@DubboService
@Slf4j
public class NoticeServiceImpl implements NoticeService {

    @Resource
    private NoticeManager noticeManager;

    @Override
    public BaseListResponse<GetNoticeListElement> getNoticeList(GetNoticeListRequest request) {
        return noticeManager.getNoticeList(StpUtil.getLoginIdAsLong(), request.getPage(), request.getPageSize(), request.getType());
    }

    @Override
    public void createNotice(CreateNoticeRequest request) {
        noticeManager.createNotice(request);
    }

    @Override
    public UnreadNoticeCheckResponse checkUnreadNotices() {
        return noticeManager.checkUnreadNotices();
    }

}