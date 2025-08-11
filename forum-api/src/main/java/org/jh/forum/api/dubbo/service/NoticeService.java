package org.jh.forum.api.dubbo.service;


import org.jh.forum.common.dto.request.GetNoticeListRequest;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.GetNoticeListElement;
import org.jh.forum.common.dto.response.UnreadCheckResponse;

/**
 * @author lyyzzz
 */
public interface NoticeService {

    BaseListResponse<GetNoticeListElement> getNoticeList(GetNoticeListRequest request);

    UnreadCheckResponse unreadCheck();
}
