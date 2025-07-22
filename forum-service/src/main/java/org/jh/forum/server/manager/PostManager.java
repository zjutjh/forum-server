package org.jh.forum.server.manager;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jh.forum.common.constants.CategoryEnum;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.PostStatusEnum;
import org.jh.forum.common.constants.TargetTypeEnum;
import org.jh.forum.common.dto.AttachmentInfoDTO;
import org.jh.forum.common.dto.request.GetAdminPostListRequest;
import org.jh.forum.common.dto.request.PublishPostRequest;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.common.entity.Attachment;
import org.jh.forum.common.entity.Post;
import org.jh.forum.common.entity.PostTopicRelation;
import org.jh.forum.common.entity.User;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.mapper.AttachmentMapper;
import org.jh.forum.server.mapper.PostMapper;
import org.jh.forum.server.mapper.PostTopicRelationMapper;
import org.jh.forum.server.mapper.UserMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private final UserManager userManager;
    private final RedisTemplate<String, String> redisTemplate;
    private final FileManager fileManager;
    private final AttachmentMapper attachmentMapper;
    private final UserMapper userMapper;
    private final PostRankManager postRankManager;

    public void publishPost(PublishPostRequest request) {
        Post post = Post.builder()
                .userId(StpUtil.getLoginIdAsLong())
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .isPinned(false)
                .isTopped(false)
                .viewCount(0)
                .status(PostStatusEnum.NORMAL)
                .build();
        postMapper.insert(post);
        for (String topic : request.getTopics()) {
            postTopicRelationMapper.insert(PostTopicRelation.builder()
                    .postId(post.getId())
                    .topicId(topicManager.getTopicId(topic))
                    .build()
            );
        }
        for (Long attachmentId : request.getAttachmentIds()) {
            fileManager.bindAttachment(attachmentId, TargetTypeEnum.POST, post.getId());
        }
    }

    public BaseListResponse<GetPostListElement> getPostList(CategoryEnum category, Integer page, Integer pageSize) {
        IPage<Post> postPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        if (category != null) {
            queryWrapper.eq(Post::getCategory, category);
        }
        queryWrapper.eq(Post::getStatus, PostStatusEnum.NORMAL).orderByDesc(Post::getCreatedAt);
        postMapper.selectPage(postPage, queryWrapper);
        List<GetPostListElement> list = new ArrayList<>();
        for (Post post : postPage.getRecords()) {
            list.add(GetPostListElement.builder()
                    .id(post.getId())
                    .publisherInfo(userManager.getUserInfo(post.getUserId()))
                    .category(post.getCategory())
                    .topics(getPostTopics(post.getId()))
                    .title(post.getTitle())
                    .content(truncateContent(post.getContent()))
                    .likeCount(getLikeCount(post.getId()))
                    .commentCount(getCommentCount(post.getId()))
                    .createdAt(post.getCreatedAt())
                    .build()
            );
        }
        return BaseListResponse.<GetPostListElement>builder()
                .list(list)
                .total(postPage.getTotal())
                .page(page)
                .pageSize(pageSize)
                .build();
    }

    public BaseListResponse<GetMyPostListElement> getMyPostList(Long userId, Integer page, Integer pageSize) {
        IPage<Post> postPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Post::getStatus, PostStatusEnum.DELETED).eq(Post::getUserId, userId).orderByDesc(Post::getCreatedAt);
        postMapper.selectPage(postPage, queryWrapper);
        List<GetMyPostListElement> list = new ArrayList<>();
        for (Post post : postPage.getRecords()) {
            list.add(GetMyPostListElement.builder()
                    .id(post.getId())
                    .category(post.getCategory())
                    .topics(getPostTopics(post.getId()))
                    .title(post.getTitle())
                    .content(truncateContent(post.getContent()))
                    .likeCount(getLikeCount(post.getId()))
                    .commentCount(getCommentCount(post.getId()))
                    .viewCount(post.getViewCount())
                    .createdAt(post.getCreatedAt())
                    .isTopped(post.getIsTopped())
                    .status(post.getStatus())
                    .build()
            );
        }
        return BaseListResponse.<GetMyPostListElement>builder()
                .list(list)
                .total(postPage.getTotal())
                .page(page)
                .pageSize(pageSize)
                .build();
    }

    public BaseListResponse<GetPostListElement> getHotPostList(CategoryEnum category, Integer page, Integer pageSize) {
        List<GetPostListElement> list = new ArrayList<>();
        PostRankManager.PageResult<Long> result = postRankManager.getHotPostIds(page, pageSize);
        result.getRecords().forEach(id -> {
            Post post = postMapper.selectById(id);
            list.add(GetPostListElement.builder()
                    .id(id)
                    .publisherInfo(userManager.getUserInfo(post.getUserId()))
                    .category(post.getCategory())
                    .topics(getPostTopics(id))
                    .title(post.getTitle())
                    .content(truncateContent(post.getContent()))
                    .likeCount(getLikeCount(id))
                    .commentCount(getCommentCount(id))
                    .createdAt(post.getCreatedAt())
                    .build()
            );
        });
        return BaseListResponse.<GetPostListElement>builder()
                .list(list)
                .total(result.getTotal())
                .page(page)
                .pageSize(pageSize)
                .build();
    }

    public GetPostInfoResponse getPostInfo(Long postId, Long userId) {
        Post post = postMapper.selectById(postId);
        if (post == null || post.getStatus() == PostStatusEnum.DELETED) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        if (post.getStatus() == PostStatusEnum.PENDING && !Objects.equals(post.getUserId(), userId)) {
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }
        updateViewCount(postId, userId);
        return GetPostInfoResponse.builder()
                .publisherInfo(userManager.getUserInfo(post.getUserId()))
                .category(post.getCategory())
                .topics(getPostTopics(postId))
                .title(post.getTitle())
                .content(post.getContent())
                .likeCount(getLikeCount(postId))
                .commentCount(getCommentCount(postId))
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .attachments(getPostAttachments(postId))
                .build();
    }

    public void deletePost(Long id, boolean isAdmin) {
        Post post = postMapper.selectById(id);
        if (post == null || post.getStatus() == PostStatusEnum.DELETED) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        if (!post.getUserId().equals(StpUtil.getLoginIdAsLong()) && !isAdmin) {
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }
        postRankManager.removePost(id);
        post.setStatus(PostStatusEnum.DELETED);
        postMapper.updateById(post);
    }

    public BaseListResponse<GetAdminPostListElement> getAdminPostList(GetAdminPostListRequest request) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(request.getTitle()), Post::getTitle, request.getTitle())
                .eq(request.getCategory() != null, Post::getCategory, request.getCategory())
                .eq(request.getStatus() != null, Post::getStatus, request.getStatus());

        // 模糊查询发布人
        if (StringUtils.isNotBlank(request.getPublisher())) {
            LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
            userQueryWrapper.like(User::getNickname, request.getPublisher());
            List<Long> userIds = userMapper.selectList(userQueryWrapper).stream()
                    .map(User::getId)
                    .collect(Collectors.toCollection(ArrayList::new));
            // 添加一个虚拟用户，防止筛选失效
            userIds.add(0L);
            queryWrapper.in(Post::getUserId, userIds);
        }

        // 筛选发帖时间
        if (request.getDate() != null) {
            LocalDateTime startOfDay = request.getDate().atStartOfDay();
            LocalDateTime endOfDay = request.getDate().atTime(23, 59, 59);
            queryWrapper.between(Post::getCreatedAt, startOfDay, endOfDay);
        }

        queryWrapper.orderByDesc(Post::getCreatedAt);
        IPage<Post> postPage = new Page<>(request.getPage(), request.getPageSize());
        postMapper.selectPage(postPage, queryWrapper);
        List<GetAdminPostListElement> list = new ArrayList<>();
        for (Post post : postPage.getRecords()) {
            list.add(GetAdminPostListElement.builder()
                    .id(post.getId())
                    .publisher(userMapper.selectById(post.getUserId()).getNickname())
                    .category(post.getCategory())
                    .title(post.getTitle())
                    .likeCount(getLikeCount(post.getId()))
                    .commentCount(getCommentCount(post.getId()))
                    .viewCount(post.getViewCount())
                    .status(post.getStatus())
                    .isPinned(post.getIsPinned())
                    .createdAt(post.getCreatedAt())
                    .build()
            );
        }
        return BaseListResponse.<GetAdminPostListElement>builder()
                .list(list)
                .total(postPage.getTotal())
                .page(request.getPage())
                .pageSize(request.getPageSize())
                .build();
    }

    public GetAdminPostInfoResponse getAdminPostInfo(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        return GetAdminPostInfoResponse.builder()
                .publisherInfo(userManager.getUserInfo(post.getUserId()))
                .category(post.getCategory())
                .topics(getPostTopics(id))
                .title(post.getTitle())
                .content(post.getContent())
                .likeCount(getLikeCount(id))
                .commentCount(getCommentCount(id))
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .status(post.getStatus())
                .isPinned(post.getIsPinned())
                .attachments(getPostAttachments(id))
                .build();
    }

    private List<String> getPostTopics(Long postId) {
        List<PostTopicRelation> relations = postTopicRelationMapper.selectList(new LambdaQueryWrapper<PostTopicRelation>().eq(PostTopicRelation::getPostId, postId));
        List<String> topics = new ArrayList<>();
        for (PostTopicRelation relation : relations) {
            topics.add(topicManager.getTopicName(relation.getTopicId()));
        }
        return topics;
    }

    private List<AttachmentInfoDTO> getPostAttachments(Long postId) {
        List<Attachment> attachments = attachmentMapper.selectList(new LambdaQueryWrapper<Attachment>()
                .eq(Attachment::getTargetId, postId)
                .eq(Attachment::getTargetType, TargetTypeEnum.POST)
        );
        List<AttachmentInfoDTO> attachmentInfoList = new ArrayList<>();
        for (Attachment attachment : attachments) {
            attachmentInfoList.add(AttachmentInfoDTO.builder()
                    .url(fileManager.getFileUrl(attachment.getFileId()))
                    .type(attachment.getType())
                    .filename(attachment.getFilename())
                    .build()
            );
        }
        return attachmentInfoList;
    }

    private Integer getLikeCount(Long postId) {
        // TODO 获取帖子点赞数
        return null;
    }

    private Integer getCommentCount(Long postId) {
        // TODO 获取帖子评论数
        return null;
    }

    private void updateViewCount(Long postId, Long userId) {
        // 2分钟内仅允许一次浏览量增加
        String checkKey = "post:view:" + userId + ":" + postId;
        Boolean isSet = redisTemplate.opsForValue().setIfAbsent(checkKey, "1", 2, TimeUnit.MINUTES);
        if (Boolean.FALSE.equals(isSet)) {
            return;
        }
        postMapper.incrementViewCount(postId);
        postRankManager.recordAction(postId, postRankManager.VIEW);
    }

    private String truncateContent(String content) {
        if (content == null || content.length() <= 50) {
            return content;
        }
        return content.substring(0, 50);
    }

    public List<Post> getTopFivePosts() {
        List<Long> postIds = postRankManager.getTopFiveHotPostIds();
        if (postIds != null && !postIds.isEmpty()) {
            return postMapper.selectByIds(postIds);
        }
        return Collections.emptyList();
    }
}
