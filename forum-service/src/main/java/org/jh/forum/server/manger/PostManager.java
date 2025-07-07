package org.jh.forum.server.manger;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.api.dubbo.message.PostListElement;
import org.jh.forum.common.constants.CategoryEnum;
import org.jh.forum.common.constants.TargetTypeEnum;
import org.jh.forum.common.dto.UserInfoDTO;
import org.jh.forum.common.entity.Post;
import org.jh.forum.common.entity.PostTopicRelation;
import org.jh.forum.server.mapper.PostMapper;
import org.jh.forum.server.mapper.PostTopicRelationMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    private final FileManager fileManager;

    public void publishPost(String title, String content, CategoryEnum category, List<String> topics, List<Long> attachmentIds) {
        Post post = Post.builder()
                .userId(StpUtil.getLoginIdAsLong())
                .title(title)
                .content(content)
                .category(category)
                .isPinned(false)
                .isTopped(false)
                .build();
        postMapper.insert(post);
        for (String topic : topics) {
            postTopicRelationMapper.insert(PostTopicRelation.builder()
                    .postId(post.getId())
                    .topicId(topicManager.getTopicId(topic))
                    .build()
            );
        }
        for (Long attachmentId : attachmentIds) {
            fileManager.bindAttachment(attachmentId, TargetTypeEnum.POST, post.getId());
        }
    }

    public List<PostListElement> getPostList(CategoryEnum category) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        if (category != null) {
            queryWrapper.eq(Post::getCategory, category);
        }
        queryWrapper.orderByDesc(Post::getCreatedAt);
        List<Post> posts = postMapper.selectList(queryWrapper);
        return convertPostsToElements(posts);
    }

    public List<PostListElement> getMyPostList(Long userId) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getUserId, userId).orderByDesc(Post::getCreatedAt);
        List<Post> posts = postMapper.selectList(queryWrapper);
        return convertPostsToElements(posts);
    }

    public List<PostListElement> getHotPostList(CategoryEnum category) {
        // TODO 获取最热帖子
        return null;
    }

    private List<PostListElement> convertPostsToElements(List<Post> posts) {
        List<PostListElement> postList = new ArrayList<>();
        for (Post post : posts) {
            List<PostTopicRelation> relations = postTopicRelationMapper.selectList(new LambdaQueryWrapper<PostTopicRelation>().eq(PostTopicRelation::getPostId, post.getId()));
            List<String> topics = new ArrayList<>();
            for (PostTopicRelation relation : relations) {
                topics.add(topicManager.getTopicName(relation.getTopicId()));
            }

            // TODO: 获取用户信息
            UserInfoDTO user = UserInfoDTO.builder().build();

            postList.add(PostListElement.builder()
                    .id(post.getId())
                    .userInfo(user)
                    .isTopped(post.getIsTopped())
                    .isPinned(post.getIsPinned())
                    .category(post.getCategory())
                    .topics(topics)
                    .title(post.getTitle())
                    .content(post.getContent().substring(0, Math.min(post.getContent().length(), 50)))
                    .likeCount(0)
                    .commentCount(0)
                    .viewCount(0)
                    .createdAt(post.getCreatedAt())
                    .status("")
                    .build()
            );
        }
        return postList;
    }
}
