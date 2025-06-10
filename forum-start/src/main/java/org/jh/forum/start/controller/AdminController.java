package org.jh.forum.start.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author MangoGovo
 */
@Slf4j
@RequestMapping("/admin")
@RestController
@Tag(name = "管理员", description = "管理员相关接口")
@SaCheckLogin
@SaCheckRole(value = {"Admin", "SuperAdmin"}, mode = SaMode.OR)
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
}
