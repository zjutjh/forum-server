package org.jh.forum.server.manger;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.ReportTargetTypeEnum;
import org.jh.forum.common.constants.ReportTypeEnum;
import org.jh.forum.common.dto.request.HandleReportRequest;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.GetReportDetailResponse;
import org.jh.forum.common.dto.response.GetReportListElement;
import org.jh.forum.common.entity.Report;
import org.jh.forum.common.exceptions.ForumServiceException;
import org.jh.forum.server.mapper.PostMapper;
import org.jh.forum.server.mapper.ReportMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zzb
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReportManager {
    private final ReportMapper reportMapper;
    private final PostMapper postMapper;
    private final UserManager userManager;

    public Long reportUser(ReportTypeEnum type, String reason, Long userId, ReportTargetTypeEnum target) {
        Report report = Report.builder()
                .type(type)
                .userId(userId)
                .reason(reason)
                .targetId(userId)
                .targetType(target)
                .status("未处理")
                .result("")
                .build();
        reportMapper.insert(report);
        return report.getId();
    }

    public Long reportContent(ReportTypeEnum type, String reason, Long targetId, ReportTargetTypeEnum target) {
        Long userId = -1L;
        try {
            switch (target) {
                case POST:
                    userId = postMapper.selectById(targetId).getUserId();
                    break;
                case COMMENT:
                    // todo 根据评论id获取被举报用户id
                    break;
                default:
                    throw new ForumServiceException(ExceptionEnum.INVALID_PARAMETER);
            }
        } catch (Exception e) {
            throw new ForumServiceException(ExceptionEnum.UNKNOWN_ERROR);
        }
        Report report = Report.builder()
                .type(type)
                .userId(userId)
                .reason(reason)
                .targetId(targetId)
                .targetType(target)
                .status("未处理")
                .result("")
                .build();
        reportMapper.insert(report);
        return report.getId();
    }

    public void handleReport(HandleReportRequest request) {
        Report report;
        try {
            report = reportMapper.selectById(request.getReportId());
            if (report == null) {
                throw new ForumServiceException(ExceptionEnum.RESOURCE_NOT_FOUND);
            }
            if (request.getDelete() == 1) {
                ReportTargetTypeEnum targetType = report.getTargetType();
                    switch (targetType) {
                        case POST:
                            postMapper.deleteById(report.getTargetId());
                            break;
                        case COMMENT:
                            // todo 根据评论id删除评论
                            break;
                        case USER:
                            break;
                        default:
                            throw new ForumServiceException(ExceptionEnum.INVALID_PARAMETER);
                    }
            }
        } catch (Exception e) {
            throw new ForumServiceException(ExceptionEnum.UNKNOWN_ERROR);
        }

        // todo 根据type入参判断禁言时长
        // todo 根据hours入参判断自定义禁言时长
        report.setStatus(request.getStatus() == 1 ? "举报成功" : "举报失败");
        report.setResult(request.getResult());
        reportMapper.updateById(report);
    }

    public BaseListResponse<GetReportListElement> getReportList(Integer status, Integer order, Integer page, Integer pageSize) {
        IPage<Report> reportPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            if (status == 1) {
                queryWrapper.eq(Report::getStatus, "未处理");
            } else {
                queryWrapper.ne(Report::getStatus, "未处理");
            }
        }
        if (order != null) {
            if (order == 1) {
                queryWrapper.orderByAsc(Report::getCreatedAt);
            } else {
                queryWrapper.orderByDesc(Report::getCreatedAt);
            }
        }
        reportMapper.selectPage(reportPage, queryWrapper);
        List<GetReportListElement> list = new ArrayList<>();
        for (Report report : reportPage.getRecords()) {
            list.add(GetReportListElement.builder()
                    .id(report.getId())
                    .status(report.getStatus())
                    .targetType(report.getTargetType())
                    .type(report.getType())
                    .reason(report.getReason())
                    .userId(report.getUserId())
                    .nickname(userManager.getUserInfo(report.getUserId()).getNickname())
                    .createdAt(report.getCreatedAt())
                    .build());
        }
        return BaseListResponse.<GetReportListElement>builder()
                .list(list)
                .total(reportPage.getTotal())
                .page(page)
                .pageSize(pageSize)
                .build();
    }

    public GetReportDetailResponse getReportDetail(Long id) {
        Report report = reportMapper.selectById(id);
        if (report == null) {
            throw new ForumServiceException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }

        return GetReportDetailResponse.builder()
                .createUid(report.getCreateUid())
                .userId(report.getUserId())
                .nickname(userManager.getUserInfo(report.getUserId()).getNickname())
                .createdAt(report.getCreatedAt())
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .type(report.getType())
                .reason(report.getReason())
                // todo 获取附件
                .attachImages(new ArrayList<>())
                .status(report.getStatus())
                .result(report.getResult())
                .build();
    }
}
