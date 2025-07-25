package org.jh.forum.start.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.google.protobuf.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.api.dubbo.service.UserService;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.dto.UserDTO;
import org.jh.forum.common.dto.request.MuteRequest;
import org.jh.forum.common.dto.request.UserUpdateAdminRequest;
import org.jh.forum.common.dto.response.UserDetailResponse;
import org.jh.forum.common.entity.User;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.mapper.UserMapper;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author MangoGovo
 */
@Slf4j
@RequestMapping("/admin")
@RestController
@Tag(name = "管理员", description = "管理员相关接口")
@SaCheckLogin
@SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
public class AdminController {
    @PostMapping("/test")
    @Operation(summary = "管理员测试")
    public AjaxResult<Object> test() {
        // 获取当前登录用户ID
        long userId = StpUtil.getLoginIdAsLong();
        log.info("用户ID {}", userId);
        // 获取当前登录用户角色
        List<String> roleList = StpUtil.getRoleList();
        log.info("角色权限列表 {}", roleList);
        return AjaxResult.success();
    }

    /**
     * @author MeaquaOWO
     */
    @Autowired
    private UserService userService;

    // 禁言/解禁用户
    @PutMapping("/muted")
    public AjaxResult<Void> updateMuteStatus(@RequestBody MuteRequest dto) {
        Long userId= Long.parseLong(dto.getUserId());
        try {
            userService.updateMuteStatus(userId, dto.getMutedUntil());
            return AjaxResult.success();
        } catch (ApiException e) {
            return AjaxResult.fail(ExceptionEnum.RESOURCE_NOT_FOUND);
        } catch (Exception e) {
            return AjaxResult.fail(ExceptionEnum.SERVER_ERROR);
        }
    }

    // 查询禁言状态
    @GetMapping("/userId/muted")
    public AjaxResult<Boolean> getMuteStatus(@RequestParam(name="userId",required=false) String targetUserId) {
        Long userId= Long.parseLong(targetUserId);
        try {
            boolean isMuted = userService.isUserMuted(userId);
            return AjaxResult.success(isMuted);
        } catch (ApiException e) {
            return AjaxResult.fail(ExceptionEnum.RESOURCE_NOT_FOUND);
        } catch (Exception e) {
            return AjaxResult.fail(ExceptionEnum.SERVER_ERROR);
        }
    }

    // 管理员获取用户资料
    @GetMapping("/users/userId")
    public AjaxResult<UserDetailResponse> getProfileByAdmin(@RequestParam(name="userId",required=false) String targetUserId) {
        Long userId= Long.parseLong(targetUserId);
        Long currentUserId = StpUtil.getLoginIdAsLong();
        if (userId.equals(currentUserId)) {
            UserDTO targetUser = userService.getUserById(userId);
            return AjaxResult.success(userService.filterFields(targetUser, userId));
        } else {
            UserDTO targetUser = userService.getUserById(userId);
            return AjaxResult.success(userService.filterFields(targetUser, userId));
        }
    }

    // 管理员更新用户资料
    @PutMapping("/users")
    //@SaCheckRole("ADMIN")
    public AjaxResult<Void> updateProfileByAdmin(@RequestBody UserUpdateAdminRequest dto) {
        Long userId= Long.parseLong(dto.getUserId());
        userService.updateUserProfileByAdmin(userId, dto);
        return AjaxResult.success();
    }
}
