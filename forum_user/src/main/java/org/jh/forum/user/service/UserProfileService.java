package org.jh.forum.user.service;

import org.jh.forum.user.dto.request.PrivacySettingRequest;
import org.jh.forum.user.dto.response.UserProfileResponse;

public interface UserProfileService {
    UserProfileResponse getUserProfile(Long userId, Long currentUserId);
    void updatePrivacySettings(Long userId, PrivacySettingRequest request);
    UserProfileResponse getUserPosts(Long userId, Long currentUserId);
    UserProfileResponse getUserFollowers(Long userId, Long currentUserId);
}