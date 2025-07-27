package org.jh.forum.server.utils;

import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * @author SugarMGP
 */
@Slf4j
public class AsyncUtil {
    public static void runAsyncWithLogging(Runnable task) {
        String tokenValue = StpUtil.getTokenValue();
        CompletableFuture.runAsync(() -> SaTokenContextMockUtil.setMockContext(() -> {
            StpUtil.setTokenValueToStorage(tokenValue);
            task.run();
        })).whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("Async task failed", ex);
            }
        });
    }
}
