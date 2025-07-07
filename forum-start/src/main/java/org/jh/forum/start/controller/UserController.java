package org.jh.forum.start.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.api.dubbo.message.LoginReq;
import org.jh.forum.api.dubbo.service.LoginService;
import org.jh.forum.common.dto.request.LoginRequest;
import org.jh.forum.common.dto.response.LoginResponse;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.common.exceptions.ForumServiceException;
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
    @Resource
    private LoginService loginService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public AjaxResult<Object> login(@RequestBody @Valid LoginRequest request) {
        try {
            LoginReq loginReq = LoginReq.builder()
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .loginType(request.getLoginType()).build();
            String userType = loginService.login(loginReq);
            return AjaxResult.success(LoginResponse.builder().userType(userType).build());
        } catch (ForumServiceException e) {
            throw new ApiException(e);
        }
    }
}
