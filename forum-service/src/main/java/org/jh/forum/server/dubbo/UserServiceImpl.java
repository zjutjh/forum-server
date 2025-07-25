package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.UserService;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.dto.UserDTO;
import org.jh.forum.common.dto.request.UserUpdateAdminRequest;
import org.jh.forum.common.dto.request.UserUpdateRequest;
import org.jh.forum.common.dto.response.UserDetailResponse;
import org.jh.forum.common.entity.User;
import org.jh.forum.common.entity.UserDetail;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.mapper.UserDetailMapper;
import org.jh.forum.server.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Objects;


@DubboService(version = "1.0.0")
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserDetailMapper userDetailMapper;

    @Override
    public UserDTO getUserById(Long userId) {
        User userEntity = userMapper.selectById(userId);
        UserDetail detailEntity = userDetailMapper.selectById(userId);
        return convertToDTO(userEntity, detailEntity);
    }

    @Override
    public void updateUserProfile(UserUpdateRequest dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        User userEntity = userMapper.selectById(userId);
        if (!Objects.isNull(dto.getAvatar())){
            userEntity.setAvatar(dto.getAvatar());
        }
        if (!Objects.isNull(dto.getNickname())) {
            userEntity.setNickname(dto.getNickname());
        }
        if (!Objects.isNull(dto.getGender())) {
            userEntity.setGender(dto.getGender());
        }

        userMapper.updateById(userEntity);
        // 更新 user_detail 表字段
        UserDetail detailEntity = userDetailMapper.selectById(userId);
        if (!Objects.isNull(dto.getSignature())) {
            detailEntity.setSignature(dto.getSignature());
        }
        if (!Objects.isNull(dto.getBirthday())) {
            detailEntity.setBirthday(dto.getBirthday());
        }
        if (!Objects.isNull(dto.getProfile())) {
            detailEntity.setProfile(dto.getProfile());
        }
        if (!Objects.isNull(dto.getBirthdayVisible())) {
            detailEntity.setBirthdayVisible(dto.getBirthdayVisible());
        }
        if (!Objects.isNull(dto.getCollegeVisible())) {
            detailEntity.setCollegeVisible(dto.getCollegeVisible());
        }
        if (!Objects.isNull(dto.getRealnameVisible())) {
            detailEntity.setRealnameVisible(dto.getRealnameVisible());
        }
        userDetailMapper.updateById(detailEntity);
    }

    // 转换 UserDTO
    private UserDTO convertToDTO(User userEntity, UserDetail detailEntity) {
        UserDTO dto = new UserDTO();
        dto.setId(userEntity.getId());
        dto.setNickname(userEntity.getNickname());
        dto.setRealname(userEntity.getRealname());
        dto.setStudentId(userEntity.getStudentId());
        dto.setGender(userEntity.getGender());
        dto.setAvatarUrl(userEntity.getAvatar());
        dto.setPhone(userEntity.getPhone());

        if (detailEntity != null) {
            dto.setSignature(detailEntity.getSignature());
            dto.setProfile(detailEntity.getProfile());
            dto.setEmail(detailEntity.getEmail());
            dto.setBirthday(detailEntity.getBirthday());
            dto.setBirthdayVisible(detailEntity.getBirthdayVisible());
            dto.setCollegeVisible(detailEntity.getCollegeVisible());
            dto.setRealnameVisible(detailEntity.getRealnameVisible());
        }
        return dto;
    }

    // 管理员更新：可修改所有字段
    public void updateUserProfileByAdmin(Long userId, UserUpdateAdminRequest dto) {

        User userEntity = userMapper.selectById(userId);

        if (!Objects.isNull(dto.getRealname())) {
            userEntity.setRealname(dto.getRealname());
        }
        if (!Objects.isNull(dto.getMutedUntil())) {
            userEntity.setMutedUntil(dto.getMutedUntil());
        }
        if (!Objects.isNull(dto.getStudentId())) {
            userEntity.setStudentId(dto.getStudentId());
        }
        if (!Objects.isNull(dto.getAvatar())){
            userEntity.setAvatar(dto.getAvatar());
        }
        if (!Objects.isNull(dto.getNickname())) {
            userEntity.setNickname(dto.getNickname());
        }
        if (!Objects.isNull(dto.getGender())) {
            userEntity.setGender(dto.getGender());
        }

        userMapper.updateById(userEntity);
        // 更新 user_detail 表字段
        UserDetail detailEntity = userDetailMapper.selectById(userId);
        if (!Objects.isNull(dto.getSignature())) {
            detailEntity.setSignature(dto.getSignature());
        }
        if (!Objects.isNull(dto.getBirthday())) {
            detailEntity.setBirthday(dto.getBirthday());
        }
        if (!Objects.isNull(dto.getProfile())) {
            detailEntity.setProfile(dto.getProfile());
        }
        if (!Objects.isNull(dto.getBirthdayVisible())) {
            detailEntity.setBirthdayVisible(dto.getBirthdayVisible());
        }
        if (!Objects.isNull(dto.getCollegeVisible())) {
            detailEntity.setCollegeVisible(dto.getCollegeVisible());
        }
        if (!Objects.isNull(dto.getRealnameVisible())) {
            detailEntity.setRealnameVisible(dto.getRealnameVisible());
        }
        userDetailMapper.updateById(detailEntity);
    }

    // 字段过滤
    public UserDetailResponse filterFields(UserDTO dto, Long targetUserId) {
        UserDetailResponse response = new UserDetailResponse();

        boolean isSelf = targetUserId == StpUtil.getLoginIdAsLong();

        boolean isAdmin = StpUtil.hasRole("super_admin") || StpUtil.hasRole("admin");

        BeanUtils.copyProperties(dto, response);
        response.setIsSelf(isSelf);
        if (!isSelf && !isAdmin ) {
            if (!dto.getCollegeVisible()) response.setCollegeId(null);
            if (!dto.getBirthdayVisible()) response.setBirthday(null);
            if (!dto.getRealnameVisible()) response.setRealname(null);
        }
        return response;
    }

    @Override
    public Boolean isUserMuted(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        LocalDateTime now = LocalDateTime.now();
        return user.getMutedUntil() != null && (user.getMutedUntil().isBefore(now) || user.getMutedUntil().isEqual(now));
    }

    @Override
    public void updateMuteStatus(Long userId, LocalDateTime mutedUntil) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        user.setMutedUntil(mutedUntil);
        userMapper.updateById(user);
    }
}
