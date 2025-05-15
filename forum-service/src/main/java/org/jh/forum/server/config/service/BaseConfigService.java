package org.jh.forum.server.config.service;

import lombok.extern.slf4j.Slf4j;

/**
 * 解析配置数据的基类
 *
 * @author Patrick_Star
 * @date 2025/4/4
 */
@Slf4j
public abstract class BaseConfigService {
    public abstract void parseConfig(String configInfo);
}
