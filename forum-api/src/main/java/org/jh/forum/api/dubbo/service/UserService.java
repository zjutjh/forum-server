package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.dto.request.UpdateUserDetailRequest;
import org.jh.forum.common.dto.response.GetUserDetailResponse;

/**
 * @author MeaquaOWO
 */
public interface UserService {
    GetUserDetailResponse getUserProfile(Long userId);

    void updateUserProfile(UpdateUserDetailRequest dto);
}
