package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.NoticeService;
import org.jh.forum.common.dto.request.CreateNoticeRequest;
import org.jh.forum.common.dto.request.GetNoticeListRequest;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.server.manger.NoticeManager;

/**
 * @author SugarMGP
 */
@DubboService(version = "1.0.0")
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

}