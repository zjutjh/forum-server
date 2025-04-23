package org.jh.forum.server.switchs;

import lombok.Data;

/**
 * @author Patrick_Star
 * @date 2025/4/19
 */
@Data
public class ForumSwitch {
    // redis锁过期时间，单位：秒
    public Integer redisLockExpireTime;
    // redis锁重试间隔时间
    public Long redisLockRetryInterval;
    // redis锁重试次数上限
    public Integer redisLockMaxRetryCount;
    // redis锁全局前缀
    public String redisLockGlobalPrefix;
}
