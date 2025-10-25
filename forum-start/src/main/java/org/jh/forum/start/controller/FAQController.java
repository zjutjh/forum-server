package org.jh.forum.start.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.dubbo.config.annotation.DubboReference;
import org.jh.forum.api.dubbo.service.FAQService;
import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.FAQDetailResponse;
import org.jh.forum.common.dto.response.FAQQuestionListElement;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * FAQ管理接口
 *
 * @author ZeroHzzzz
 */
@RestController
@RequestMapping("/faq")
@Tag(name = "FAQ管理", description = "FAQ相关接口")
@SaCheckLogin
public class FAQController {

    @DubboReference
    private FAQService faqService;

    /**
     * 根据分类ID获取问题列表
     */
    @Operation(summary = "获取分类问题列表")
    @GetMapping("/question/list")
    public AjaxResult<BaseListResponse<FAQQuestionListElement>> getCategoryQuestions(@Valid FAQQuestionListRequest request) {
        return AjaxResult.success(faqService.getFaqQuestions(request));
    }

    /**
     * 获取问题详情
     */
    @Operation(summary = "获取问题详情", description = "根据问题ID获取FAQ问题的详细信息")
    @GetMapping("/question/detail")
    public AjaxResult<FAQDetailResponse> getQuestionDetail(@Valid FAQQuestionDetailRequest request) {
        return AjaxResult.success(faqService.getFaqDetail(request));
    }

    /**
     * 创建新的FAQ问题
     */
    @Operation(summary = "创建问题", description = "创建新的FAQ问题")
    @PostMapping("/question")
    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Tag(name = "管理员")
    public AjaxResult<Void> createQuestion(@RequestBody @Valid FAQQuestionCreateRequest request) {
        faqService.createFaq(request);
        return AjaxResult.success();
    }

    /**
     * 更新FAQ问题
     */
    @Operation(summary = "更新问题", description = "更新已有的FAQ问题")
    @PutMapping("/question")
    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Tag(name = "管理员")
    public AjaxResult<Void> updateQuestion(@RequestBody @Valid FAQQuestionUpdateRequest request) {
        faqService.updateFaq(request);
        return AjaxResult.success();
    }

    /**
     * 删除FAQ问题
     */
    @Operation(summary = "删除问题", description = "删除指定的FAQ问题")
    @DeleteMapping("/question")
    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Tag(name = "管理员")
    public AjaxResult<Void> deleteQuestion(@Valid FAQQuestionDeleteRequest request) {
        faqService.deleteFaq(request);
        return AjaxResult.success();
    }
}
