package org.jh.forum.server.config.service;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.ForumConfigNameConstantEnum;
import org.jh.forum.server.config.NacosConfigConfiguration;
import org.jh.forum.server.switchs.SuperLoginSwitch;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * @author MangoGovo
 */
@Service
@Slf4j
public class SuperLoginSwitchService extends BaseConfigService {
    public static SuperLoginSwitch superLoginSwitch;

    @PostConstruct
    public void init() {
        NacosConfigConfiguration.addConfigService(ForumConfigNameConstantEnum.SUPER_LOGIN_SWITCH.getName(), this);
    }

    @Override
    public void parseConfig(String configInfo) {
        superLoginSwitch = JSON.parseObject(configInfo, SuperLoginSwitch.class);
    }
}
