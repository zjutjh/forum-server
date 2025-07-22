package org.jh.forum.server.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.TopicService;
import org.jh.forum.server.manager.TopicManager;

import jakarta.annotation.Resource;

/**
 * @author SugarMGP
 */
@DubboService(version = "1.0.0")
@Slf4j
public class TopicServiceImpl implements TopicService {
    @Resource
    private TopicManager topicManager;

    @Override
    public Long getTopicId(String name) {
        return topicManager.getTopicId(name);
    }
}
