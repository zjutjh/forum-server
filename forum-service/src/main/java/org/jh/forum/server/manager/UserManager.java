package org.jh.forum.server.manager;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jh.cube.CubeService;
import org.jh.forum.common.constants.*;
import org.jh.forum.common.dto.UserInfoDTO;
import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.common.entity.*;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.client.AliyunGreenClient;
import org.jh.forum.server.mapper.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author MangoGovo
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserManager {
    private final UserMapper userMapper;
    private final UserDetailMapper userDetailMapper;
    private final AttachmentMapper attachmentMapper;
    private final FileMapper fileMapper;
    private final CubeService cubeService;
    private final AliyunGreenClient aliyunGreenClient;
    private final FileManager fileManager;
    private final ReportMapper reportMapper;
    private final OperationLogManager operationLogManager;

    public void insertUserDetail(Long userId) {
        userDetailMapper.insert(UserDetail.builder()
                .userId(userId)
                .email("")
                .profile("")
                .signature("")
                .birthday(LocalDate.of(1900, 1, 1))
                .birthdayVisible(true)
                .realnameVisible(true)
                .studentIdVisible(true)
                .build());
    }

    public List<String> getRoleList(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Collections.emptyList();
        }
        return List.of(user.getRole().getValue());
    }

    public UserInfoDTO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return UserInfoDTO.builder().build();
        }
        return UserInfoDTO.builder()
                .id(userId)
                .nickname(user.getNickname())
                .avatar(getUserAvatar(user))
                .build();
    }

    public String getUserAvatar(User user) {
        Attachment attachment = attachmentMapper.selectById(user.getAvatarId());
        if (attachment == null) {
            return "";
        }
        File file = fileMapper.selectById(attachment.getFileId());
        if (file == null) {
            return "";
        }
        return cubeService.getFileUrl(file.getObjectKey(), true);
    }

    public GetNoticeSettingsResponse getNoticeSettings() {
        User user = userMapper.selectById(StpUtil.getLoginIdAsLong());
        return GetNoticeSettingsResponse.builder()
                .upvoteNotice(user.getUpvoteNotice())
                .commentNotice(user.getCommentNotice())
                .build();
    }

    public void updateNoticeSettings(UpdateNoticeSettingsRequest request) {
        User user = userMapper.selectById(StpUtil.getLoginIdAsLong());
        user.setUpvoteNotice(request.getUpvoteNotice());
        user.setCommentNotice(request.getCommentNotice());
        userMapper.updateById(user);
    }

    public LocalDateTime checkMute() {
        User user = userMapper.selectById(StpUtil.getLoginIdAsLong());
        LocalDateTime mutedUntil = null;
        if (user != null && user.getMutedUntil() != null && user.getMutedUntil().isAfter(LocalDateTime.now())) {
            mutedUntil = user.getMutedUntil();
        }
        return mutedUntil;
    }

    public void muteUser(long id, int hours) {
        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, id)
                .set(User::getMutedUntil, LocalDateTime.now().plusHours(hours))
        );
        operationLogManager.addOperationLog(
                AdminOperationLogTypeEnum.MUTE_USER,
                "",
                hours + "hours",
                id
        );
    }

    public void addMuteTime(long id, int hours) {
        User user = getUserOrThrow(id);
        LocalDateTime mutedUntil = user.getMutedUntil() == null ? LocalDateTime.now() : user.getMutedUntil();
        user.setMutedUntil(mutedUntil.plusHours(hours));
        userMapper.updateById(user);
    }

    public String generateRandomNickname() {
        for (int i = 0; i < 10; i++) {
            String nickname = "精小弘" + RandomUtil.randomNumbers(6);
            if (!userMapper.exists(new LambdaQueryWrapper<User>().eq(User::getNickname, nickname))) {
                return nickname;
            }
        }
        return "精小弘" + IdUtil.objectId();
    }

    public User getUserOrThrow(Long id) {
        return Optional.ofNullable(userMapper.selectById(id))
                .orElseThrow(() -> new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND));
    }

    public void updateUserProfile(UpdateUserProfileRequest request) {
        String text = StringUtils.joinWith(" ", request.getNickname(), request.getSignature(), request.getProfile(), request.getEmail());
        aliyunGreenClient.checkText(text, TextModerationServiceEnum.NICKNAME);

        Long userId = StpUtil.getLoginIdAsLong();
        User userEntity = userMapper.selectById(userId);
        UserDetail detailEntity = userDetailMapper.selectById(userId);

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .ne(User::getId, userId)
                .eq(User::getNickname, request.getNickname());
        if (userMapper.exists(queryWrapper)) {
            throw new ApiException(ExceptionEnum.USER_NICKNAME_EXISTS);
        }

        Long attachmentId = fileManager.getAttachmentIdFromUrl(request.getAvatar());
        if (attachmentId != null) {
            if (!attachmentId.equals(userEntity.getAvatarId())) {
                fileManager.deleteAttachment(userEntity.getAvatarId());
            }
            userEntity.setAvatarId(attachmentId);
        }

        if (request.getBirthday() != null && request.getBirthday().isAfter(LocalDate.of(1900, 1, 1))) {
            detailEntity.setBirthday(request.getBirthday());
        }
        userEntity.setNickname(request.getNickname());
        userEntity.setGender(request.getGender());
        userEntity.setCollegeId(request.getCollegeId());
        detailEntity.setSignature(request.getSignature());
        detailEntity.setEmail(request.getEmail());
        detailEntity.setProfile(request.getProfile());
        detailEntity.setBirthdayVisible(request.getBirthdayVisible());
        detailEntity.setRealnameVisible(request.getRealnameVisible());
        detailEntity.setStudentIdVisible(request.getStudentIdVisible());

        userMapper.updateById(userEntity);
        userDetailMapper.updateById(detailEntity);
    }

    public void updateBackgroundImage(UpdateBackgroundImageRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        UserDetail detailEntity = userDetailMapper.selectById(userId);
        detailEntity.setBackgroundImage(request.getBackgroundImage());
        userDetailMapper.updateById(detailEntity);
    }

    public GetUserProfileResponse getUserProfile(Long userId) {
        User userEntity = userMapper.selectById(userId);
        UserDetail detailEntity = userDetailMapper.selectById(userId);
        if (userEntity == null || detailEntity == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }

        Boolean isSelf = userId.equals(StpUtil.getLoginIdAsLong());
        LocalDate birthday = null;
        if ((detailEntity.getBirthdayVisible() || isSelf)
                && detailEntity.getBirthday().isAfter(LocalDate.of(1900, 1, 1))) {
            birthday = detailEntity.getBirthday();
        }

        return GetUserProfileResponse.builder()
                .userId(userId)
                .nickname(userEntity.getNickname())
                .avatar(getUserAvatar(userEntity))
                .signature(detailEntity.getSignature())
                .profile(detailEntity.getProfile())
                .email(detailEntity.getEmail())
                .gender(userEntity.getGender())
                .background(detailEntity.getBackgroundImage())
                .birthdayVisible(detailEntity.getBirthdayVisible())
                .realnameVisible(detailEntity.getRealnameVisible())
                .studentIdVisible(detailEntity.getStudentIdVisible())
                .collegeId(userEntity.getCollegeId())
                .birthday(birthday)
                .realname(detailEntity.getRealnameVisible() || isSelf ? userEntity.getRealname() : null)
                .studentId(detailEntity.getStudentIdVisible() || isSelf ? userEntity.getStudentId() : null)
                .build();
    }

    public BaseListResponse<GetUserListElement> getUserList(GetUserListRequest request) {
        IPage<User> page = new Page<>(request.getPage(), request.getPageSize());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getRole, UserTypeEnum.STUDENT);
        if (request.getStatus() != null) {
            if (request.getStatus() == UserStatusEnum.NORMAL) {
                queryWrapper.and(wrapper -> wrapper
                        .isNull(User::getMutedUntil)
                        .or()
                        .le(User::getMutedUntil, LocalDateTime.now()));
                queryWrapper.apply("report_count = resolved_report_count");
            }
            if (request.getStatus() == UserStatusEnum.PENDING) {
                queryWrapper.and(wrapper -> wrapper
                        .isNull(User::getMutedUntil)
                        .or()
                        .le(User::getMutedUntil, LocalDateTime.now()));
                queryWrapper.apply("report_count > resolved_report_count");
            }
            if (request.getStatus() == UserStatusEnum.MUTED) {
                queryWrapper.isNotNull(User::getMutedUntil).gt(User::getMutedUntil, LocalDateTime.now());
            }
        }
        if ("no".equals(request.getReported())) {
            queryWrapper.eq(User::getReportCount, 0);
        }
        if ("yes".equals(request.getReported())) {
            queryWrapper.gt(User::getReportCount, 0);
        }
        if (StringUtils.isNotBlank(request.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(User::getNickname, request.getKeyword())
                    .or()
                    .like(User::getStudentId, request.getKeyword()));
        }
        userMapper.selectPage(page, queryWrapper);
        return BaseListResponse.<GetUserListElement>builder()
                .page(request.getPage())
                .pageSize(request.getPageSize())
                .total(page.getTotal())
                .list(page.getRecords().stream().map(this::buildUserListElement).toList())
                .build();
    }

    public AdminGetUserDetailResponse getUserDetail(Long id) {
        User userEntity = userMapper.selectById(id);
        UserDetail detailEntity = userDetailMapper.selectById(id);
        if (userEntity == null || detailEntity == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }

        LocalDate birthday = detailEntity.getBirthday();
        if (!birthday.isAfter(LocalDate.of(1900, 1, 1))) {
            birthday = null;
        }

        return AdminGetUserDetailResponse.builder()
                .nickname(userEntity.getNickname())
                .avatar(getUserAvatar(userEntity))
                .background(detailEntity.getBackgroundImage())
                .signature(detailEntity.getSignature())
                .email(detailEntity.getEmail())
                .gender(userEntity.getGender())
                .realname(userEntity.getRealname())
                .collegeId(userEntity.getCollegeId())
                .birthday(birthday)
                .createdAt(userEntity.getCreatedAt())
                .status(getUserStatus(userEntity))
                .profile(detailEntity.getProfile())
                .studentId(userEntity.getStudentId())
                .mutedUntil(userEntity.getMutedUntil())
                .build();
    }

    public BaseListResponse<GetAdminListElement> getAdminList(GetAdminListRequest request) {
        IPage<User> page = new Page<>(request.getPage(), request.getPageSize());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>().ne(User::getRole, UserTypeEnum.STUDENT);
        queryWrapper.like(StringUtils.isNotBlank(request.getKeyword()), User::getNickname, request.getKeyword());
        userMapper.selectPage(page, queryWrapper);
        return BaseListResponse.<GetAdminListElement>builder()
                .page(request.getPage())
                .pageSize(request.getPageSize())
                .total(page.getTotal())
                .list(page.getRecords().stream().map(this::buildAdminListElement).toList())
                .build();
    }

    private GetUserListElement buildUserListElement(User userEntity) {
        UserDetail detailEntity = userDetailMapper.selectById(userEntity.getId());
        return GetUserListElement.builder()
                .id(userEntity.getId())
                .avatar(getUserAvatar(userEntity))
                .nickname(userEntity.getNickname())
                .studentId(userEntity.getStudentId())
                .email(detailEntity.getEmail())
                .createdAt(userEntity.getCreatedAt())
                .reportCount(userEntity.getReportCount())
                .status(getUserStatus(userEntity))
                .mutedUntil(userEntity.getMutedUntil())
                .build();
    }

    private UserStatusEnum getUserStatus(User userEntity) {
        if (userEntity.getMutedUntil() == null || userEntity.getMutedUntil().isBefore(LocalDateTime.now())) {
            if (userEntity.getReportCount() > userEntity.getResolvedReportCount()) {
                return UserStatusEnum.PENDING;
            }
            return UserStatusEnum.NORMAL;
        }
        return UserStatusEnum.MUTED;
    }

    private GetAdminListElement buildAdminListElement(User userEntity) {
        LambdaQueryWrapper<Report> reportWrapper = new LambdaQueryWrapper<Report>()
                .ne(Report::getStatus, ReportStatusEnum.PENDING)
                .eq(Report::getReviewerId, userEntity.getId());
        Integer reportCount = Math.toIntExact(reportMapper.selectCount(reportWrapper));

        return GetAdminListElement.builder()
                .id(userEntity.getId())
                .nickname(userEntity.getNickname())
                .type(userEntity.getRole())
                .createdAt(userEntity.getCreatedAt())
                .reportCount(reportCount)
                .build();
    }
}
