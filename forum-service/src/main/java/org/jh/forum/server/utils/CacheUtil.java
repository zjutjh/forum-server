package org.jh.forum.server.utils;

import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.entity.User;
import org.jh.forum.server.mapper.UserMapper;
import org.springframework.cache.annotation.Cacheable;
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
    
    /**
     * 根据用户ID获取用户昵称（带缓存）
     * 
     * @param #a0 用户ID的位置参数
     * @return 用户昵称
     */
    @Cacheable(value = "userNicknameCache", key = "#a0")
    public String getUsernameById(Long userId) {
        if (userId == null) {
            log.warn("获取用户昵称时，用户ID为空");
            return "unknown";
        }
        
        log.info("从数据库查询用户昵称，用户ID: {}", userId);
        
        try {
            User user = userMapper.selectById(userId);
            String nickname = user != null ? user.getNickname() : null;
            
            if (nickname == null || nickname.trim().isEmpty()) {
                nickname = "user_" + userId;
            }
            
            log.info("查询到用户昵称: {}, 用户ID: {}", nickname, userId);
            return nickname;
            
        } catch (Exception e) {
            log.error("查询用户昵称时发生异常，用户ID: {}", userId, e);
            return "user_" + userId;
        }
    }
    
    /**
     * 清除指定用户的昵称缓存
     * 
     * @param userId 用户ID
     */
    @CacheEvict(value = "userNicknameCache", key = "#userId")
    public void evictUserNicknameCache(Long userId) {
        log.info("清除用户昵称缓存，用户ID: {}", userId);
    }
    
    /**
     * 清除所有用户昵称缓存
     */
    @CacheEvict(value = "userNicknameCache", allEntries = true)
    public void evictAllUserNicknameCache() {
        log.info("清除所有用户昵称缓存");
    }
}