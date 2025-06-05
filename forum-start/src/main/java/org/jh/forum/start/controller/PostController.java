package org.jh.forum.start.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.dto.request.BaseListRequest;
import org.jh.forum.common.dto.request.CreatePostRequest;
import org.jh.forum.common.dto.request.GetPostListRequest;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.GetMyPostListElement;
import org.jh.forum.common.dto.response.GetPostInfoResponse;
import org.jh.forum.common.dto.response.GetPostListElement;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.*;

/**
 * @author SugarMGP
 */
@Slf4j
@RestController
@Tag(name = "帖子", description = "帖子相关接口")
public class PostController {

    @Operation(summary = "获取帖子信息")
    @GetMapping("/post/info")
    public AjaxResult<GetPostInfoResponse> getPostInfo(@RequestParam(value = "id", required = true) Long id) {
        return null;
    }

    @Operation(summary = "创建帖子")
    @PostMapping("/post/create")
    public AjaxResult<Void> createPost(@RequestBody CreatePostRequest request) {
        return AjaxResult.success();
    }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/post/delete")
    public AjaxResult<Void> deletePost(@RequestParam(value = "id", required = true) Long id) {
        return AjaxResult.success();
    }

    @Operation(summary = "获取帖子列表")
    @GetMapping("/post/list")
    public AjaxResult<BaseListResponse<GetPostListElement>> getPostList(@RequestParam GetPostListRequest request) {
        return AjaxResult.success();
    }

    @Operation(summary = "获取我的帖子列表")
    @GetMapping("/post/my_list")
    public AjaxResult<BaseListResponse<GetMyPostListElement>> getMyPostList(@RequestParam BaseListRequest request) {
        return AjaxResult.success();
    }
}
