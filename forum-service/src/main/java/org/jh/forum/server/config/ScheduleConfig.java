package org.jh.forum.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务配置
 * 启用Spring的定时任务支持
 *
 * @author SituChengxiang
 */
@Configuration
@EnableScheduling
public class ScheduleConfig {
    // 启用定时任务支持
    // 后续可以在这里添加更多定时任务相关的配置
}
