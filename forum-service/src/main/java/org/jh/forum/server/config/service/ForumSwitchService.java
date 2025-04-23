package org.jh.forum.server.config.service;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.ForumConfigNameConstantEnum;
import org.jh.forum.server.config.NacosConfigA;
import org.jh.forum.server.config.NacosConfigConfiguration;
import org.jh.forum.server.switchs.ForumSwitch;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author Patrick_Star
 * @date 2025/4/19
 */
@Service
@Slf4j
public class ForumSwitchService extends BaseConfigService {
    public static ForumSwitch forumSwitch;

    @PostConstruct
    public void init() {
        NacosConfigConfiguration.addConfigService(ForumConfigNameConstantEnum.FORUM_SWITCH.getName(), this);
    }

    @Override
    public void parseConfig(String configInfo) {
        forumSwitch = JSON.parseObject(configInfo, ForumSwitch.class);
    }
}
