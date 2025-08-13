package org.jh.forum.server.manager;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.EnumUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.annotation.IgnoreLogicDelete;
import org.jh.forum.common.constants.*;
import org.jh.forum.common.dto.PictureInfoDTO;
import org.jh.forum.common.dto.request.HandleReportRequest;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.common.entity.*;
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
    private final ReportInfoMapper reportInfoMapper;
    private final AnnouncementManager announcementManager;

    @Transactional
    public void reportUser(ReportTypeEnum type, String reason, Long targetUserId, List<String> pictureUrls) {
        Long userId = StpUtil.getLoginIdAsLong();
        if (targetUserId.equals(userId)) {
            throw new ApiException(ExceptionEnum.CANNOT_REPORT_YOURSELF);
        }

        Report existingReport = reportMapper.selectOne(new LambdaQueryWrapper<Report>()
                .eq(Report::getTargetType, TargetTypeEnum.USER)
                .eq(Report::getTargetId, targetUserId)
                .eq(Report::getStatus, ReportStatusEnum.PENDING));

        if (existingReport != null) {
            if (reportInfoMapper.exists(new LambdaQueryWrapper<ReportInfo>()
                    .eq(ReportInfo::getReportId, existingReport.getId())
                    .eq(ReportInfo::getUserId, userId))) {
                throw new ApiException(ExceptionEnum.REPORT_ALREADY_EXISTS);
            }
        }

        Report report = Report.builder()
                .targetUserId(targetUserId)
                .targetId(targetUserId)
                .targetType(TargetTypeEnum.USER)
                .status(ReportStatusEnum.PENDING)
                .result("")
                .build();
        reportMapper.insert(report);

        ReportInfo reportInfo = ReportInfo.builder()
                .reportId(report.getId())
                .userId(userId)
                .type(type)
                .reason(reason)
                .build();
        reportInfoMapper.insert(reportInfo);

        userMapper.incrementReportCount(targetUserId);

        for (String url : pictureUrls) {
            fileManager.bindAttachment(url, TargetTypeEnum.REPORT, reportInfo.getId());
        }
    }

    @Transactional
    public void reportContent(ReportTypeEnum type, String reason, Long targetId, TargetTypeEnum target, List<String> pictureUrls) {
        Long userId = StpUtil.getLoginIdAsLong();

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

        if (targetUserId.equals(userId)) {
            throw new ApiException(ExceptionEnum.CANNOT_REPORT_YOURSELF);
        }

        Report existingReport = reportMapper.selectOne(new LambdaQueryWrapper<Report>()
                .eq(Report::getTargetType, target)
                .eq(Report::getTargetId, targetId)
                .eq(Report::getStatus, ReportStatusEnum.PENDING));
        if (existingReport != null) {
            if (reportInfoMapper.exists(new LambdaQueryWrapper<ReportInfo>()
                    .eq(ReportInfo::getReportId, existingReport.getId())
                    .eq(ReportInfo::getUserId, userId))) {
                throw new ApiException(ExceptionEnum.REPORT_ALREADY_EXISTS);
            }

            ReportInfo reportInfo = ReportInfo.builder()
                    .reportId(existingReport.getId())
                    .userId(userId)
                    .type(type)
                    .reason(reason)
                    .build();
            reportInfoMapper.insert(reportInfo);

            for (String url : pictureUrls) {
                fileManager.bindAttachment(url, TargetTypeEnum.REPORT, reportInfo.getId());
            }
        } else {
            Report report = Report.builder()
                    .targetUserId(targetUserId)
                    .targetId(targetId)
                    .targetType(target)
                    .status(ReportStatusEnum.PENDING)
                    .result("")
                    .build();
            reportMapper.insert(report);
            existingReport = report;

            ReportInfo reportInfo = ReportInfo.builder()
                    .reportId(report.getId())
                    .userId(userId)
                    .type(type)
                    .reason(reason)
                    .build();
            reportInfoMapper.insert(reportInfo);

            for (String url : pictureUrls) {
                fileManager.bindAttachment(url, TargetTypeEnum.REPORT, reportInfo.getId());
            }
        }

        userMapper.incrementReportCount(targetUserId);

        if (target == TargetTypeEnum.POST) {
            postMapper.incrementReportCount(existingReport.getTargetId());
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

        int hours = switch (request.getType()) {
            case NO_PUNISHMENT -> 0;
            case SHORT_MUTE -> 24;
            case LONG_MUTE -> 168;
            case CUSTOM_MUTE -> request.getDays() * 24;
        };
        if (hours > 0) {
            userManager.addMuteTime(report.getTargetUserId(), hours);
        }

        report.setStatus(status);
        report.setResult(request.getResult());
        report.setReviewerId(StpUtil.getLoginIdAsLong());
        report.setShouldDelete(request.getShouldDelete());
        report.setPunishmentType(request.getType());
        report.setMuteDays(request.getDays());
        reportMapper.updateById(report);

        userMapper.incrementResolvedReportCount(report.getTargetUserId());
        if (report.getTargetType() == TargetTypeEnum.POST) {
            postMapper.incrementResolvedReportCount(report.getTargetId());
        }

        sendResultToReportUser(report);
        announcementManager.sendSystemNotification("举报结果通知", request.getResult(), report.getTargetUserId());

        if (request.getShouldDelete()) {
            TargetTypeEnum targetType = report.getTargetType();
            if (targetType == TargetTypeEnum.POST) {
                postManager.deletePost(report.getTargetId(), true);
            } else if (targetType == TargetTypeEnum.COMMENT) {
                commentMapper.deleteById(report.getTargetId());
            }
        }

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
            ReportInfo firstReportInfo = reportInfoMapper.selectOne(new LambdaQueryWrapper<ReportInfo>()
                    .eq(ReportInfo::getReportId, report.getId())
                    .orderByAsc(ReportInfo::getCreatedAt)
                    .last("limit 1"));

            list.add(GetReportListElement.builder()
                    .id(report.getId())
                    .status(report.getStatus())
                    .targetType(report.getTargetType())
                    .type(firstReportInfo.getType())
                    .reason(firstReportInfo.getReason())
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

    @IgnoreLogicDelete
    public GetReportDetailResponse getReportDetail(Long id) {
        Report report = reportMapper.selectById(id);
        if (report == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }

        Integer commentPosition = null;
        LocalDateTime targetTypeCreatedAt = null;
        Comment comment = null;
        Long parentId = null;
        Integer replyPosition = null;
        if (report.getTargetType() == TargetTypeEnum.COMMENT) {
            comment = commentMapper.selectById(report.getTargetId());
            if (comment == null) {
                throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
            }
            targetTypeCreatedAt = comment.getCreatedAt();
            List<Comment> comments = commentMapper.selectList(new LambdaQueryWrapper<Comment>()
                    .eq(Comment::getPostId, comment.getPostId())
                    .eq(Comment::getParentId, 0L)
                    .orderByAsc(Comment::getCreatedAt));
            if (comment.getParentId() == 0) {
                commentPosition = comments.indexOf(comment) + 1;
            } else {
                commentPosition = comments.indexOf(commentMapper.selectById(comment.getParentId())) + 1;
                parentId = comment.getParentId();
                List<Comment> replies = commentMapper.selectList(new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getPostId, comment.getPostId())
                        .eq(Comment::getParentId, comment.getParentId())
                        .orderByAsc(Comment::getCreatedAt));
                replyPosition = replies.indexOf(comment) + 1;
            }
        }
        if (report.getTargetType() == TargetTypeEnum.POST) {
            Post post = postMapper.selectById(report.getTargetId());
            if (post == null) {
                throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
            }
            targetTypeCreatedAt = post.getCreatedAt();
        }

        List<ReportTypeEnum> reportTypes = reportInfoMapper.selectList(
                        new LambdaQueryWrapper<ReportInfo>()
                                .select(ReportInfo::getType)
                                .eq(ReportInfo::getReportId, report.getId())
                                .groupBy(ReportInfo::getType)
                ).stream()
                .map(ReportInfo::getType)
                .toList();

        return GetReportDetailResponse.builder()
                .targetUserId(report.getTargetUserId())
                .targetNickname(userMapper.selectById(report.getTargetUserId()).getNickname())
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .postId(comment != null ? comment.getPostId() : null)
                .commentPosition(commentPosition)
                .parentId(parentId)
                .replyPosition(replyPosition)
                .status(report.getStatus())
                .result(report.getResult())
                .userHistoryStats(getUserHistoryStats(report.getTargetUserId()))
                .shouldDelete(report.getShouldDelete())
                .punishmentType(report.getPunishmentType())
                .muteDays(report.getMuteDays())
                .targetTypeCreatedAt(targetTypeCreatedAt)
                .reportTypes(reportTypes)
                .build();
    }

    public BaseListResponse<GetReportInfoElement> getReportInfoList(Long reportId, Integer page, Integer pageSize) {
        Report report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }

        IPage<ReportInfo> reportInfoPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<ReportInfo> queryWrapper = new LambdaQueryWrapper<ReportInfo>()
                .eq(ReportInfo::getReportId, reportId)
                .orderByAsc(ReportInfo::getCreatedAt);

        reportInfoMapper.selectPage(reportInfoPage, queryWrapper);

        List<GetReportInfoElement> list = new ArrayList<>();
        for (ReportInfo reportInfo : reportInfoPage.getRecords()) {
            list.add(GetReportInfoElement.builder()
                    .userId(reportInfo.getUserId())
                    .type(reportInfo.getType())
                    .reason(reportInfo.getReason())
                    .createdAt(reportInfo.getCreatedAt())
                    .pictures(getReportPictures(reportInfo.getId()))
                    .build());
        }

        return BaseListResponse.<GetReportInfoElement>builder()
                .list(list)
                .total(reportInfoPage.getTotal())
                .page(page)
                .pageSize(pageSize)
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

    private void sendResultToReportUser(Report report) {
        List<ReportInfo> reportInfoList = reportInfoMapper.selectList(new LambdaQueryWrapper<ReportInfo>()
                .eq(ReportInfo::getReportId, report.getId()));

        String targetDescription;
        if (report.getTargetType() == TargetTypeEnum.POST) {
            Post post = postMapper.selectById(report.getTargetId());
            targetDescription = post != null ? String.format("帖子《%s》", post.getTitle()) : "帖子";
        } else if (report.getTargetType() == TargetTypeEnum.COMMENT) {
            Comment comment = commentMapper.selectById(report.getTargetId());
            targetDescription = comment != null ? String.format("评论\"%s\"", comment.getContent()) : "评论";
        } else if (report.getTargetType() == TargetTypeEnum.USER) {
            User user = userMapper.selectById(report.getTargetUserId());
            targetDescription = user != null ? String.format("用户@%s", user.getNickname()) : "用户";
        } else {
            targetDescription = "内容";
        }

        for (ReportInfo reportInfo : reportInfoList) {
            announcementManager.sendSystemNotification(
                    "举报结果通知",
                    getContent(report, userMapper.selectById(reportInfo.getUserId()).getNickname(),
                            targetDescription),
                    reportInfo.getUserId()
            );
        }
    }

    private String getContent(Report report, String userNickname, String targetDescription) {
        String notificationContent;
        if (report.getStatus() == ReportStatusEnum.SUCCESS) {
            String targetTypeDesc;
            if (report.getTargetType() == TargetTypeEnum.POST) {
                targetTypeDesc = "该条帖子";
            } else if (report.getTargetType() == TargetTypeEnum.COMMENT) {
                targetTypeDesc = "该条评论";
            } else if (report.getTargetType() == TargetTypeEnum.USER) {
                targetTypeDesc = "该用户";
            } else {
                targetTypeDesc = "该内容";
            }
            notificationContent = String.format(
                    "尊敬的%s，经核实，您举报的%s存在违规，%s已被处理。感谢您对精弘论坛美好氛围的贡献！",
                    userNickname,
                    targetDescription,
                    targetTypeDesc
            );
        } else if (report.getStatus() == ReportStatusEnum.FAILURE) {
            notificationContent = String.format(
                    "尊敬的%s，我们暂时无法判定您举报的%s存在违规，已对其重点关注！建议举报时可以" +
                            "1、丰富举报描述。2、附上违规截图等材料。感谢您对精弘论坛美好氛围的贡献！",
                    userNickname,
                    targetDescription
            );
        } else {
            notificationContent = String.format(
                    "尊敬的%s，您的举报已被处理。感谢您对精弘论坛美好氛围的贡献！",
                    userNickname
            );
        }
        return notificationContent;
    }
}
