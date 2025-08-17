package org.jh.forum.start.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.jh.forum.api.dubbo.service.UserService;
import org.jh.forum.common.dto.request.GetAdminListRequest;
import org.jh.forum.common.dto.request.GetUserListRequest;
import org.jh.forum.common.dto.request.MuteUserRequest;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.GetAdminListElement;
import org.jh.forum.common.dto.response.GetUserDetailResponse;
import org.jh.forum.common.dto.response.GetUserListElement;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * @author MangoGovo
 */
@Slf4j
@RequestMapping("/account")
@RestController
@Tag(name = "账号管理")
@SaCheckLogin
@SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
public class AccountController {
    @DubboReference
    private UserService userService;

    @Operation(summary = "管理员获取普通用户列表")
    @GetMapping("/users")
    public AjaxResult<BaseListResponse<GetUserListElement>> getUserList(@Valid GetUserListRequest request) {
        return AjaxResult.success(userService.getUserList(request));
    }

    @Operation(summary = "管理员设置用户禁言")
    @PostMapping("/mute")
    public AjaxResult<Void> muteUser(@Valid @RequestBody MuteUserRequest request) {
        userService.muteUser(request);
        return AjaxResult.success();
    }

    @Operation(summary = "管理员获取用户详情")
    @GetMapping("/detail")
    public AjaxResult<GetUserDetailResponse> getUserDetail(@RequestParam("id") Long id) {
        return AjaxResult.success(userService.getUserDetail(id));
    }

    @Operation(summary = "管理员列表")
    @GetMapping("/admins")
    @SaCheckRole(value = {"super_admin"})
    public AjaxResult<BaseListResponse<GetAdminListElement>> getAdminList(@Valid GetAdminListRequest request) {
        return AjaxResult.success(userService.getAdminList(request));
    }
}
