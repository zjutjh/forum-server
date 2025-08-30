package org.jh.forum.server.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.UserService;
import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.server.manager.UserManager;

import jakarta.annotation.Resource;


/**
 * @author MeaquaOWO
 */
@DubboService
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private UserManager userManager;

    @Override
    public GetUserProfileResponse getUserProfile(Long userId) {
        return userManager.getUserProfile(userId);
    }

    @Override
    public void updateUserProfile(UpdateUserProfileRequest request) {
        userManager.updateUserProfile(request);
    }

    @Override
    public void updateBackgroundImage(UpdateBackgroundImageRequest request) {
        userManager.updateBackgroundImage(request);
    }

    @Override
    public GetNoticeSettingsResponse getNoticeSettings() {
        return userManager.getNoticeSettings();
    }

    @Override
    public void updateNoticeSettings(UpdateNoticeSettingsRequest request) {
        userManager.updateNoticeSettings(request);
    }

    @Override
    public CheckMuteResponse checkMute() {
        return new CheckMuteResponse(userManager.checkMute());
    }

    @Override
    public BaseListResponse<GetUserListElement> getUserList(GetUserListRequest request) {
        return userManager.getUserList(request);
    }

    @Override
    public void muteUser(MuteUserRequest request) {
        userManager.muteUser(request.getId(), request.getHours());
    }

    @Override
    public AdminGetUserDetailResponse getUserDetail(Long id) {
        return userManager.getUserDetail(id);
    }

    @Override
    public BaseListResponse<GetAdminListElement> getAdminList(GetAdminListRequest request) {
        return userManager.getAdminList(request);
    }
}
