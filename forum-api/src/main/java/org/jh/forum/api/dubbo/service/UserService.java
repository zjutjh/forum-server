package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;

/**
 * @author MeaquaOWO
 */
public interface UserService {
    GetUserProfileResponse getUserProfile(Long userId);

    void updateUserProfile(UpdateUserProfileRequest dto);

    BaseListResponse<GetUserListElement> getUserList(GetUserListRequest request);

    void muteUser(MuteUserRequest request);

    AdminGetUserDetailResponse getUserDetail(Long id);

    BaseListResponse<GetAdminListElement> getAdminList(GetAdminListRequest request);

    void adminRegister(AdminRegisterRequest request);

    void updateBackgroundImage(UpdateBackgroundImageRequest request);

    GetNoticeSettingsResponse getNoticeSettings();

    void updateNoticeSettings(UpdateNoticeSettingsRequest request);

    CheckMuteResponse checkMute();
}
