package org.jh.forum.user.controller;

import org.jh.forum.user.dto.request.PrivacySettingRequest;
import org.jh.forum.user.dto.response.UserProfileResponse;
import org.jh.forum.user.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    // 获取用户基础资料（含隐私字段）
    @GetMapping("/{userId}/profile")
    @ApiOperation(value = "获取用户公开资料", notes = "返回用户基础信息及隐私字段（如邮箱、简介）")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @ApiParam(value = "目标用户ID", required = true, example = "123")
            @PathVariable Long userId,
            //@RequestHeader(value = "X-Current-User", required = false)
            //@ApiParam(value = "当前用户ID（用于权限校验）", required = false, example = "456")
            Long currentUserId) {

        UserProfileResponse response = userProfileService.getUserProfile(userId, currentUserId);
        // 清理非必要字段（仅保留 profile 相关字段）
        response.setPosts(null);
        response.setFollowers(null);
        return ResponseEntity.ok(response);
    }

    // 更新用户隐私设置
    @PutMapping("/{userId}/privacy")
    @ApiOperation(value = "更新用户隐私设置", notes = "修改用户的隐私选项（如是否显示邮箱、帖子等）")
    public ResponseEntity<Void> updatePrivacySettings(
            @ApiParam(value = "用户ID", required = true, example = "123")
            @PathVariable Long userId,
            @RequestBody @ApiParam(value = "隐私设置请求体") PrivacySettingRequest request) {

        userProfileService.updatePrivacySettings(userId, request);
        return ResponseEntity.noContent().build();
    }

    // 获取用户帖子列表
    @GetMapping("/{userId}/posts")
    @ApiOperation(value = "获取用户帖子", notes = "返回指定用户的公开帖子列表")
    public ResponseEntity<UserProfileResponse> getUserPosts(
            @ApiParam(value = "目标用户ID", required = true, example = "123")
            @PathVariable Long userId,
            //@RequestHeader(value = "X-Current-User", required = false)
            //@ApiParam(value = "当前用户ID（用于权限校验）", required = false, example = "456")
            Long currentUserId) {

        UserProfileResponse response = userProfileService.getUserPosts(userId, currentUserId);
        // 清理非必要字段
        response.setEmail(null);
        response.setBio(null);
        response.setFollowers(null);
        return ResponseEntity.ok(response);
    }

    // 获取用户关注者列表
    @GetMapping("/{userId}/followers")
    @ApiOperation(value = "获取用户关注者", notes = "返回指定用户的关注者列表")
    public ResponseEntity<UserProfileResponse> getUserFollowers(
            @ApiParam(value = "目标用户ID", required = true, example = "123")
            @PathVariable Long userId,
            //@RequestHeader(value = "X-Current-User", required = false)
            //@ApiParam(value = "当前用户ID（用于权限校验）", required = false, example = "456")
            Long currentUserId) {

        UserProfileResponse response = userProfileService.getUserFollowers(userId, currentUserId);
        // 清理非必要字段
        response.setEmail(null);
        response.setBio(null);
        response.setPosts(null);
        return ResponseEntity.ok(response);
    }
}