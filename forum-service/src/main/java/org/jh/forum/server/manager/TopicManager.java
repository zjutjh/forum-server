package org.jh.forum.server.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.entity.Topic;
import org.jh.forum.server.mapper.TopicMapper;
import org.springframework.stereotype.Service;

/**
 * @author SugarMGP
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TopicManager {
    private final TopicMapper topicMapper;

    public Long getTopicId(String name) {
        LambdaQueryWrapper<Topic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Topic::getName, name);
        Topic topic = topicMapper.selectOne(queryWrapper);
        if (topic == null) {
            topic = Topic.builder().name(name).build();
            topicMapper.insert(topic);
        }
        return topic.getId();
    }

    public String getTopicName(Long id) {
        Topic topic = topicMapper.selectById(id);
        if (topic == null) {
            return "";
        }
        return topic.getName();
    }
}
