package org.jh.forum.server.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.ReportService;
import org.jh.forum.common.dto.request.GetReportListRequest;
import org.jh.forum.common.dto.request.HandleReportRequest;
import org.jh.forum.common.dto.request.ReportContentRequest;
import org.jh.forum.common.dto.request.ReportUserRequest;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.GetReportDetailResponse;
import org.jh.forum.common.dto.response.GetReportListElement;
import org.jh.forum.common.dto.response.UserHistoryStatsResponse;
import org.jh.forum.server.manager.ReportManager;

import jakarta.annotation.Resource;

/**
 * @author zzb
 */
@DubboService(version = "1.0.0")
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Resource
    private ReportManager reportManager;

    @Override
    public void reportUser(ReportUserRequest request) {
        reportManager.reportUser(request.getType(), request.getReason(),
                request.getUserId(), request.getAttachmentIds());
    }

    @Override
    public void reportContent(ReportContentRequest request) {
        reportManager.reportContent(request.getType(), request.getReason(),
                request.getTargetId(), request.getTarget(), request.getAttachmentIds());
    }

    @Override
    public void handleReport(HandleReportRequest request) {
        reportManager.handleReport(request);
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
    public UserHistoryStatsResponse getUserHistoryStats(Long userId) {
        return reportManager.getUserHistoryStats(userId);
    }
}
