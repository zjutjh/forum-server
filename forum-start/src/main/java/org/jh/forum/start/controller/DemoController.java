package org.jh.forum.start.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.jh.forum.api.dubbo.CorrectDataService;
import org.jh.forum.api.dubbo.PublishPostReq;
import org.jh.forum.common.dto.request.DemoRequest;
import org.jh.forum.common.dto.response.DemoResponse;
import org.jh.forum.server.config.service.NacosConfigAService;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.*;

/**
 * <p>样例接口</p>
 * 将 localhost:8080/v3/api-docs 导入ApiFox即可自动同步接口文档 (项目概览-自动导入-设置-OpenAPI/Swagger-URL方式导入)
 *
 * @author MangoGovo
 */
@Slf4j
@RestController
@Tag(name = "样例接口", description = "接口描述")
public class DemoController {
    @Resource
    private CorrectDataService correctDataService;

    @Operation(summary = "Get接口样例")
    @GetMapping("/hello")
    public String hello(@RequestParam(name = "name", defaultValue = "unknown user") String name) {
        correctDataService.publishPost(PublishPostReq.newBuilder().setContext("123").setUid("2").setTitle("title").build());
        return "Hello " + name + ". I am " + NacosConfigAService.nacosConfigA.getName();
    }

    @Operation(summary = "Post接口样例")
    @PostMapping("/hello")
    public AjaxResult<DemoResponse> hello(@RequestBody DemoRequest request) {
        return AjaxResult.success(DemoResponse.builder().greet("hello " + request.getName()).build());
    }
}