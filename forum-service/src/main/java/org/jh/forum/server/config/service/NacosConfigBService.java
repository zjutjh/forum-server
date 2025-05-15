package org.jh.forum.server.config.service;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.ForumConfigNameConstantEnum;
import org.jh.forum.server.config.NacosConfigB;
import org.jh.forum.server.config.NacosConfigConfiguration;
import org.springframework.stereotype.Service;

/**
 * @author Patrick_Star
 * @date 2025/4/4
 */
@Service
@Slf4j
public class NacosConfigBService extends BaseConfigService {
    public static NacosConfigB nacosConfigB;

    @PostConstruct
    public void init() {
        NacosConfigConfiguration.addConfigService(ForumConfigNameConstantEnum.NACOS_CONFIG_B.getName(), this);
    }

    @Override
    public void parseConfig(String configInfo) {
        log.info("[Listener] Config received for configInfo:{}", configInfo);
        log.info("[Before User] {}", JSON.toJSONString(NacosConfigBService.nacosConfigB));

        nacosConfigB = JSON.parseObject(configInfo, NacosConfigB.class);

        log.info("[After User] {}", JSON.toJSONString(NacosConfigBService.nacosConfigB));
    }
}
