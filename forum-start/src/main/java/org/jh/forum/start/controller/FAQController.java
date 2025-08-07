package org.jh.forum.start.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.dubbo.config.annotation.DubboReference;
import org.jh.forum.common.dto.request.BaseListRequest;
import org.jh.forum.common.dto.request.FAQ.*;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.FAQ.FAQDetailResponse;
import org.jh.forum.common.dto.response.FAQ.FAQQuestionListResponse;
import org.jh.forum.api.dubbo.service.FAQService;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FAQ管理接口
 *
 * @author ZeroHzzzz
 */
@RestController
@RequestMapping("/faq")
@Tag(name = "FAQ管理", description = "提供FAQ问题和分类的增、删、改、查功能")
public class FAQController {
    
    @DubboReference(version = "1.0.0")
    private FAQService faqService;

    /**
     * 获取FAQ分类列表
     */
    @Operation(summary = "获取FAQ分类列表", description = "获取所有可用的FAQ分类")
    @GetMapping("/categories")
    public AjaxResult<List<String>> getFAQCategories() {
        List<String> categoryList = faqService.getFAQCategories();
        return AjaxResult.success(categoryList);
    }    
    
    /**
     * 根据分类ID获取问题列表
     */
    @Operation(summary = "获取分类问题列表")
    @GetMapping("/questions/list")
    public AjaxResult<BaseListResponse<FAQQuestionListResponse>> getCategoryQuestions(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        
        FAQQuestionListRequest categoryRequest = new FAQQuestionListRequest();
        categoryRequest.setCategory(category);
        
        BaseListRequest pageRequest = new BaseListRequest();
        pageRequest.setPage(page);
        pageRequest.setPageSize(pageSize);
        
        BaseListResponse<FAQQuestionListResponse> response = faqService.getFAQQuestions(categoryRequest, pageRequest);
        return AjaxResult.success(response);
    }
    
    /**
     * 获取问题详情
     */
    @Operation(summary = "获取问题详情", description = "根据问题ID获取FAQ问题的详细信息")
    @PostMapping("/questions/detail")
    public AjaxResult<FAQDetailResponse> getQuestionDetail(@RequestBody @Valid FAQQuestionDetailRequest request) {
        FAQDetailResponse detail = faqService.getFAQDetail(request);
        return AjaxResult.success(detail);
    }
    
    /**
     * 创建新的FAQ问题
     */
    @Operation(summary = "创建问题", description = "创建新的FAQ问题")
    @PostMapping("/manage/create")
    public AjaxResult<Long> createQuestion(@RequestBody @Valid FAQQuestionCreateRequest request) {
        Long questionId = faqService.createFAQ(request);
        return AjaxResult.success(questionId);
    }
    
    /**
     * 更新FAQ问题
     */
    @Operation(summary = "更新问题", description = "更新已有的FAQ问题")
    @PostMapping("/manage/update")
    public AjaxResult<Void> updateQuestion(@RequestBody @Valid FAQQuestionUpdateRequest request) {
        faqService.updateFAQ(request);
        return AjaxResult.success();
    }
    
    /**
     * 删除FAQ问题
     */
    @Operation(summary = "删除问题", description = "删除指定的FAQ问题")
    @PostMapping("/manage/delete")
    public AjaxResult<Void> deleteQuestion(@RequestBody @Valid FAQQuestionDeleteRequest request) {
        faqService.deleteFAQ(request);
        return AjaxResult.success();
    }
    
}
