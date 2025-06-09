package org.jh.forum.server.manger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.api.dubbo.PublishPostReq;
import org.jh.forum.common.entity.Post;
import org.jh.forum.common.entity.PostTopicRelation;
import org.jh.forum.common.entity.mapper.PostMapper;
import org.jh.forum.common.entity.mapper.PostTopicRelationMapper;
import org.springframework.stereotype.Service;

/**
 * @author SugarMGP
 * @date 2025/6/9
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PostManager {
    private final PostMapper postMapper;
    private final PostTopicRelationMapper postTopicRelationMapper;
    private final TopicManager topicManager;

    public void publishPost(PublishPostReq req) {
        Post post = Post.builder()
                .userId(req.getUserId())
                .title(req.getTitle())
                .content(req.getContent())
                .categoryId(req.getCategoryId())
                .isPinned(false)
                .build();
        postMapper.insert(post);
        for (String topic : req.getTopicsList()) {
            postTopicRelationMapper.insert(PostTopicRelation.builder()
                    .postId(post.getId())
                    .topicId(topicManager.getTopicId(topic))
                    .build()
            );
        }
    }
}
