package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.dto.request.GetAdminListRequest;
import org.jh.forum.common.dto.request.GetUserListRequest;
import org.jh.forum.common.dto.request.MuteUserRequest;
import org.jh.forum.common.dto.request.UpdateUserDetailRequest;
import org.jh.forum.common.dto.response.*;

/**
 * @author MeaquaOWO
 */
public interface UserService {
    GetUserProfileResponse getUserProfile(Long userId);

    void updateUserProfile(UpdateUserDetailRequest dto);

    BaseListResponse<GetUserListElement> getUserList(GetUserListRequest request);

    void muteUser(MuteUserRequest request);

    GetUserDetailResponse getUserDetail(Long id);

    BaseListResponse<GetAdminListElement> getAdminList(GetAdminListRequest request);
}
