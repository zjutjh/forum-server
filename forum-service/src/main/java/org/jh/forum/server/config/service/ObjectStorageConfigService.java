package org.jh.forum.server.config.service;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.ForumConfigNameConstantEnum;
import org.jh.forum.server.config.NacosConfigConfiguration;
import org.jh.forum.server.config.ObjectStorageConfig;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * @author SugarMGP
 * @date 2025/05/30
 */
@Slf4j
@Service
public class ObjectStorageConfigService extends BaseConfigService {
    public static ObjectStorageConfig config;

    @PostConstruct
    public void init() {
        NacosConfigConfiguration.addConfigService(
                ForumConfigNameConstantEnum.OBJECT_STORAGE_CONFIG.getName(),
                this
        );
    }

    @Override
    public void parseConfig(String configInfo) {
        log.info("[ObjectStorageConfig] 配置更新: {}", configInfo);
        try {
            config = JSON.parseObject(configInfo, ObjectStorageConfig.class);
            log.info("[ObjectStorageConfig] 解析成功: {}", JSON.toJSONString(config));
        } catch (Exception e) {
            log.error("[ObjectStorageConfig] 解析失败", e);
        }
    }
}
