package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;

/**
 * @author zzb
 */
public interface ReportService {
    void reportUser(ReportUserRequest request);

    void reportContent(ReportContentRequest request);

    HandleReportResponse handleReport(HandleReportRequest request);

    BaseListResponse<GetReportListElement> getReportList(GetReportListRequest request);

    GetReportDetailResponse getReportDetail(Long id);

    BaseListResponse<GetReportInfoElement> getReportInfoList(GetReportInfoListRequest request);
}
