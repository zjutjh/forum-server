package org.jh.forum.start.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.api.dubbo.message.GetPostListReq;
import org.jh.forum.api.dubbo.message.PostListElement;
import org.jh.forum.api.dubbo.message.PublishPostReq;
import org.jh.forum.api.dubbo.service.PostService;
import org.jh.forum.common.dto.request.BaseListRequest;
import org.jh.forum.common.dto.request.GetAdminPostListRequest;
import org.jh.forum.common.dto.request.GetPostListRequest;
import org.jh.forum.common.dto.request.PublishPostRequest;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.common.exceptions.ForumServiceException;
import org.jh.forum.start.converter.PostConverter;
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
@RequestMapping("/post")
@RestController
@Tag(name = "帖子", description = "帖子相关接口")
public class PostController {
    @Resource
    private PostService postService;

    @Resource
    private PostConverter postConverter;

    @Operation(summary = "获取帖子信息")
    @GetMapping("/info")
    public AjaxResult<GetPostInfoResponse> getPostInfo(@RequestParam(value = "id", required = true) Long id) {
        return null;
    }

    @Operation(summary = "创建帖子")
    @PostMapping("/create")
    public AjaxResult<Void> createPost(@Valid @RequestBody PublishPostRequest request) {
        try {
            PublishPostReq req = postConverter.toMessage(request);
            postService.publishPost(req);
        } catch (ForumServiceException e) {
            throw new ApiException(e);
        }
        return AjaxResult.success();
    }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/delete")
    public AjaxResult<Void> deletePost(@RequestParam(value = "id", required = true) Long id) {
        return AjaxResult.success();
    }

    @Operation(summary = "获取帖子列表")
    @GetMapping("/list")
    public AjaxResult<BaseListResponse<GetPostListElement>> getPostList(@Valid GetPostListRequest request) {
        GetPostListReq req = postConverter.toMessage(request);
        List<GetPostListElement> list = new ArrayList<>();
        List<PostListElement> result = postService.getPostList(req);
        result.forEach(post -> {
            list.add(postConverter.toListDTO(post));
        });
        BaseListResponse<GetPostListElement> response = new BaseListResponse<>();
        response.setList(list);
        return AjaxResult.success(response);
    }

    @Operation(summary = "获取我的帖子列表")
    @GetMapping("/my_list")
    public AjaxResult<BaseListResponse<GetMyPostListElement>> getMyPostList(BaseListRequest request) {
        List<GetMyPostListElement> list = new ArrayList<>();
        List<PostListElement> result = postService.getMyPostList();
        result.forEach(post -> {
            list.add(postConverter.toMyListDTO(post));
        });
        BaseListResponse<GetMyPostListElement> response = new BaseListResponse<>();
        response.setList(list);
        return AjaxResult.success(response);
    }

    @Operation(summary = "管理员获取帖子列表")
    @Tag(name = "管理员")
    @GetMapping("/admin/list")
    public AjaxResult<BaseListResponse<GetAdminPostListElement>> getAdminPostList(GetAdminPostListRequest request) {
        return null;
    }

    @Operation(summary = "管理员获取帖子信息")
    @Tag(name = "管理员")
    @GetMapping("/admin/info")
    public AjaxResult<GetAdminPostInfoResponse> getAdminPostInfo(@RequestParam(value = "id", required = true) Long id) {
        return null;
    }
}
