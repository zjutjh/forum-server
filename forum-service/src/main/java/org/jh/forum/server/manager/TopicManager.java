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
        Topic topic = new Topic(name);
        int rows = topicMapper.insertIgnore(topic);
        if (rows > 0) {
            return topic.getId();
        } else {
            return topicMapper.selectOne(
                    new LambdaQueryWrapper<Topic>()
                            .eq(Topic::getName, name)
            ).getId();
        }
    }

    public String getTopicName(Long id) {
        Topic topic = topicMapper.selectById(id);
        if (topic == null) {
            return "";
        }
        return topic.getName();
    }
}
