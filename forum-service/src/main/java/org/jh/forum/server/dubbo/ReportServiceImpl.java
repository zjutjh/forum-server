package org.jh.forum.server.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.ReportService;
import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.server.manager.ReportManager;

import jakarta.annotation.Resource;

/**
 * @author zzb
 */
@DubboService
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Resource
    private ReportManager reportManager;

    @Override
    public void reportUser(ReportUserRequest request) {
        reportManager.reportUser(request.getType(), request.getReason(),
                request.getUserId(), request.getPictures());
    }

    @Override
    public void reportContent(ReportContentRequest request) {
        reportManager.reportContent(request.getType(), request.getReason(),
                request.getTargetId(), request.getTarget(), request.getPictures());
    }

    @Override
    public HandleReportResponse handleReport(HandleReportRequest request) {
        return reportManager.handleReport(request);
    }

    @Override
    public BaseListResponse<GetReportListElement> getReportList(GetReportListRequest request) {
        return reportManager.getReportList(request.getStatus(), request.getOrder(),
                request.getPage(), request.getPageSize());
    }

    @Override
    public GetReportDetailResponse getReportDetail(Long id) {
        return reportManager.getReportDetail(id);
    }

    @Override
    public BaseListResponse<GetReportInfoElement> getReportInfoList(GetReportInfoListRequest request) {
        return reportManager.getReportInfoList(request.getReportId(), request.getPage(),
                request.getPageSize());
    }
}
