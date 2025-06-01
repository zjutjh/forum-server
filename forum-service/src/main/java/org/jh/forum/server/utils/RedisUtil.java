package org.jh.forum.server.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.server.config.service.ForumSwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.SetParams;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * @author Patrick_Star
 * @date 2025/4/19
 */
@Component
@Slf4j
public class RedisUtil {
    public static final String LOCK_SUCCESS = "OK";

    public static final String LOCK_FAIL = "LOCK_FAIL";
    private final ThreadLocal<Map<String, LockInfo>> currentValue = ThreadLocal.withInitial(() -> new HashMap<>(8));
    private final RedisProperties redisProperties;
    private JedisPool jedisPool;

    @Autowired
    public RedisUtil(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    static <V> V getOrElse(Callable<V> callable, V val) {
        V result = null;
        try {
            result = callable.call();
        } catch (Exception e) {
            log.error("callable error", e);
        }
        return Objects.isNull(result) ? val : result;
    }

    public static boolean isStatusOk(String result) {
        return LOCK_SUCCESS.equalsIgnoreCase(result);
    }

    @PostConstruct
    public void init() {
        // 配置连接池参数
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        // 最大连接数
        poolConfig.setMaxTotal(10);

        // 最大空闲连接数
        poolConfig.setMaxIdle(5);

        // 最小空闲连接数
        poolConfig.setMinIdle(1);

        // 创建 JedisPool 实例
        jedisPool = new JedisPool(poolConfig,
                redisProperties.getHost(),
                redisProperties.getPort(),
                2000,
                redisProperties.getUsername(),
                redisProperties.getPassword(),
                redisProperties.getDatabase()
        );
    }

    /**
     * 加载缓存，如果缓存不存在，则调用 provider 获取数据，并设置缓存
     *
     * @param key           缓存 key
     * @param expireSeconds 过期时间，单位秒
     * @param provider      找回方法
     * @return
     */
    public String load(String key, Long expireSeconds, Supplier<String> provider) {
        String val = get(key, null);
        if (Objects.isNull(val)) {
            String newVal = provider.get();
            if (Objects.nonNull(newVal)) {
                setWithExpire(key, newVal, expireSeconds);
            }
            val = newVal;
        }
        return val;
    }

    /**
     * 加载缓存，支持返回泛型数据
     *
     * @param key
     * @param expireSeconds
     * @param provider
     * @param type
     * @param <T>
     * @return
     */
    public <T> T smartLoad(String key, Long expireSeconds, Supplier<T> provider, Type type) {
        String val = get(key, null);
        if (Objects.isNull(val)) {
            T newVal = provider.get();
            if (Objects.nonNull(newVal)) {
                setWithExpire(key, JSON.toJSONString(newVal), expireSeconds);
            }
            return newVal;
        } else {
            return JSON.parseObject(val, type);
        }
    }

    public <T> List<T> smartLoadList(String key, Long expireSeconds, Supplier<List<T>> provider, Class<T> clazz) {
        String val = get(key, null);
        if (Objects.isNull(val)) {
            List<T> newVal = provider.get();
            if (Objects.nonNull(newVal)) {
                setWithExpire(key, JSON.toJSONString(newVal), expireSeconds);
            }
            return newVal;
        } else {
            return JSON.parseObject(val, new TypeReference<List<T>>(clazz) {
            });
        }
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public void del(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            getOrElse(() -> jedis.del(key), null);
        } catch (Exception e) {
            log.error("redis del error", e);
        }
    }

    /**
     * 批量删除缓存
     *
     * @param keys
     */
    public void del(String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            getOrElse(() -> jedis.del(keys), null);
        } catch (Exception e) {
            log.error("redis del error", e);
        }
    }

