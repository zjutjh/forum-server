package org.jh.forum.start.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;

import org.jh.forum.common.dto.request.BaseListRequest;
import org.jh.forum.common.dto.request.FAQ.*;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.FAQ.*;
import org.jh.forum.api.dubbo.service.FAQService;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * FAQ管理接口
 *
 * @author ZeroHzzzz
 */
@Slf4j
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
    public AjaxResult<List<FAQCategoryResponse>> getFAQCategories() {
        try {
            log.info("开始获取FAQ分类列表");
            List<FAQCategoryResponse> categoryList = faqService.getFAQCategories();
            if (categoryList == null) {
                log.warn("FAQ分类列表为空");
                categoryList = new ArrayList<>();
            }
            log.info("成功获取FAQ分类列表，共{}个分类", categoryList.size());
            return AjaxResult.success(categoryList);
        } catch (Exception e) {
            log.error("获取FAQ分类列表失败", e);
            return AjaxResult.fail(500, "获取分类列表失败：" + e.getMessage());
        }
    }    
    
    /**
     * 根据分类ID获取问题列表
     */
    @Operation(summary = "获取分类问题列表")
    @GetMapping("/questions/list")
    public AjaxResult<BaseListResponse<FAQQuestionListResponse>> getCategoryQuestions(
            @ModelAttribute FAQQuestionListRequest categoryRequest,
            @ModelAttribute BaseListRequest pageRequest) {
        try {
            // 设置默认分页参数
            if (pageRequest.getPage() == null) {
                pageRequest.setPage(1);
            }
            if (pageRequest.getPageSize() == null) {
                pageRequest.setPageSize(10);
            }
            
            log.info("开始获取FAQ问题列表，分类：{}, 页码：{}, 页大小：{}", 
                    categoryRequest.getCategory(), pageRequest.getPage(), pageRequest.getPageSize());
            
            BaseListResponse<FAQQuestionListResponse> response = faqService.getFAQQuestions(categoryRequest, pageRequest);
            log.info("成功获取FAQ问题列表，共{}条记录", response.getTotal());
            return AjaxResult.success(response);
        } catch (Exception e) {
            log.error("获取FAQ问题列表失败，分类：{}", categoryRequest.getCategory(), e);
            return AjaxResult.fail(500, "获取问题列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取问题详情
     */
    @Operation(summary = "获取问题详情", description = "根据问题ID获取FAQ问题的详细信息")
    @PostMapping("/questions/detail")
    public AjaxResult<FAQDetailResponse> getQuestionDetail(@RequestBody @Valid FAQQuestionDetailRequest request) {
        try {
            log.info("开始获取FAQ问题详情，问题ID：{}", request.getQuestionId());
            
            if (request.getQuestionId() == null || request.getQuestionId() <= 0) {
                log.warn("问题ID无效：{}", request.getQuestionId());
                return AjaxResult.fail(400, "问题ID不能为空或小于等于0");
            }
            
            FAQDetailResponse detail = faqService.getFAQDetail(request);
            log.info("成功获取FAQ问题详情，问题ID：{}", request.getQuestionId());
            return AjaxResult.success(detail);
        } catch (Exception e) {
            log.error("获取FAQ问题详情失败，问题ID：{}", request.getQuestionId(), e);
            return AjaxResult.fail(500, "获取问题详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建新的FAQ问题
     */
    @Operation(summary = "创建问题", description = "创建新的FAQ问题")
    @PostMapping("/manage/create")
    public AjaxResult<FAQOperationResponse> createQuestion(@RequestBody @Valid FAQQuestionCreateRequest request) {
        try {
            log.info("开始创建FAQ问题，问题：{}, 分类：{}", request.getQuestion(), request.getCategory());
            
            // 基本参数验证
            if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
                log.warn("创建FAQ问题失败：问题描述不能为空");
                return AjaxResult.fail(400, "问题描述不能为空");
            }
            
            if (request.getAnswer() == null || request.getAnswer().trim().isEmpty()) {
                log.warn("创建FAQ问题失败：答案不能为空");
                return AjaxResult.fail(400, "问题答案不能为空");
            }
            
            if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
                log.warn("创建FAQ问题失败：分类不能为空：{}", request.getCategory());
                return AjaxResult.fail(400, "分类不能为空");
            }
            
            FAQOperationResponse result = faqService.createFAQ(request);
            log.info("成功创建FAQ问题，问题ID：{}", result.getQuestionId());
            return AjaxResult.success(result);
        } catch (Exception e) {
            log.error("创建FAQ问题失败，问题：{}", request.getQuestion(), e);
            return AjaxResult.fail(500, "创建问题失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新FAQ问题
     */
    @Operation(summary = "更新问题", description = "更新已有的FAQ问题")
    @PostMapping("/manage/update")
    public AjaxResult<FAQOperationResponse> updateQuestion(@RequestBody @Valid FAQQuestionUpdateRequest request) {
        try {
            log.info("开始更新FAQ问题，问题ID：{}", request.getQuestionId());
            
            // 基本参数验证
            if (request.getQuestionId() == null || request.getQuestionId() <= 0) {
                log.warn("更新FAQ问题失败：问题ID无效：{}", request.getQuestionId());
                return AjaxResult.fail(400, "问题ID不能为空或小于等于0");
            }
            
            if (request.getQuestion() != null && request.getQuestion().trim().isEmpty()) {
                log.warn("更新FAQ问题失败：问题描述不能为空字符串");
                return AjaxResult.fail(400, "问题描述不能为空字符串");
            }
            
            if (request.getAnswer() != null && request.getAnswer().trim().isEmpty()) {
                log.warn("更新FAQ问题失败：答案不能为空字符串");
                return AjaxResult.fail(400, "问题答案不能为空字符串");
            }
            
            FAQOperationResponse result = faqService.updateFAQ(request);
            log.info("成功更新FAQ问题，问题ID：{}", request.getQuestionId());
            return AjaxResult.success(result);
        } catch (Exception e) {
            log.error("更新FAQ问题失败，问题ID：{}", request.getQuestionId(), e);
            return AjaxResult.fail(500, "更新问题失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除FAQ问题
     */
    @Operation(summary = "删除问题", description = "删除指定的FAQ问题")
    @PostMapping("/manage/delete")
    public AjaxResult<FAQOperationResponse> deleteQuestion(@RequestBody @Valid FAQQuestionDeleteRequest request) {
        try {
            log.info("开始删除FAQ问题，问题ID：{}", request.getQuestionId());
            
            // 基本参数验证
            if (request.getQuestionId() == null || request.getQuestionId() <= 0) {
                log.warn("删除FAQ问题失败：问题ID无效：{}", request.getQuestionId());
                return AjaxResult.fail(400, "问题ID不能为空或小于等于0");
            }
            
            FAQOperationResponse result = faqService.deleteFAQ(request);
            log.info("成功删除FAQ问题，问题ID：{}", request.getQuestionId());
            return AjaxResult.success(result);
        } catch (Exception e) {
            log.error("删除FAQ问题失败，问题ID：{}", request.getQuestionId(), e);
            return AjaxResult.fail(500, "删除问题失败：" + e.getMessage());
        }
    }
    
}
