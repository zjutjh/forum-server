package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;

/**
 * 举报服务接口
 *
 * @author zzb
 */
public interface ReportService {

    /**
     * 举报用户
     *
     * @param request 举报用户请求参数
     */
    void reportUser(ReportUserRequest request);

    /**
     * 举报内容（帖子或评论）
     *
     * @param request 举报内容请求参数
     */
    void reportContent(ReportContentRequest request);

    /**
     * 处理举报
     *
     * @param request 处理举报请求参数
     * @return 下一个举报响应
     */
    HandleReportResponse handleReport(HandleReportRequest request);

    /**
     * 获取举报列表
     *
     * @param request 获取举报列表请求参数
     * @return 举报列表分页响应
     */
    BaseListResponse<GetReportListElement> getReportList(GetReportListRequest request);

    /**
     * 获取举报详情
     *
     * @param id 举报ID
     * @return 举报详情响应
     */
    GetReportDetailResponse getReportDetail(Long id);

    /**
     * 获取举报信息列表
     *
     * @param request 获取举报信息列表请求参数
     * @return 举报信息列表分页响应
     */
    BaseListResponse<GetReportInfoElement> getReportInfoList(GetReportInfoListRequest request);
}
