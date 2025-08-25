package org.jh.forum.server.manager;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jh.forum.common.annotation.IgnoreLogicDelete;
import org.jh.forum.common.constants.*;
import org.jh.forum.common.dto.PictureInfoDTO;
import org.jh.forum.common.dto.request.GetAdminPostListRequest;
import org.jh.forum.common.dto.request.GetPersonalPostRequest;
import org.jh.forum.common.dto.request.PublishPostRequest;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.common.entity.*;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.client.AliyunGreenClient;
import org.jh.forum.server.mapper.*;
import org.jh.forum.server.utils.AsyncUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    private final CommentMapper commentMapper;
    private final UpvoteMapper upvoteMapper;
    private final NoticeManager noticeManager;
    private final AliyunGreenClient aliyunGreenClient;

    @Transactional
    public void publishPost(PublishPostRequest request) {
        String text = StringUtils.joinWith(" ", request.getTitle(), request.getContent(), request.getTopics());
        aliyunGreenClient.checkText(text, TextModerationServiceEnum.UGC_LLM);

        Post post = Post.builder()
                .userId(StpUtil.getLoginIdAsLong())
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .isPinned(false)
                .isTopped(false)
                .viewCount(0)
                .reportCount(0)
                .resolvedReportCount(0)
                .build();
        postMapper.insert(post);
        List<Long> topicIds = request.getTopics().stream().distinct()
                .map(topicManager::getTopicId).toList();
        for (Long topic : topicIds) {
            postTopicRelationMapper.insertIgnore(PostTopicRelation.builder()
                    .postId(post.getId())
                    .topicId(topic)
                    .build()
            );
        }
        for (String url : request.getPictures()) {
            fileManager.bindAttachment(url, TargetTypeEnum.POST, post.getId());
        }
    }

    public BaseListResponse<GetPostListElement> getPostList(CategoryEnum category, Integer page, Integer pageSize) {
        IPage<Post> postPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        if (category != null) {
            queryWrapper.eq(Post::getCategory, category);
        }
        queryWrapper.orderByDesc(Post::getIsPinned).orderByDesc(Post::getCreatedAt);
        postMapper.selectPage(postPage, queryWrapper);
        List<GetPostListElement> list = postPage.getRecords().stream()
                .map(this::buildPostListElement)
                .toList();
        return BaseListResponse.<GetPostListElement>builder()
                .list(list)
                .total(postPage.getTotal())
                .page(page)
                .pageSize(pageSize)
                .build();
    }

    public List<PictureInfoDTO> getPostPictures(Long id) {
        return attachmentMapper.selectList(new LambdaQueryWrapper<Attachment>()
                .eq(Attachment::getTargetId, id)
                .eq(Attachment::getTargetType, TargetTypeEnum.POST)
                .eq(Attachment::getType, AttachmentTypeEnum.PICTURE)
        ).stream().map(fileManager::buildPictureInfoDTO).toList();
    }

    public BaseListResponse<GetPersonalPostListElement> getPersonalPostList(GetPersonalPostRequest request) {
        IPage<Post> postPage = new Page<>(request.getPage(), request.getPageSize());
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        if (request.getId() == null || request.getId().equals(StpUtil.getLoginIdAsLong())) {
            queryWrapper.eq(Post::getUserId, StpUtil.getLoginIdAsLong());
        } else {
            queryWrapper.eq(Post::getUserId, request.getId());
        }
        queryWrapper.orderByDesc(Post::getIsTopped).orderByDesc(Post::getCreatedAt);
        postMapper.selectPage(postPage, queryWrapper);
        List<GetPersonalPostListElement> list = new ArrayList<>();
        for (Post post : postPage.getRecords()) {
            List<PictureInfoDTO> pictures = getPostPictures(post.getId());
            list.add(GetPersonalPostListElement.builder()
                    .id(post.getId())
                    .category(post.getCategory())
                    .topics(getPostTopics(post.getId()))
                    .title(post.getTitle())
                    .content(StringUtils.left(post.getContent(), 200))
                    .likeCount(getLikeCount(post.getId()))
                    .commentCount(getCommentCount(post.getId()))
                    .viewCount(post.getViewCount())
                    .createdAt(post.getCreatedAt())
                    .isLiked(checkIsLiked(post.getId()))
                    .isTopped(post.getIsTopped())
                    .pictures(pictures.subList(0, Math.min(pictures.size(), 3)))
                    .totalPictures(pictures.size())
                    .build()
            );
        }
        return BaseListResponse.<GetPersonalPostListElement>builder()
                .list(list)
                .total(postPage.getTotal())
                .page(request.getPage())
                .pageSize(request.getPageSize())
                .build();
    }

    public BaseListResponse<GetPostListElement> getHotPostList(CategoryEnum category, Integer page, Integer pageSize) {
        PostRankManager.PageResult<Long> result = postRankManager.getHotPostIds(category, page, pageSize);
        List<GetPostListElement> list = result.getRecords().stream()
                .map(postMapper::selectById)
                .filter(Objects::nonNull)
                .map(this::buildPostListElement)
                .toList();
        return BaseListResponse.<GetPostListElement>builder()
                .list(list)
                .total(result.getTotal())
                .page(page)
                .pageSize(pageSize)
                .build();
    }

    public GetPostInfoResponse getPostInfo(Long postId, Long userId) {
        Post post = getPostOrThrow(postId);
        updateViewCount(postId, userId, post.getCategory());
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
                .pictures(getPostPictures(postId))
                .isLiked(checkIsLiked(postId))
                .isSelf(post.getUserId().equals(userId))
                .build();
    }

    private boolean checkIsLiked(Long postId) {
        LambdaQueryWrapper<Upvote> queryWrapper = new LambdaQueryWrapper<Upvote>()
                .eq(Upvote::getUserId, StpUtil.getLoginIdAsLong())
                .eq(Upvote::getPostId, postId);
        Upvote upvote = upvoteMapper.selectOne(queryWrapper);
        return upvote != null && upvote.getStatus();
    }

    public void deletePost(Long id, boolean isAdmin) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            return;
        }
        if (!post.getUserId().equals(StpUtil.getLoginIdAsLong()) && !isAdmin) {
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }
        postRankManager.removePost(id);
        postMapper.deleteById(id);
    }

    @IgnoreLogicDelete
    public BaseListResponse<GetAdminPostListElement> getAdminPostList(GetAdminPostListRequest request) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(request.getTitle()), Post::getTitle, request.getTitle())
                .eq(request.getCategory() != null, Post::getCategory, request.getCategory());

        if (request.getStatus() != null) {
            if (request.getStatus() == PostStatusEnum.DELETED) {
                queryWrapper.eq(Post::getDeleted, true);
            }
            if (request.getStatus() == PostStatusEnum.NORMAL) {
                queryWrapper.eq(Post::getDeleted, false).apply("report_count = resolved_report_count");
            }
            if (request.getStatus() == PostStatusEnum.PENDING) {
                queryWrapper.eq(Post::getDeleted, false).apply("report_count > resolved_report_count");
            }
        }

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

        queryWrapper.orderByDesc(Post::getIsPinned).orderByDesc(Post::getCreatedAt);
        IPage<Post> postPage = new Page<>(request.getPage(), request.getPageSize());
        postMapper.selectPage(postPage, queryWrapper);
        List<GetAdminPostListElement> list = new ArrayList<>();
        for (Post post : postPage.getRecords()) {
            User user = userMapper.selectById(post.getUserId());
            list.add(GetAdminPostListElement.builder()
                    .id(post.getId())
                    .publisher(user == null ? "" : user.getNickname())
                    .category(post.getCategory())
                    .title(post.getTitle())
                    .likeCount(getLikeCount(post.getId()))
                    .commentCount(getCommentCount(post.getId()))
                    .viewCount(post.getViewCount())
                    .status(getPostStatus(post))
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

    @IgnoreLogicDelete
    public GetAdminPostInfoResponse getAdminPostInfo(Long id) {
        Post post = getPostOrThrow(id);
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
                .status(getPostStatus(post))
                .isPinned(post.getIsPinned())
                .pictures(getPostPictures(id))
                .build();
    }

    private PostStatusEnum getPostStatus(Post post) {
        if (Boolean.TRUE.equals(post.getDeleted())) {
            return PostStatusEnum.DELETED;
        }
        if (post.getReportCount() > post.getResolvedReportCount()) {
            return PostStatusEnum.PENDING;
        }
        return PostStatusEnum.NORMAL;
    }

    private List<String> getPostTopics(Long postId) {
        List<PostTopicRelation> relations = postTopicRelationMapper.selectList(new LambdaQueryWrapper<PostTopicRelation>().eq(PostTopicRelation::getPostId, postId));
        List<String> topics = new ArrayList<>();
        for (PostTopicRelation relation : relations) {
            topics.add(topicManager.getTopicName(relation.getTopicId()));
        }
        return topics;
    }

    private Integer getLikeCount(Long postId) {
        long count = upvoteMapper.selectCount(new LambdaQueryWrapper<Upvote>()
                .eq(Upvote::getPostId, postId).eq(Upvote::getStatus, true));
        return Math.toIntExact(count);
    }

    private Integer getCommentCount(Long postId) {
        return commentMapper.selectCommentCount(postId);
    }

    private void updateViewCount(Long postId, Long userId, CategoryEnum category) {
        // 2分钟内仅允许一次浏览量增加
        String checkKey = "post:view:" + userId + ":" + postId;
        Boolean isSet = redisTemplate.opsForValue().setIfAbsent(checkKey, "1", 2, TimeUnit.MINUTES);
        if (Boolean.FALSE.equals(isSet)) {
            return;
        }
        postMapper.incrementViewCount(postId);
        postRankManager.recordAction(postId, category, postRankManager.VIEW);
    }

    public List<Post> getTopFivePosts() {
        List<Long> postIds = postRankManager.getTopFiveHotPostIds();
        return postIds.stream()
                .map(postMapper::selectById)
                .filter(Objects::nonNull)
                .toList();
    }

    @IgnoreLogicDelete
    public void restorePost(Long id) {
        getPostOrThrow(id);
        postMapper.restorePost(id);
    }

    public void pinPost(Long id, Boolean pinned) {
        long count = postMapper.selectCount(new LambdaQueryWrapper<Post>()
                .ne(Post::getId, id)
                .eq(Post::getIsPinned, true));
        if (count >= 3 && Boolean.TRUE.equals(pinned)) {
            throw new ApiException(ExceptionEnum.POST_PINNED_LIMIT_REACHED);
        }
        Post post = getPostOrThrow(id);
        post.setIsPinned(pinned);
        postMapper.updateById(post);
    }

    public void topPost(Long id, Boolean topped) {
        Post post = getPostOrThrow(id);
        if (!post.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }
        boolean exist = postMapper.exists(new LambdaQueryWrapper<Post>()
                .ne(Post::getId, id)
                .eq(Post::getUserId, post.getUserId())
                .eq(Post::getIsTopped, true));
        if (exist && Boolean.TRUE.equals(topped)) {
            throw new ApiException(ExceptionEnum.POST_TOPPED_LIMIT_REACHED);
        }
        post.setIsTopped(topped);
        postMapper.updateById(post);
    }

    public Boolean upvotePost(Long id) {
        Post post = getPostOrThrow(id);
        Long userId = StpUtil.getLoginIdAsLong();

        upvoteMapper.updatePostUpvote(userId, id);

        Upvote upvote = upvoteMapper.selectOne(new LambdaQueryWrapper<Upvote>()
                .eq(Upvote::getPostId, id)
                .eq(Upvote::getUserId, userId));
        Boolean status = upvote.getStatus();

        if (Boolean.TRUE.equals(status)) {
            AsyncUtil.runAsyncWithLogging(() -> {
                postRankManager.recordAction(id, post.getCategory(), postRankManager.LIKE);
                noticeManager.createNotice(post.getUserId(), NoticeTypeEnum.LIKE, NoticePositionTypeEnum.POST, id, null);
            });
        } else {
            AsyncUtil.runAsyncWithLogging(() ->
                    noticeManager.cancelLike(post.getUserId(), NoticePositionTypeEnum.POST, id)
            );
        }
        return status;
    }

    private GetPostListElement buildPostListElement(Post post) {
        List<PictureInfoDTO> pictures = getPostPictures(post.getId());
        return GetPostListElement.builder()
                .id(post.getId())
                .publisherInfo(userManager.getUserInfo(post.getUserId()))
                .category(post.getCategory())
                .topics(getPostTopics(post.getId()))
                .title(post.getTitle())
                .content(StringUtils.left(post.getContent(), 200))
                .likeCount(getLikeCount(post.getId()))
                .commentCount(getCommentCount(post.getId()))
                .createdAt(post.getCreatedAt())
                .isPinned(post.getIsPinned())
                .pictures(pictures.subList(0, Math.min(pictures.size(), 3)))
                .totalPictures(pictures.size())
                .isLiked(checkIsLiked(post.getId()))
                .build();
    }

    public Post getPostOrThrow(Long id) {
        return Optional.ofNullable(postMapper.selectById(id))
                .orElseThrow(() -> new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND));
    }
}
