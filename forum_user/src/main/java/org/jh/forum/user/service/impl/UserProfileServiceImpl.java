package org.jh.forum.user.service.impl;

import org.jh.forum.user.config.RedisConfig;
import org.jh.forum.user.dto.request.PrivacySettingRequest;
import org.jh.forum.user.dto.response.UserProfileResponse;
import org.jh.forum.user.entity.User;
import org.jh.forum.user.repository.UserRepository;
import org.jh.forum.user.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public UserProfileServiceImpl(
            UserRepository userRepository,
            @Qualifier("userRedisTemplate") RedisTemplate<String, Object> redisTemplate) {

        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public UserProfileResponse getUserProfile(Long userId, Long currentUserId) {
        // 1. 尝试从Redis获取
        String cacheKey = "user_profile:" + userId;
        UserProfileResponse cached = (UserProfileResponse) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return applyPrivacyFilter(cached, currentUserId, userId);
        }

        // 2. 从数据库获取
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOpt.get();
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setJoinDate(user.getCreatedAt());
        response.setEmail(user.getEmail());
        response.setBio(user.getBio());

        // 3. 存入缓存
        redisTemplate.opsForValue().set(
                cacheKey,
                response,
                30,
                TimeUnit.MINUTES
        );

        return applyPrivacyFilter(response, currentUserId, userId);
    }

    private UserProfileResponse applyPrivacyFilter(UserProfileResponse response,
                                                   Long currentUserId,
                                                   Long targetUserId) {
        boolean isSelf = targetUserId.equals(currentUserId);

        // 如果不是本人，应用隐私设置 (简化示例，实际应从数据库获取隐私设置)
        if (!isSelf) {
            response.setEmail(null);
            response.setBio(null);
        }

        return response;
    }

    @Override
    public void updatePrivacySettings(Long userId, PrivacySettingRequest request) {
        // 更新隐私设置逻辑 (简化示例)
        // 实际应将设置保存到数据库

        // 清除缓存
        redisTemplate.delete("user_profile:" + userId);
    }

    @Override
    public UserProfileResponse getUserPosts(Long userId, Long currentUserId) {
        // 获取用户帖子逻辑
        UserProfileResponse response = new UserProfileResponse();
        response.setId(userId);
        // 添加帖子信息...
        return applyPrivacyFilter(response, currentUserId, userId);
    }

    @Override
    public UserProfileResponse getUserFollowers(Long userId, Long currentUserId) {
        // 获取用户粉丝逻辑
        UserProfileResponse response = new UserProfileResponse();
        response.setId(userId);
        // 添加粉丝信息...
        return applyPrivacyFilter(response, currentUserId, userId);
    }
}