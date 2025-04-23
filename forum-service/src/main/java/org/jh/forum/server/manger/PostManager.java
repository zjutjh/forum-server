package org.jh.forum.server.manger;

import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.annotation.WithLock;
import org.springframework.stereotype.Service;

/**
 * @author Patrick_Star
 * @date 2025/4/19
 */
@Service
@Slf4j
public class PostManager {
    @WithLock(params = {"uid"}, prefix = "genPostId")
    public String genPostId(String uid) throws InterruptedException {
        Thread.sleep(1000);
        return uid + System.currentTimeMillis();
    }
}
