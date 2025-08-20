package org.jh.forum.start.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.jh.forum.api.dubbo.service.LoginService;
import org.jh.forum.api.dubbo.service.UserService;
import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * @author MangoGovo, MeaquaOWO
 */
@Slf4j
@RequestMapping("/user")
@RestController
@Tag(name = "用户", description = "用户相关接口")
public class UserController {
    @DubboReference
    private LoginService loginService;

    @DubboReference
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public AjaxResult<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return AjaxResult.success(loginService.userLogin(request.getUsername(), request.getPassword()));
    }

    @Operation(summary = "通用注册")
    @PostMapping("/register")
    public AjaxResult<Void> register(@RequestBody @Valid AdminRegisterRequest request) {
        userService.adminRegister(request);
        return AjaxResult.success();
    }

    @PostMapping("/logout")
    @Operation(summary = "用户退出登录")
    @SaCheckLogin
    public AjaxResult<Void> logout() {
        StpUtil.logout();
        return AjaxResult.success();
    }

    @GetMapping("/profile")
    @Operation(summary = "获取个人信息")
    @SaCheckLogin
    public AjaxResult<GetUserProfileResponse> getProfile(@RequestParam(name = "id", required = false) Long targetUserId) {
        if (targetUserId == null) {
            targetUserId = StpUtil.getLoginIdAsLong();
        }
        return AjaxResult.success(userService.getUserProfile(targetUserId));
    }

    @PutMapping("/profile")
    @Operation(summary = "更新个人信息")
    @SaCheckLogin
    public AjaxResult<ModerationResultResponse> updateMyProfile(@Valid @RequestBody UpdateUserDetailRequest request) {
        userService.updateUserProfile(request);
        return AjaxResult.success(ModerationResultResponse.success());
    }

    @PutMapping("/background")
    @Operation(summary = "更新个人背景")
    @SaCheckLogin
    public AjaxResult<Void> updateMyBackgroundImage(@Valid @RequestBody UpdateBackgroundImageRequest request) {
        userService.updateBackgroundImage(request);
        return AjaxResult.success();
    }

    @GetMapping("/notice")
    @Operation(summary = "获取消息设置")
    @SaCheckLogin
    public AjaxResult<GetNoticeSettingsResponse> getNoticeSettings() {
        return AjaxResult.success(userService.getNoticeSettings());
    }

    @PostMapping("/notice")
    @Operation(summary = "修改消息设置")
    @SaCheckLogin
    public AjaxResult<Void> updateNoticeSettings(@Valid @RequestBody UpdateNoticeSettingsRequest request) {
        userService.updateNoticeSettings(request);
        return AjaxResult.success();
    }

    @GetMapping("/mute")
    @Operation(summary = "检查禁言状态")
    @SaCheckLogin
    public AjaxResult<CheckMuteResponse> checkMute() {
        return AjaxResult.success(userService.checkMute());
    }
}
