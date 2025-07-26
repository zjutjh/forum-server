package org.jh.forum.start.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.api.dubbo.service.LoginService;
import org.jh.forum.common.constants.UserTypeEnum;
import org.jh.forum.common.dto.request.LoginRequest;
import org.jh.forum.common.dto.response.LoginResponse;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * @author MangoGovo
 */
@Slf4j
@RequestMapping("/user")
@RestController
@Tag(name = "用户", description = "用户相关接口")
public class UserController {
    @DubboReference
    private LoginService loginService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public AjaxResult<Object> login(@RequestBody @Valid LoginRequest request) {
        UserTypeEnum userType = loginService.login(request.getUsername(), request.getPassword(), request.getLoginType());
        return AjaxResult.success(LoginResponse.builder().userType(userType).build());
    }

    @PostMapping("/logout")
    @Operation(summary = "用户退出登录")
    @SaCheckLogin
    public AjaxResult<Void> logout() {
        StpUtil.logout();
        return AjaxResult.success();
    }


    /**
     * @author MeaquaOWO
     */
    @Autowired
    private UserService userService;

    //普通用户访问自己
    @GetMapping("/profile")
    public AjaxResult<UserDetailResponse> getMyProfile() {
        Long userId = StpUtil.getLoginIdAsLong();
        UserDTO targetUser = userService.getUserById(userId);
        return AjaxResult.success(userService.filterFields(targetUser, userId));
    }

    // 普通用户访问他人
    @GetMapping("/profile/userId")
    public AjaxResult<UserDetailResponse> getOtherProfile(@RequestParam(name="userId",required=false) String targetUserId) {
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

    // 普通用户更新自己资料
    @PutMapping("/profile")
    public AjaxResult<Void> updateMyProfile(@RequestBody UserUpdateRequest dto) {
        userService.updateUserProfile(dto);
        return AjaxResult.success();
    }

}
