package org.jh.forum.start.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
