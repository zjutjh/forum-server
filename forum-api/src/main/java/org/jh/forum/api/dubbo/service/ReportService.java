package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.dto.request.GetReportListRequest;
import org.jh.forum.common.dto.request.HandleReportRequest;
import org.jh.forum.common.dto.request.ReportContentRequest;
import org.jh.forum.common.dto.request.ReportUserRequest;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.GetReportDetailResponse;
import org.jh.forum.common.dto.response.GetReportListElement;
import org.jh.forum.common.dto.response.UserHistoryStatsResponse;

/**
 * @author zzb
 */
public interface ReportService {
    void reportUser(ReportUserRequest request);

    void reportContent(ReportContentRequest request);

    void handleReport(HandleReportRequest request);

    BaseListResponse<GetReportListElement> getReportList(GetReportListRequest request);

    GetReportDetailResponse getReportDetail(Long id);

    UserHistoryStatsResponse getUserHistoryStats(Long userId);
}
