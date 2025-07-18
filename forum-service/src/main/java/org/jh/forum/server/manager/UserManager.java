package org.jh.forum.server.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.dto.UserInfoDTO;
import org.jh.forum.common.entity.User;
import org.jh.forum.common.entity.UserDetail;
import org.jh.forum.server.mapper.UserDetailMapper;
import org.jh.forum.server.mapper.UserMapper;
import org.jh.forum.server.utils.RedisUtil;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author MangoGovo
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserManager {
    // 缓存配置常量
    private static final String CACHE_PREFIX = "user:nickname:";
    private static final Long CACHE_EXPIRE_SECONDS = 3600L; // 1小时过期
    private static final Long NEGATIVE_CACHE_EXPIRE_SECONDS = 300L; // 缓存空值， 5分钟更新
    private final UserMapper userMapper;
    private final UserDetailMapper userDetailMapper;
    private final RedisUtil redisUtil;

    public void insertUserDetail(Long userId) {
        userDetailMapper.insert(UserDetail.builder()
                .userId(userId)
                .email("")
                .profile("")
                .signature("")
                .birthday(null)
                .birthdayVisible(true)
                .collegeVisible(true)
                .realnameVisible(true)
                .build());
    }

    public List<String> getRoleList(Long userId) {
        String role = userMapper.selectById(userId).getRole().getValue();
        return List.of(role);
    }

    public UserInfoDTO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        return UserInfoDTO.builder()
                .id(userId)
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
    }

    /**
     * 批量获取用户昵称（）
     *
     * @param userIds 用户ID集合
     * @return 用户ID到昵称的映射
     */
    public Map<Long, String> getUsernamesByIds(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }

        log.debug("批量查询用户昵称, userIds: {}", userIds);

        Map<Long, String> result = new HashMap<>();
        Set<Long> uncachedUserIds = new HashSet<>();

        // 1. 从Redis缓存中批量获取
        for (Long userId : userIds) {
            String cacheKey = CACHE_PREFIX + userId;
            String nickname = redisUtil.get(cacheKey, null);
            if (nickname != null) {
                result.put(userId, nickname);
            } else {
                uncachedUserIds.add(userId);
            }
        }

        // 2. 从数据库查询未缓存的用户
        if (!uncachedUserIds.isEmpty()) {
            try {
                Map<Long, String> dbResult = getUsernamesByIdsFromDB(uncachedUserIds);

                // 3. 将查询结果写入Redis缓存
                for (Map.Entry<Long, String> entry : dbResult.entrySet()) {
                    Long userId = entry.getKey();
                    String nickname = entry.getValue();

                    String cacheKey = CACHE_PREFIX + userId;
                    redisUtil.setWithExpire(cacheKey, nickname, CACHE_EXPIRE_SECONDS);

                    result.put(userId, nickname);
                }

                // 4. 处理数据库中不存在的用户ID
                for (Long userId : uncachedUserIds) {
                    if (!result.containsKey(userId)) {
                        // 缓存空值，避免重复查询
                        String cacheKey = CACHE_PREFIX + userId;
                        redisUtil.setWithExpire(cacheKey, "", NEGATIVE_CACHE_EXPIRE_SECONDS);
                        result.put(userId, "");
                    }
                }

                log.debug("从数据库查询{}个用户昵称并缓存", uncachedUserIds.size());
            } catch (Exception e) {
                log.error("批量查询用户昵称失败, uncachedUserIds: {}", uncachedUserIds, e);
                // 出错时为未缓存的用户ID返回空字符串
                for (Long userId : uncachedUserIds) {
                    result.put(userId, "");
                }
            }
        }

        log.debug("批量查询用户昵称完成, 总计: {}, 缓存命中: {}, 数据库查询: {}",
                userIds.size(), userIds.size() - uncachedUserIds.size(), uncachedUserIds.size());

        return result;
    }

    /**
     * 从数据库批量获取用户昵称
     *
     * @param userIds 用户ID集合
     * @return 用户ID到昵称的映射
     */
    private Map<Long, String> getUsernamesByIdsFromDB(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, userIds);
        List<User> users = userMapper.selectList(queryWrapper);

        return users.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        User::getNickname,
                        (existing, replacement) -> existing
                ));
    }

    /**
     * 清理指定用户的缓存
     *
     * @param userId 用户ID
     */
    public void evictUserNicknameCache(Long userId) {
        if (userId == null) {
            return;
        }

        // 清理Redis缓存
        String cacheKey = CACHE_PREFIX + userId;
        redisUtil.del(cacheKey);

        log.debug("清理用户昵称缓存, userId: {}", userId);
    }

    /**
     * 批量清理用户缓存
     *
     * @param userIds 用户ID集合
     */
    public void evictUserNicknameCache(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        // 清理Redis缓存
        String[] cacheKeys = userIds.stream()
                .map(userId -> CACHE_PREFIX + userId)
                .toArray(String[]::new);
        redisUtil.del(cacheKeys);

        log.debug("批量清理用户昵称缓存, userIds: {}", userIds);
    }
}
