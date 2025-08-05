package org.jh.forum.start.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.jh.forum.api.dubbo.service.LoginService;
import org.jh.forum.api.dubbo.service.UserService;
import org.jh.forum.common.constants.UserTypeEnum;
import org.jh.forum.common.dto.request.LoginRequest;
import org.jh.forum.common.dto.request.UpdateUserDetailRequest;
import org.jh.forum.common.dto.response.GetUserProfileResponse;
import org.jh.forum.common.dto.response.LoginResponse;
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
        UserTypeEnum userType = loginService.login(request.getUsername(), request.getPassword(), request.getLoginType());
        return AjaxResult.success(new LoginResponse(userType));
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
    public AjaxResult<Void> updateMyProfile(@Valid @RequestBody UpdateUserDetailRequest request) {
        userService.updateUserProfile(request);
        return AjaxResult.success();
    }
}
