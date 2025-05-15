package org.jh.forum.start.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * @author Patrick_Star
 * @date 2025/4/24
 */
@Configuration
@RequiredArgsConstructor
public class ProfileConfig {

    public static final String DEV = "dev";
    public static final String PROD = "prod";
    public static final String DEBUG = "debug";
    private final ApplicationContext context;

    /**
     * 获取当前激活的 Profile
     *
     * @return 当前激活的 Profile
     */
    public String getActiveProfile() {
        return context.getEnvironment().getActiveProfiles()[0];
    }

    /**
     * 判断当前是否为开发环境
     *
     * @return 是否为开发环境
     */
    public boolean isDev() {
        String activeProfile = getActiveProfile();
        return DEV.equals(activeProfile) || DEBUG.equals(activeProfile);
    }
}