package org.jh.forum.server.config.service;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.ForumConfigNameConstantEnum;
import org.jh.forum.server.config.NacosConfigA;
import org.jh.forum.server.config.NacosConfigConfiguration;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * @author Patrick_Star
 * @date 2025/4/4
 */
@Service
@Slf4j
public class NacosConfigAService extends BaseConfigService {
    public static NacosConfigA nacosConfigA;


    @PostConstruct
    public void init() {
        NacosConfigConfiguration.addConfigService(ForumConfigNameConstantEnum.NACOS_CONFIG_A.getName(), this);
    }

    @Override
    public void parseConfig(String configInfo) {
        log.info("[Listener] Config received for configInfo:{}", configInfo);
        log.info("[Before User] {}", JSON.toJSONString(NacosConfigAService.nacosConfigA));

        nacosConfigA = JSON.parseObject(configInfo, NacosConfigA.class);

        log.info("[After User] {}", JSON.toJSONString(NacosConfigAService.nacosConfigA));
    }
}
