package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.dto.UserDTO;
import org.jh.forum.common.dto.request.UserUpdateRequest;
import org.jh.forum.common.dto.response.UserDetailResponse;
import org.jh.forum.common.dto.request.UserUpdateAdminRequest;

import java.time.LocalDateTime;

public interface UserService {

    UserDTO getUserById(Long userId);

    void updateUserProfile(UserUpdateRequest dto);

    UserDetailResponse filterFields(UserDTO targetUser, Long targetUserId);

    Boolean isUserMuted(Long userId); // 查询禁言状态

    void updateMuteStatus(Long userId, LocalDateTime mutedUntil);

    void updateUserProfileByAdmin(Long userId, UserUpdateAdminRequest dto);

}
