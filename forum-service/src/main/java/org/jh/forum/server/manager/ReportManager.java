package org.jh.forum.server.manager;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.EnumUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.*;
import org.jh.forum.common.dto.PictureInfoDTO;
import org.jh.forum.common.dto.request.HandleReportRequest;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.common.entity.Attachment;
import org.jh.forum.common.entity.Comment;
import org.jh.forum.common.entity.Post;
import org.jh.forum.common.entity.Report;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final AttachmentMapper attachmentMapper;
    private final FileManager fileManager;
    private final PostManager postManager;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    private final UserManager userManager;

    @Transactional
    public void reportUser(ReportTypeEnum type, String reason, Long targetUserId, List<String> pictureUrls) {
        if (targetUserId.equals(StpUtil.getLoginIdAsLong())) {
            throw new ApiException(ExceptionEnum.CANNOT_REPORT_YOURSELF);
        }
        Report report = Report.builder()
                .type(type)
                .userId(StpUtil.getLoginIdAsLong())
                .targetUserId(targetUserId)
                .reason(reason)
                .targetId(targetUserId)
                .targetType(TargetTypeEnum.USER)
                .status(ReportStatusEnum.PENDING)
                .result("")
                .build();
        reportMapper.insert(report);
        userMapper.incrementReportCount(targetUserId);
        for (String url : pictureUrls) {
            fileManager.bindAttachment(url, TargetTypeEnum.REPORT, report.getId());
        }
    }

    @Transactional
    public void reportContent(ReportTypeEnum type, String reason, Long targetId, TargetTypeEnum target, List<String> pictureUrls) {
        Long targetUserId;
        if (target == TargetTypeEnum.POST) {
            Post post = postMapper.selectById(targetId);
            if (post == null) {
                throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
            }
            targetUserId = post.getUserId();
        } else if (target == TargetTypeEnum.COMMENT) {
            Comment comment = commentMapper.selectById(targetId);
            if (comment == null) {
                throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
            }
            targetUserId = comment.getUserId();
        } else {
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
        }
        if (targetUserId.equals(StpUtil.getLoginIdAsLong())) {
            throw new ApiException(ExceptionEnum.CANNOT_REPORT_YOURSELF);
        }
        Report report = Report.builder()
                .type(type)
                .userId(StpUtil.getLoginIdAsLong())
                .targetUserId(targetUserId)
                .reason(reason)
                .targetId(targetId)
                .targetType(target)
                .status(ReportStatusEnum.PENDING)
                .result("")
                .build();
        reportMapper.insert(report);
        for (String url : pictureUrls) {
            fileManager.bindAttachment(url, TargetTypeEnum.REPORT, report.getId());
        }
    }

    @Transactional
    public HandleReportResponse handleReport(HandleReportRequest request) {
        Report report = reportMapper.selectById(request.getReportId());
        if (report == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }

        ReportStatusEnum status = EnumUtil.getBy(ReportStatusEnum::getValue, request.getStatus());
        if (status == null) {
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
        }

        if (report.getStatus() != ReportStatusEnum.PENDING) {
            throw new ApiException(ExceptionEnum.REPORT_ALREADY_HANDLED);
        }

        if (request.getShouldDelete()) {
            TargetTypeEnum targetType = report.getTargetType();
            if (targetType == TargetTypeEnum.POST) {
                postManager.deletePost(report.getTargetId(), true);
            } else if (targetType == TargetTypeEnum.COMMENT) {
                commentMapper.deleteById(report.getTargetId());
            }
        }

        if (HandleReportEnum.SHORT_MUTE.equals(request.getType())) {
            userManager.muteUser(report.getTargetUserId(), 24);
        }
        if (HandleReportEnum.LONG_MUTE.equals(request.getType())) {
            userManager.muteUser(report.getTargetUserId(), 168);
        }
        if (HandleReportEnum.CUSTOM_MUTE.equals(request.getType())) {
            userManager.muteUser(report.getTargetUserId(), request.getDays() * 24);
        }

        report.setStatus(status);
        report.setResult(request.getResult());
        report.setReviewerId(StpUtil.getLoginIdAsLong());
        report.setShouldDelete(request.getShouldDelete());
        report.setPunishmentType(request.getType());
        report.setMuteDays(request.getDays());
        reportMapper.updateById(report);

        if (report.getTargetType() == TargetTypeEnum.USER) {
            userMapper.incrementResolvedReportCount(report.getTargetUserId());
        }

        // Todo 发送举报结果给举报人和被举报人

        Report nextReport = reportMapper.selectOne(new LambdaQueryWrapper<Report>()
                .eq(Report::getStatus, ReportStatusEnum.PENDING)
                .orderByAsc(Report::getCreatedAt)
                .last("limit 1"));
        return HandleReportResponse.builder()
                .nextReportId(nextReport == null ? null : nextReport.getId())
                .build();
    }

    public BaseListResponse<GetReportListElement> getReportList(String status, String order, Integer page, Integer pageSize) {
        IPage<Report> reportPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
        if ("pending".equals(status)) {
            queryWrapper.eq(Report::getStatus, ReportStatusEnum.PENDING);
        }
        if ("processed".equals(status)) {
            queryWrapper.ne(Report::getStatus, ReportStatusEnum.PENDING);
        }
        boolean isAsc = "asc".equals(order);
        queryWrapper.orderBy(true, isAsc, Report::getCreatedAt);

        reportMapper.selectPage(reportPage, queryWrapper);
        List<GetReportListElement> list = new ArrayList<>();
        for (Report report : reportPage.getRecords()) {
            list.add(GetReportListElement.builder()
                    .id(report.getId())
                    .status(report.getStatus())
                    .targetType(report.getTargetType())
                    .type(report.getType())
                    .reason(report.getReason())
                    .userId(report.getTargetUserId())
                    .targetNickname(userMapper.selectById(report.getTargetUserId()).getNickname())
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
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }

        Integer commentPosition = null;
        LocalDateTime targetTypeCreatedAt = null;
        Comment comment = null;
        if (report.getTargetType() == TargetTypeEnum.COMMENT) {
            comment = commentMapper.selectById(report.getTargetId());
            if (comment == null) {
                throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
            }
            targetTypeCreatedAt = comment.getCreatedAt();
            List<Comment> comments = commentMapper.selectList(new LambdaQueryWrapper<Comment>()
                    .eq(Comment::getPostId, comment.getPostId())
                    .orderByAsc(Comment::getCreatedAt));
            commentPosition = comments.indexOf(comment) + 1;
        }
        if (report.getTargetType() == TargetTypeEnum.POST) {
            Post post = postMapper.selectById(report.getTargetId());
            if (post == null) {
                throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
            }
            targetTypeCreatedAt = post.getCreatedAt();
        }

        return GetReportDetailResponse.builder()
                .userId(report.getUserId())
                .targetUserId(report.getTargetUserId())
                .targetNickname(userMapper.selectById(report.getTargetUserId()).getNickname())
                .createdAt(report.getCreatedAt())
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .postId(comment != null ? comment.getPostId() : null)
                .commentPosition(commentPosition)
                .type(report.getType())
                .reason(report.getReason())
                .status(report.getStatus())
                .result(report.getResult())
                .pictures(getReportPictures(report.getId()))
                .userHistoryStats(getUserHistoryStats(report.getTargetUserId()))
                .shouldDelete(report.getShouldDelete())
                .punishmentType(report.getPunishmentType())
                .muteDays(report.getMuteDays())
                .targetTypeCreatedAt(targetTypeCreatedAt)
                .build();
    }

    private List<PictureInfoDTO> getReportPictures(Long reportId) {
        List<Attachment> attachments = attachmentMapper.selectList(new LambdaQueryWrapper<Attachment>()
                .eq(Attachment::getType, AttachmentTypeEnum.PICTURE)
                .eq(Attachment::getTargetId, reportId)
                .eq(Attachment::getTargetType, TargetTypeEnum.REPORT)
        );
        List<PictureInfoDTO> attachmentInfoList = new ArrayList<>();
        for (Attachment attachment : attachments) {
            attachmentInfoList.add(PictureInfoDTO.builder()
                    .url(fileManager.getFileUrl(attachment.getFileId()))
                    .build()
            );
        }
        return attachmentInfoList;
    }

    private UserHistoryStatsResponse getUserHistoryStats(Long userId) {
        List<Report> reports = reportMapper.selectList(new LambdaQueryWrapper<Report>()
                .eq(Report::getTargetUserId, userId)
                .orderByDesc(Report::getCreatedAt));

        List<Report> postReports = reports.stream()
                .filter(report -> report.getTargetType() == TargetTypeEnum.POST)
                .toList();

        List<Report> commentReports = reports.stream()
                .filter(report -> report.getTargetType() == TargetTypeEnum.COMMENT)
                .toList();

        List<Report> userReports = reports.stream()
                .filter(report -> report.getTargetType() == TargetTypeEnum.USER)
                .toList();

        return UserHistoryStatsResponse.builder()
                .post(calculateStats(postReports))
                .comment(calculateStats(commentReports))
                .user(calculateStats(userReports))
                .total(calculateStats(reports))
                .build();
    }

    private UserHistoryStatsResponse.StatDetail calculateStats(List<Report> reports) {
        return UserHistoryStatsResponse.StatDetail.builder()
                .reportCount(reports.size())
                .establishedCount((int) reports.stream()
                        .filter(report -> report.getStatus() == ReportStatusEnum.SUCCESS)
                        .count())
                .recentEstablishedCount((int) reports.stream()
                        .filter(report -> report.getStatus() == ReportStatusEnum.SUCCESS)
                        .filter(report -> report.getUpdatedAt().isAfter(LocalDateTime.now().minusDays(60)))
                        .count())
                .build();
    }
}
