package org.jh.forum.server.manager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.PostCategoryEnum;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author SugarMGP
 */
@Service
@AllArgsConstructor
@Slf4j
public class PostRankManager implements ApplicationListener<ApplicationReadyEvent> {
    public final String ACTIVE_POSTS_KEY = "active_posts";
    public final String HOT_RANK_KEY = "hot_rank";
    public final String HOT_RANK_TEMP_KEY = "hot_rank_temp";
    public final String LAST_COMPUTE_TIME_KEY = "hot_rank:last_compute_time";
    public final String LAST_CLEANUP_HOUR_KEY = "hot_rank:last_cleanup_hour";
    public final String LIKE = "like";
    public final String COMMENT = "comment";
    public final String VIEW = "view";
    public final String CATEGORY = "category";

    // 热榜计算间隔（单位：秒）
    public final long COMPUTE_INTERVAL_SECONDS = 15 * 60;

    private final RedisTemplate<String, Object> redisTemplate;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void recordAction(Long postId, PostCategoryEnum category, String type) {
        long currentTime = System.currentTimeMillis() / 1000;
        long hour = currentTime / 3600;
        String key = "post:" + postId + ":" + hour;

        // 累加字段
        redisTemplate.opsForHash().increment(key, type, 1);
        redisTemplate.opsForHash().put(key, CATEGORY, category.getValue());

        // 设置24小时过期（动态）
        long expire = (hour + 24) * 3600 - currentTime;
        redisTemplate.expire(key, Duration.ofSeconds(expire));

        // 添加活跃帖子记录
        redisTemplate.opsForZSet().add(ACTIVE_POSTS_KEY, postId.toString(), currentTime);
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        log.info("[PostRankManager] 应用启动完成，开始调度热榜计算任务");
        scheduler.scheduleAtFixedRate(() -> {
            try {
                computeHotRank();
            } catch (Exception e) {
                log.error("[PostRankManager] 热榜计算异常", e);
            }
        }, 5, COMPUTE_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    private void computeHotRank() {
        long currentTime = System.currentTimeMillis() / 1000;

        Boolean lockAcquired = redisTemplate.opsForValue()
                .setIfAbsent(LAST_COMPUTE_TIME_KEY, currentTime, COMPUTE_INTERVAL_SECONDS - 5, TimeUnit.SECONDS);
        if (lockAcquired == null || !lockAcquired) {
            log.info("[PostRankManager] 未获取到分布式锁，跳过热榜计算");
            return;
        }

        log.info("[PostRankManager] 开始计算帖子热度值");
        long currentHour = currentTime / 3600;
        long threshold = currentTime - 86400;

        // 清理24小时外的帖子（每小时执行一次）
        lockAcquired = redisTemplate.opsForValue()
                .setIfAbsent(LAST_CLEANUP_HOUR_KEY, currentHour, 1, TimeUnit.HOURS);
        if (Boolean.TRUE.equals(lockAcquired)) {
            log.info("[PostRankManager] 清理过期帖子");
            redisTemplate.opsForZSet().removeRangeByScore(ACTIVE_POSTS_KEY, 0, threshold - 1);
        }

        // 获取24小时内活跃帖子
        Set<Object> postIds = redisTemplate.opsForZSet().rangeByScore(ACTIVE_POSTS_KEY, threshold, currentTime);
        if (postIds == null || postIds.isEmpty()) {
            return;
        }

        // 对每个帖子计算24小时热度值
        for (Object postId : postIds) {
            int likeSum = 0, commentSum = 0, viewSum = 0;
            String category = null;

            for (int i = 0; i < 24; i++) {
                long hour = (currentTime / 3600) - i;
                String key = "post:" + postId + ":" + hour;

                Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
                likeSum += toInt(map.get(LIKE));
                commentSum += toInt(map.get(COMMENT));
                viewSum += toInt(map.get(VIEW));

                if (category == null && map.containsKey(CATEGORY)) {
                    category = map.get(CATEGORY).toString();
                }
            }

            double score = likeSum + commentSum * 2 + viewSum * 0.1;
            redisTemplate.opsForZSet().add(HOT_RANK_TEMP_KEY, postId, score);
            if (category != null) {
                redisTemplate.opsForZSet().add(HOT_RANK_TEMP_KEY + ":" + category, postId, score);
            }
        }

        // 替换总榜
        if (redisTemplate.hasKey(HOT_RANK_TEMP_KEY)) {
            redisTemplate.rename(HOT_RANK_TEMP_KEY, HOT_RANK_KEY);
        }

        // 替换每个分类榜
        for (PostCategoryEnum category : PostCategoryEnum.values()) {
            String tempKey = HOT_RANK_TEMP_KEY + ":" + category.getValue();
            String realKey = HOT_RANK_KEY + ":" + category.getValue();
            if (redisTemplate.hasKey(tempKey)) {
                redisTemplate.rename(tempKey, realKey);
            }
        }
    }

    public List<Long> getTopFiveHotPostIds() {
        Set<Object> topPosts = redisTemplate.opsForZSet().reverseRange(HOT_RANK_KEY, 0, 4);
        if (topPosts == null || topPosts.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> result = new ArrayList<>();
        for (Object obj : topPosts) {
            if (obj != null) {
                result.add(Long.parseLong(obj.toString()));
            }
        }
        return result;
    }


    public PageResult<Long> getHotPostIds(PostCategoryEnum category, int page, int pageSize) {
        int start = (page - 1) * pageSize;
        int end = start + pageSize - 1;

        String key;
        if (category == null) {
            key = HOT_RANK_KEY;
        } else {
            key = HOT_RANK_KEY + ":" + category.getValue();
        }

        // 获取总数
        Long total = redisTemplate.opsForZSet().zCard(key);
        if (total == null || total == 0) {
            return new PageResult<>(Collections.emptyList(), 0);
        }

        // 倒序分页查询帖子ID
        Set<ZSetOperations.TypedTuple<Object>> result = redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, start, end);
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

    public void removePost(Long postId) {
        redisTemplate.opsForZSet().remove(ACTIVE_POSTS_KEY, postId.toString());
        redisTemplate.opsForZSet().remove(HOT_RANK_KEY, postId.toString());
        redisTemplate.opsForZSet().remove(HOT_RANK_TEMP_KEY, postId.toString());

        for (PostCategoryEnum category : PostCategoryEnum.values()) {
            redisTemplate.opsForZSet().remove(HOT_RANK_KEY + ":" + category.getValue(), postId.toString());
            redisTemplate.opsForZSet().remove(HOT_RANK_TEMP_KEY + ":" + category.getValue(), postId.toString());
        }
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
