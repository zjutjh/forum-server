package org.jh.forum.start.controller;

import com.google.protobuf.InvalidProtocolBufferException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.api.dubbo.GetPostListReq;
import org.jh.forum.api.dubbo.GetPostListResp;
import org.jh.forum.api.dubbo.PostService;
import org.jh.forum.api.dubbo.PublishPostReq;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.dto.request.BaseListRequest;
import org.jh.forum.common.dto.request.GetPostListRequest;
import org.jh.forum.common.dto.request.PublishPostRequest;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.GetMyPostListElement;
import org.jh.forum.common.dto.response.GetPostInfoResponse;
import org.jh.forum.common.dto.response.GetPostListElement;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.common.exceptions.ForumServiceException;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SugarMGP
 */
@Slf4j
@Validated
@RestController
@Tag(name = "帖子", description = "帖子相关接口")
public class PostController {
    @Resource
    private PostService postService;

    @Operation(summary = "获取帖子信息")
    @GetMapping("/post/info")
    public AjaxResult<GetPostInfoResponse> getPostInfo(@RequestParam(value = "id", required = true) Long id) {
        return null;
    }

    @Operation(summary = "创建帖子")
    @PostMapping("/post/create")
    public AjaxResult<Void> createPost(@Valid @RequestBody PublishPostRequest request) {
        try {
            PublishPostReq req = PublishPostReq.newBuilder()
                    .setTitle(request.getTitle())
                    .setContent(request.getContent())
                    .setCategoryId(request.getCategoryId())
                    .addAllTopics(List.of(request.getTopics()))
                    .addAllAttachmentIds(List.of(request.getAttachmentIds()))
                    .build();
            postService.publishPost(req);
        } catch (ForumServiceException e) {
            throw new ApiException(e);
        }
        return AjaxResult.success();
    }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/post/delete")
    public AjaxResult<Void> deletePost(@RequestParam(value = "id", required = true) Long id) {
        return AjaxResult.success();
    }

    @Operation(summary = "获取帖子列表")
    @GetMapping("/post/list")
    public AjaxResult<BaseListResponse<GetPostListElement>> getPostList(@Valid GetPostListRequest request) {
        GetPostListReq req = GetPostListReq.newBuilder()
                .setCategoryId(request.getCategoryId())
                .setSortType(request.getSortType())
                .build();
        try {
            List<GetPostListElement> list = new ArrayList<>();
            GetPostListResp result = postService.getPostList(req).getData().unpack(GetPostListResp.class);
            result.getPostsList().forEach(post -> {
                list.add(GetPostListElement.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .likeCount(post.getLikeCount())
                        .commentCount(post.getCommentCount())
                        .viewCount(post.getViewCount())
                        .createdAt(post.getCreateAt())
                        .userId(post.getUserId())
                        .categoryId(post.getCategoryId())
                        .topics(post.getTopicsList().toArray(new String[0]))
                        .build()
                );
            });
            BaseListResponse<GetPostListElement> response = new BaseListResponse<>();
            response.setList(list);
            return AjaxResult.success(response);
        } catch (InvalidProtocolBufferException e) {
            throw new ApiException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    @Operation(summary = "获取我的帖子列表")
    @GetMapping("/post/my_list")
    public AjaxResult<BaseListResponse<GetMyPostListElement>> getMyPostList(BaseListRequest request) {
        try {
            List<GetMyPostListElement> list = new ArrayList<>();
            GetPostListResp result = postService.getMyPostList(null).getData().unpack(GetPostListResp.class);
            result.getPostsList().forEach(post -> {
                list.add(GetMyPostListElement.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .likeCount(post.getLikeCount())
                        .commentCount(post.getCommentCount())
                        .viewCount(post.getViewCount())
                        .createdAt(post.getCreateAt())
                        .isPinned(post.getIsPinned())
                        .categoryId(post.getCategoryId())
                        .topics(post.getTopicsList().toArray(new String[0]))
                        .build()
                );
            });
            BaseListResponse<GetMyPostListElement> response = new BaseListResponse<>();
            response.setList(list);
            return AjaxResult.success(response);
        } catch (InvalidProtocolBufferException e) {
            throw new ApiException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }
}