    /**
     * 设置缓存，并设置过期时间
     *
     * @param key
     * @param value
     * @param expireSeconds
     */
    public String setWithExpire(String key, String value, Long expireSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            return getOrElse(() -> jedis.setex(key, expireSeconds, value), null);
        } catch (Exception e) {
            log.error("redis setex error", e);
            return null;
        }
    }

    public void set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            getOrElse(() -> jedis.set(key, value), null);
        } catch (Exception e) {
            log.error("redis set error", e);
        }
    }

    /**
     * 过期时间单位是毫秒
     *
     * @param key
     * @param value
     * @param expireMilliseconds
     * @param defaultValue
     * @return
     */
    public String setNXWithExpire(String key, String value, Integer expireMilliseconds, String defaultValue) {
        try (Jedis jedis = jedisPool.getResource()) {
            SetParams setParams = new SetParams();
            setParams.nx();
            setParams.px(expireMilliseconds);
            return getOrElse(() -> jedis.set(key, value, setParams), defaultValue);
        } catch (Exception e) {
            log.error("redis setnx error", e);
            return defaultValue;
        }
    }

    /**
     * 获取缓存，支持默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String get(String key, String defaultValue) {
        try (Jedis jedis = jedisPool.getResource()) {
            return getOrElse(() -> jedis.get(key), defaultValue);
        } catch (Exception e) {
            log.error("redis get error", e);
            return defaultValue;
        }
    }

    public List<String> multiGetOrElse(List<String> keys, List<String> defaultValue) {
        try (Jedis jedis = jedisPool.getResource()) {
            return getOrElse(() -> jedis.mget(keys.toArray(new String[0])), defaultValue);
        } catch (Exception e) {
            log.error("redis mget error", e);
            return defaultValue;
        }
    }

    public <T> T getObject(String key, Type type) {
        String val = get(key, null);
        if (Objects.isNull(val)) {
            return null;
        } else {
            return JSON.parseObject(val, type);
        }
    }

    public <T> void setObject(String key, Long expireSeconds, Supplier<T> provider) {
        T val = provider.get();
        if (Objects.nonNull(val)) {
            setWithExpire(key, JSON.toJSONString(val), expireSeconds);
        }
    }

    /**
     * 向队列左端写入数据，队列不存在则自动新建队列
     *
     * @param key
     * @param value
     * @param expire
     * @return
     */
    public Long lPush(String key, String value, Long expire) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.expire(key, expire);
            return getOrElse(() -> jedis.lpush(key, value), 0L);
        } catch (Exception e) {
            log.error("redis lpush error", e);
            return 0L;
        }
    }

    /**
     * 向队列右端写入数据，队列不存在则自动新建队列
     *
     * @param key
     * @param value
     * @param expire
     * @return
     */
    public Long rPush(String key, String value, Long expire) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.expire(key, expire);
            return getOrElse(() -> jedis.rpush(key, value), 0L);
        } catch (Exception e) {
            log.error("redis rpush error", e);
            return 0L;
        }
    }

    public List<String> lRange(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            log.error("redis lrange error", e);
            return null;
        }
    }

    public List<String> lRange(String key) {
        return lRange(key, 0, -1);
    }

    public Long lLen(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.llen(key);
        } catch (Exception e) {
            log.error("redis llen error", e);
            return null;
        }
    }

    /**
     * 上锁，可重入，仅用于切片少量线程场景，切勿用于高并发的复用线程池中
     *
     * @param key
     * @return
     */
    public boolean tryLockWithReentrant(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            String lockValue = jedis.get(key);
            if (Objects.isNull(lockValue)) {
                String value = genValue();
                SetParams setParams = buildRedisLockSetParams();
                String result = getOrElse(() -> jedis.set(key, value, setParams), LOCK_FAIL);
                if (isStatusOk(result)) {
                    setLockInfo(key, value);
                    return true;
                } else {
                    return false;
                }
            } else if (lockValue.equals(getLockValue(key))) {
                // 重入
                reentrantLock(key);
                return true;
            }
        } catch (Exception e) {
            log.error("redis tryLockWithReentrant error key:{}", key, e);
        }
        return false;
    }

    /**
     * 轻量级锁
     *
     * @param key
     * @param expireTime
     * @return
     */
    public boolean tryLock(String key, Long expireTime) {
        try (Jedis jedis = jedisPool.getResource()) {
            String lockValue = jedis.get(key);
            if (Objects.nonNull(lockValue)) {
                return false;
            }
            String value = genValue();
            SetParams setParams = buildRedisLockSetParams(expireTime);
            String result = getOrElse(() -> jedis.set(key, value, setParams), LOCK_FAIL);
            return isStatusOk(result);
        } catch (Exception e) {
            log.error("redis tryLock error key:{}", key, e);
        }
        return false;
    }

    /**
     * 释放轻量级锁
     *
     * @param key
     */
    public void releaseLock(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            getOrElse(() -> jedis.del(key), null);
        } catch (Exception e) {
            log.error("redis releaseLock error key:{}", key, e);
        }
    }

    public void releaseLockWithReentrant(String key) {
        Map<String, LockInfo> keyValueMap = currentValue.get();
        LockInfo lockInfo = keyValueMap.get(key);
        if (Objects.nonNull(lockInfo)) {
            int count = lockInfo.getCount() - 1;
            if (count > 0) {
                lockInfo.setCount(count);
            } else {
                try (Jedis jedis = jedisPool.getResource()) {
                    keyValueMap.remove(key);
                    getOrElse(() -> jedis.del(key), 0L);
                } catch (Exception e) {
                    log.error("redis releaseLockWithReentrant error key:{}", key, e);
                }
            }
        }
    }

    private SetParams buildRedisLockSetParams(Long expireTime) {
        return SetParams.setParams().nx().px(expireTime);
    }

    private SetParams buildRedisLockSetParams() {
        return SetParams.setParams().nx().px(ForumSwitchService.forumSwitch.redisLockExpireTime);
    }

    private String genValue() {
        try {
            return InetAddress.getLocalHost().getHostName() + ":" + UUID.randomUUID() + ":" + System.currentTimeMillis();
        } catch (UnknownHostException e) {
            log.error("genValue error", e);
            return "unknown:" + UUID.randomUUID();
        }
    }

    private String getLockValue(String key) {
        Map<String, LockInfo> keyValueMap = currentValue.get();
        LockInfo lockInfo = keyValueMap.get(key);
        if (Objects.nonNull(lockInfo)) {
            return lockInfo.getValue();
        }
        return null;
    }

    private void reentrantLock(String key) {
        Map<String, LockInfo> keyValueMap = currentValue.get();
        LockInfo lockInfo = keyValueMap.get(key);
        if (Objects.nonNull(lockInfo)) {
            lockInfo.setCount(lockInfo.getCount() + 1);
        }
    }

    private void setLockInfo(String key, String value) {
        Map<String, LockInfo> keyValueMap = currentValue.get();
        LockInfo lockInfo = new LockInfo();
        lockInfo.setValue(value);
        lockInfo.setCount(1);
        keyValueMap.put(key, lockInfo);
        currentValue.set(keyValueMap);
    }

    @Getter
    @Setter
    public static class LockInfo {
        /**
         * 加锁的值
         */
        private String value;

        /**
         * 重入计数，计数为 0 时可以释放锁
         */
        private int count;
    }
}
