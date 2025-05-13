package org.jh.forum.server.config;

import jakarta.annotation.Resource;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.ForumConfigNameConstantEnum;
import org.jh.forum.server.config.service.BaseConfigService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.AbstractListener;

import java.util.Map;
import java.util.Objects;

/**
 * Nacos 配置初始化和监听变化的Bean
 * 后续新增新的配置，请先到 ForumConfigNameConstantEnum 中添加配置名称，然后建好对应 config 的类，最后建立一个对应的 configService
 * @author Patrick_Star
 * @date 2025/04/02
 */
@Configuration
@Slf4j
public class NacosConfigConfiguration {

    public static final Map<String, BaseConfigService> CONFIG_SERVICE_MAP = Maps.newHashMap();

    public static void addConfigService(String dataId, BaseConfigService configService) {
        CONFIG_SERVICE_MAP.put(dataId, configService);
    }

    @Resource
    private NacosConfigManager nacosConfigManager;

    @Bean
    public ApplicationRunner runner() {
        return args -> {
            // 遍历每个配置并注册监听器
            for (ForumConfigNameConstantEnum entry : ForumConfigNameConstantEnum.values()) {
                String dataId = entry.getName();
                String group = entry.getGroup();

                // 获取初始配置内容并注册监听器
                String configInfo = nacosConfigManager.getConfigService().getConfigAndSignListener(dataId, group, 5000, new AbstractListener() {
                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        BaseConfigService configService = CONFIG_SERVICE_MAP.get(dataId);
                        if (Objects.isNull(configService)) {
                            log.error("NacosConfigConfiguration can not find this config service, dataId:{}", dataId);
                            return;
                        }
                        configService.parseConfig(configInfo);
                    }
                });

                // 解析初始配置内容
                BaseConfigService configService = CONFIG_SERVICE_MAP.get(dataId);
                if (Objects.isNull(configService)) {
                    log.error("NacosConfigConfiguration can not find this config service, dataId:{}", dataId);
                    return;
                }
                configService.parseConfig(configInfo);
            }
        };
    }
}
