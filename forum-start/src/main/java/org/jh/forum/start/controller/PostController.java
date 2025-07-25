package org.jh.forum.start.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.api.dubbo.service.PostService;
import org.jh.forum.common.dto.request.GetAdminPostListRequest;
import org.jh.forum.common.dto.request.GetPersonalPostRequest;
import org.jh.forum.common.dto.request.GetPostListRequest;
import org.jh.forum.common.dto.request.PublishPostRequest;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * @author SugarMGP
 */
@Slf4j
@RequestMapping("/post")
@RestController
@Tag(name = "帖子", description = "帖子相关接口")
public class PostController {
    @Resource
    private PostService postService;

    @Operation(summary = "获取帖子信息")
    @GetMapping("/info")
    public AjaxResult<GetPostInfoResponse> getPostInfo(@RequestParam(value = "id") Long id) {
        return AjaxResult.success(postService.getPostInfo(id));
    }

    @Operation(summary = "创建帖子")
    @PostMapping("/create")
    public AjaxResult<Void> createPost(@Valid @RequestBody PublishPostRequest request) {
        postService.publishPost(request);
        return AjaxResult.success();
    }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/delete")
    public AjaxResult<Void> deletePost(@RequestParam(value = "id") Long id) {
        postService.deletePost(id);
        return AjaxResult.success();
    }

    @Operation(summary = "获取帖子列表")
    @GetMapping("/list")
    public AjaxResult<BaseListResponse<GetPostListElement>> getPostList(@Valid GetPostListRequest request) {
        return AjaxResult.success(postService.getPostList(request));
    }

    @Operation(summary = "获取个人帖子列表")
    @GetMapping("/personal")
    public AjaxResult<BaseListResponse<GetPersonalPostListElement>> getPersonalPostList(@Valid GetPersonalPostRequest request) {
        return AjaxResult.success(postService.getPersonalPostList(request));
    }

    @Operation(summary = "管理员获取帖子列表")
    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Tag(name = "管理员")
    @GetMapping("/admin/list")
    public AjaxResult<BaseListResponse<GetAdminPostListElement>> getAdminPostList(@Valid GetAdminPostListRequest request) {
        return AjaxResult.success(postService.getAdminPostList(request));
    }

    @Operation(summary = "管理员获取帖子信息")
    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Tag(name = "管理员")
    @GetMapping("/admin/info")
    public AjaxResult<GetAdminPostInfoResponse> getAdminPostInfo(@RequestParam(value = "id") Long id) {
        return AjaxResult.success(postService.getAdminPostInfo(id));
    }

    @Operation(summary = "恢复帖子")
    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Tag(name = "管理员")
    @PutMapping("/restore")
    public AjaxResult<Void> restorePost(@RequestParam(value = "id") Long id) {
        postService.restorePost(id);
        return AjaxResult.success();
    }

    @Operation(summary = "获取五大热帖")
    @GetMapping("/five")
    public AjaxResult<TopFivePostList> getTopFivePosts() {
        return AjaxResult.success(postService.getTopFivePosts());
    }
}
