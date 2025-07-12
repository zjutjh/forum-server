package org.jh.forum.server.manger;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jh.forum.common.annotation.WithLock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author SugarMGP
 */
@Service
@AllArgsConstructor
public class PostRankManager {
    public final String ACTIVE_POSTS_KEY = "active_posts";
    public final String HOT_RANK_KEY = "hot_rank";
    public final String HOT_RANK_TEMP_KEY = "hot_rank_temp";
    public final String LIKE = "like";
    public final String COMMENT = "comment";
    public final String VIEW = "view";
    private final RedisTemplate<String, Object> redisTemplate;

    // 上次清理的小时戳（单位：小时）
    private final AtomicLong lastCleanupHour = new AtomicLong(-1);

    public void recordAction(Long postId, String type) {
        long currentTime = System.currentTimeMillis() / 1000;
        long hour = currentTime / 3600;
        String key = "post:" + postId + ":" + hour;

        // 累加字段
        redisTemplate.opsForHash().increment(key, type, 1);

        // 设置24小时过期（动态）
        long expire = (hour + 24) * 3600 - currentTime;
        redisTemplate.expire(key, Duration.ofSeconds(expire));

        // 添加活跃帖子记录
        redisTemplate.opsForZSet().add(ACTIVE_POSTS_KEY, postId.toString(), currentTime);
    }

    @WithLock(prefix = "hot_rank")
    @Scheduled(cron = "0 */15 * * * *")
    public void computeHotRank() {
        long currentTime = System.currentTimeMillis() / 1000;
        long currentHour = currentTime / 3600;
        long threshold = currentTime - 86400;

        // 清理24小时外的帖子（每小时执行一次）
        if (lastCleanupHour.get() != currentHour) {
            redisTemplate.opsForZSet().removeRangeByScore(ACTIVE_POSTS_KEY, 0, threshold - 1);
            lastCleanupHour.set(currentHour);
        }

        // 获取24小时内活跃帖子
        Set<Object> postIds = redisTemplate.opsForZSet().rangeByScore(ACTIVE_POSTS_KEY, threshold, currentTime);
        if (postIds == null || postIds.isEmpty()) {
            return;
        }

        // 对每个帖子计算24小时热度值
        for (Object postId : postIds) {
            int likeSum = 0, commentSum = 0, viewSum = 0;
            for (int i = 0; i < 24; i++) {
                long hour = (currentTime / 3600) - i;
                String key = "post:" + postId + ":" + hour;

                Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
                likeSum += toInt(map.get(LIKE));
                commentSum += toInt(map.get(COMMENT));
                viewSum += toInt(map.get(VIEW));
            }

            double score = likeSum + commentSum * 2 + viewSum * 0.1;
            redisTemplate.opsForZSet().add(HOT_RANK_TEMP_KEY, postId, score);
        }

        // 替换排行榜
        redisTemplate.rename(HOT_RANK_TEMP_KEY, HOT_RANK_KEY);
    }

    public PageResult<Long> getHotPostIds(int page, int pageSize) {
        int start = (page - 1) * pageSize;
        int end = start + pageSize - 1;

        // 获取总数
        Long total = redisTemplate.opsForZSet().zCard(HOT_RANK_KEY);
        if (total == null || total == 0) {
            return new PageResult<>(Collections.emptyList(), 0);
        }

        // 倒序分页查询帖子ID
        Set<ZSetOperations.TypedTuple<Object>> result = redisTemplate.opsForZSet()
                .reverseRangeWithScores(HOT_RANK_KEY, start, end);
        if (result == null || result.isEmpty()) {
            return new PageResult<>(Collections.emptyList(), total);
        }

        // 提取帖子ID列表
        List<Long> postIds = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Object> tuple : result) {
            Object value = tuple.getValue();
            if (value != null) {
                postIds.add(Long.parseLong(value.toString()));
            }
        }
        return new PageResult<>(postIds, total);
    }


    private int toInt(Object value) {
        return value == null ? 0 : Integer.parseInt(value.toString());
    }

    @Data
    @AllArgsConstructor
    public static class PageResult<T> {
        private List<T> records;
        private long total;
    }
}
