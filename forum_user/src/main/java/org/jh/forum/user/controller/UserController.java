package org.jh.forum.user.controller;

import org.jh.forum.user.dto.EditUserDTO;
import org.jh.forum.user.dto.UserDetailDTO;
import org.jh.forum.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取用户详情接口
     * @param userId 路径参数，用户ID
     * @return 用户详情 DTO
     */

    @GetMapping("/{userId}")
    public Result<UserDetailDTO> getUserInfo(@PathVariable Long userId) {
        return Result.success(userService.getUserDetail(userId));
    }

    /**
     * 编辑资料接口
     * 处理昵称、性别、签名等字段更新
     * @param dto 请求体，包含编辑数据
     * @return 操作结果
     */
    @PatchMapping("/profile")
    public Result<?> editProfile(@RequestBody EditUserDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((CustomUserDetails) auth.getPrincipal()).getId();
        userService.editProfile(dto, userId);
        return Result.success();
    }
}