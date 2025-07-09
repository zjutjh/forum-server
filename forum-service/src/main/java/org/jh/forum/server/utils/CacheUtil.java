package org.jh.forum.server.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jh.forum.common.entity.User;
import org.jh.forum.server.mapper.UserMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 缓存工具
 * 
 * @author SituChengxiang
 */
@Slf4j
@Component
public class CacheUtil {

    @Resource
    private UserMapper userMapper;

    @Resource
    private CacheManager cacheManager;

    /**
     * 根据用户ID获取用户昵称（带缓存）
     * 
     * @param #a0 用户ID的位置参数
     * @return 用户昵称
     */
    @Cacheable(value = "userNicknameCache", key = "#a0")
    public String getUsernameById(Long userId) {
        if (userId == null) {
            log.warn("获取用户昵称时, 用户ID为空");
            return "unknown";
        }

        log.info("从数据库查询用户昵称, 用户ID: {}", userId);

        try {
            User user = userMapper.selectById(userId);
            String nickname = user != null ? user.getNickname() : null;

            if (nickname == null || nickname.trim().isEmpty()) {
                nickname = "user_" + userId;
            }

            log.info("查询到用户昵称: {}, 用户ID: {}", nickname, userId);
            return nickname;

        } catch (Exception e) {
            log.error("查询用户昵称时发生异常, 用户ID: {}", userId, e);
            return "user_" + userId;
        }
    }

    /**
     * 根据用户ID批量获取用户昵称
     * 
     * @param userId
     */
    public Map<Long, String> getUsernamesByIds(Set<Long> userIds) {
        log.info("批量获取用户昵称, 用户ID列表: {}", userIds);
        Map<Long, String> result = new HashMap<>(userIds.size());
        if (userIds == null || userIds.isEmpty()) {
            return result;
        }

        // 1. 批量查缓存
        Cache cache = cacheManager.getCache("userNicknameCache");
        Set<Long> missedIds = new HashSet<>();
        for (Long userId : userIds) {
            String nickname = cache != null ? cache.get(userId, String.class) : null;
            if (nickname != null) {
                result.put(userId, nickname);
            } else {
                missedIds.add(userId);
            }
        }

        // 2. 手动循环查数据库
        Map<Long, String> dbMap = new HashMap<>();
        for (Long id : missedIds) {
            User user = null;
            try {
                user = userMapper.selectById(id);
            } catch (Exception e) {
                log.error("批量查数据库时异常, 用户ID: {}", id, e);
            }
            String nickname = (user != null && user.getNickname() != null && !user.getNickname().trim().isEmpty())
                    ? user.getNickname()
                    : "user_" + id;
            dbMap.put(id, nickname);
        }

        // 3. 查不到的userId也要兜底
        for (Long id : missedIds) {
            String nickname = dbMap.getOrDefault(id, "user_" + id);
            result.put(id, nickname);
            // 4. 回填缓存
            if (cache != null) {
                cache.put(id, nickname);
            }
        }

        return result;
    }

    /**
     * 清除指定用户的昵称缓存
     * 
     * @param userId 用户ID
     */
    @Deprecated
    @CacheEvict(value = "userNicknameCache", key = "#userId")
    public void evictUserNicknameCache(Long userId) {
        log.info("清除用户昵称缓存，用户ID: {}", userId);
    }

    /**
     * 清除所有用户昵称缓存
     */
    @Deprecated
    @CacheEvict(value = "userNicknameCache", allEntries = true)
    public void evictAllUserNicknameCache() {
        log.info("清除所有用户昵称缓存");
    }
}