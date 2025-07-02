package org.jh.forum.server.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置类
 * 
 * @author SituChengxiang
 */
@Configuration
@EnableCaching
public class cacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 设置最大缓存数量
                .maximumSize(1000)
                // 设置写缓存后过期时间
                .expireAfterWrite(30, TimeUnit.MINUTES)
                // 设置缓存容器的初始容量
                .initialCapacity(100)
                // 开启缓存统计
                .recordStats());
        return cacheManager;
    }
}