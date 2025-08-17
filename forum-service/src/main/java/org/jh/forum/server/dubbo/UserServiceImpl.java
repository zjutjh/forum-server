package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.UserService;
import org.jh.forum.common.constants.*;
import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.common.entity.Report;
import org.jh.forum.common.entity.User;
import org.jh.forum.common.entity.UserDetail;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.client.AliyunGreenClient;
import org.jh.forum.server.manager.UserManager;
import org.jh.forum.server.mapper.ReportMapper;
import org.jh.forum.server.mapper.UserDetailMapper;
import org.jh.forum.server.mapper.UserMapper;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.jh.forum.server.config.service.AdminRegisterSwitchService.adminRegisterSwitch;


/**
 * @author MeaquaOWO
 */
@DubboService
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserDetailMapper userDetailMapper;

    @Resource
    private UserManager userManager;

    @Resource
    private ReportMapper reportMapper;

    @Resource
    private AliyunGreenClient aliyunGreenClient;

    @Override
    public GetUserProfileResponse getUserProfile(Long userId) {
        User userEntity = userMapper.selectById(userId);
        UserDetail detailEntity = userDetailMapper.selectById(userId);
        if (userEntity == null || detailEntity == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }

        GetUserProfileResponse resp = new GetUserProfileResponse();
        resp.setNickname(userEntity.getNickname());
        resp.setAvatar(userEntity.getAvatar());
        resp.setSignature(detailEntity.getSignature());
        resp.setProfile(detailEntity.getProfile());
        resp.setEmail(detailEntity.getEmail());
        resp.setGender(userEntity.getGender());
        resp.setIsSelf(userId.equals(StpUtil.getLoginIdAsLong()));
        resp.setBackground(detailEntity.getBackgroundImage());
        resp.setRealname(detailEntity.getRealnameVisible() ? userEntity.getRealname() : null);
        resp.setCollegeId(detailEntity.getCollegeVisible() ? userEntity.getCollegeId() : null);
        resp.setBirthday(detailEntity.getBirthdayVisible() ? detailEntity.getBirthday() : null);
        resp.setStudentId(detailEntity.getStudentIdVisible() ? userEntity.getStudentId() : null);
        return resp;
    }

    @Override
    public void updateUserProfile(UpdateUserDetailRequest request) {
        String text = StringUtils.joinWith(" ", request.getNickname(), request.getSignature(), request.getProfile());
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

        userEntity.setAvatar(request.getAvatar());
        userEntity.setNickname(request.getNickname());
        userEntity.setGender(request.getGender());
        userEntity.setCollegeId(request.getCollegeId());
        detailEntity.setSignature(request.getSignature());
        detailEntity.setEmail(request.getEmail());
        detailEntity.setBirthday(request.getBirthday());
        detailEntity.setProfile(request.getProfile());
        detailEntity.setBirthdayVisible(request.getBirthdayVisible());
        detailEntity.setCollegeVisible(request.getCollegeVisible());
        detailEntity.setRealnameVisible(request.getRealnameVisible());
        detailEntity.setStudentIdVisible(request.getStudentIdVisible());

        userMapper.updateById(userEntity);
        userDetailMapper.updateById(detailEntity);
    }

    @Override
    public void updateBackgroundImage(UpdateBackgroundImageRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        UserDetail detailEntity = userDetailMapper.selectById(userId);

        detailEntity.setBackgroundImage(request.getBackgroundImage());

        userDetailMapper.updateById(detailEntity);
    }

    @Override
    public GetNoticeSettingsResponse getNoticeSettings() {
        User user = userMapper.selectById(StpUtil.getLoginIdAsLong());
        return GetNoticeSettingsResponse.builder()
                .upvoteNotice(user.getUpvoteNotice())
                .commentNotice(user.getCommentNotice())
                .build();
    }

    @Override
    public void updateNoticeSettings(UpdateNoticeSettingsRequest request) {
        User user = userMapper.selectById(StpUtil.getLoginIdAsLong());
        user.setUpvoteNotice(request.getUpvoteNotice());
        user.setCommentNotice(request.getCommentNotice());
        userMapper.updateById(user);
    }

    @Override
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

    @Override
    public void muteUser(MuteUserRequest request) {
        userManager.muteUser(request.getId(), request.getHours());
    }

    @Override
    public GetUserDetailResponse getUserDetail(Long id) {
        User userEntity = userMapper.selectById(id);
        UserDetail detailEntity = userDetailMapper.selectById(id);
        if (userEntity == null || detailEntity == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }

        GetUserDetailResponse resp = new GetUserDetailResponse();
        resp.setNickname(userEntity.getNickname());
        resp.setAvatar(userEntity.getAvatar());
        resp.setBackground(detailEntity.getBackgroundImage());
        resp.setSignature(detailEntity.getSignature());
        resp.setEmail(detailEntity.getEmail());
        resp.setGender(userEntity.getGender());
        resp.setRealname(userEntity.getRealname());
        resp.setCollegeId(userEntity.getCollegeId());
        resp.setBirthday(detailEntity.getBirthday());
        resp.setCreatedAt(userEntity.getCreatedAt());
        resp.setStatus(getUserStatus(userEntity));
        resp.setProfile(detailEntity.getProfile());
        resp.setStudentId(userEntity.getStudentId());
        return resp;
    }

    @Override
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

    /**
     * 管理员注册 支持nacos开关，key动态配置
     */
    @Override
    public void adminRegister(AdminRegisterRequest request) {
        if (Objects.isNull(adminRegisterSwitch)) {
            log.info("未获取到 AdminRegister 配置");
            throw new ApiException(ExceptionEnum.NOT_FOUND_ERROR);
        }
        String secretKey = adminRegisterSwitch.getKey();
        Boolean adminRegisterEnabled = adminRegisterSwitch.getEnabled();
        // 接口是否下线
        if (!adminRegisterEnabled) {
            log.info("接口已下线");
            throw new ApiException(ExceptionEnum.NOT_FOUND_ERROR);
        }
        // 校验key
        if (StringUtils.isBlank(request.getKey()) || !request.getKey().equals(secretKey)) {
            log.error("密钥错误");
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getStudentId, request.getUsername()));
        if (user != null) {
            user.setPassword(BCrypt.hashpw(request.getPassword()));
            user.setRole(request.getUserType());
            userMapper.updateById(user);
            return;
        }
        user = User.builder()
                .nickname(userManager.generateRandomNickname())
                .realname("测试账号")
                .studentId(request.getUsername())
                .password(BCrypt.hashpw(request.getPassword()))
                .collegeId("000000")
                .gender(GenderEnum.UNKNOWN)
                .role(request.getUserType())
                .reportCount(0)
                .resolvedReportCount(0).build();
        userMapper.insert(user);
        userManager.insertUserDetail(user.getId());
    }

    private GetUserListElement buildUserListElement(User userEntity) {
        GetUserListElement element = new GetUserListElement();
        UserDetail detailEntity = userDetailMapper.selectById(userEntity.getId());
        element.setId(userEntity.getId());
        element.setAvatar(userEntity.getAvatar());
        element.setNickname(userEntity.getNickname());
        element.setStudentId(userEntity.getStudentId());
        element.setEmail(detailEntity.getEmail());
        element.setCreatedAt(userEntity.getCreatedAt());
        element.setReportCount(userEntity.getReportCount());
        element.setStatus(getUserStatus(userEntity));
        element.setMutedUntil(userEntity.getMutedUntil());
        return element;
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
        GetAdminListElement element = new GetAdminListElement();
        element.setId(userEntity.getId());
        element.setNickname(userEntity.getNickname());
        element.setType(userEntity.getRole());
        element.setCreatedAt(userEntity.getCreatedAt());

        LambdaQueryWrapper<Report> reportWrapper = new LambdaQueryWrapper<Report>()
                .ne(Report::getStatus, ReportStatusEnum.PENDING)
                .eq(Report::getReviewerId, userEntity.getId());
        element.setReportCount(Math.toIntExact(reportMapper.selectCount(reportWrapper)));
        return element;
    }
}
