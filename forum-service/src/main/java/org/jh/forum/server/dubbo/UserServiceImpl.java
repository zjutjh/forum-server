package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.UserService;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.ReportStatusEnum;
import org.jh.forum.common.constants.UserStatusEnum;
import org.jh.forum.common.constants.UserTypeEnum;
import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.common.entity.Report;
import org.jh.forum.common.entity.User;
import org.jh.forum.common.entity.UserDetail;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.manager.UserManager;
import org.jh.forum.server.mapper.ReportMapper;
import org.jh.forum.server.mapper.UserDetailMapper;
import org.jh.forum.server.mapper.UserMapper;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;


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
        resp.setCollege(detailEntity.getCollegeVisible() ? userEntity.getCollege() : null);
        resp.setBirthday(detailEntity.getBirthdayVisible() ? detailEntity.getBirthday() : null);
        return resp;
    }

    @Override
    public void updateUserProfile(UpdateUserDetailRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        User userEntity = userMapper.selectById(userId);
        UserDetail detailEntity = userDetailMapper.selectById(userId);

        userEntity.setAvatar(request.getAvatar());
        userEntity.setNickname(request.getNickname());
        userEntity.setGender(request.getGender());
        userEntity.setCollege(request.getCollege());
        detailEntity.setSignature(request.getSignature());
        detailEntity.setEmail(request.getEmail());
        detailEntity.setBirthday(request.getBirthday());
        detailEntity.setProfile(request.getProfile());
        detailEntity.setBirthdayVisible(request.getBirthdayVisible());
        detailEntity.setCollegeVisible(request.getCollegeVisible());
        detailEntity.setRealnameVisible(request.getRealnameVisible());

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
    public BaseListResponse<GetUserListElement> getUserList(GetUserListRequest request) {
        IPage<User> page = new Page<>(request.getPage(), request.getPageSize());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
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
        resp.setCollege(userEntity.getCollege());
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
