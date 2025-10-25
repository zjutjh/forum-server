package org.jh.forum.api.dubbo.service;


import org.jh.forum.common.dto.request.GetNoticeListRequest;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.GetNoticeListElement;
import org.jh.forum.common.dto.response.UnreadCheckResponse;

/**
 * 消息服务接口
 *
 * @author lyyzzz
 */
public interface NoticeService {

    /**
     * 获取消息列表
     *
     * @param request 请求参数
     * @return 消息列表分页结果
     */
    BaseListResponse<GetNoticeListElement> getNoticeList(GetNoticeListRequest request);

    /**
     * 未读消息检查
     *
     * @return 未读消息数量响应
     */
    UnreadCheckResponse unreadCheck();
}
