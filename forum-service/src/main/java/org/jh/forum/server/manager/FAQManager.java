package org.jh.forum.server.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.dto.request.BaseListRequest;

import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.FAQ.*;
import org.jh.forum.common.entity.FAQ;
import org.jh.forum.server.mapper.FAQMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.stream.Collectors;

/**
 * FAQ业务管理器
 *
 * @author ZeroHzzzz
 */
@Slf4j
@Component
public class FAQManager {
    
    @Autowired
    private FAQMapper faqMapper;
    
    /**
     * 获取所有分类
     */
    public List<String> getAllCategories() {
        log.info("开始获取FAQ分类列表");
        
        // 返回固定的分类列表
        List<String> fixedCategories = List.of(
                "账号问题", 
                "学院问题", 
                "帖子问题", 
                "猜你想问"
        );
        
        log.info("返回固定的FAQ分类列表，共{}个分类", fixedCategories.size());
        fixedCategories.forEach(category -> log.info("  - 分类: [{}]", category));
        
        return fixedCategories;
    }
    
    /**
     * 根据分类获取FAQ列表
     */
    public BaseListResponse<FAQQuestionListResponse> getFAQQuestions(String category, BaseListRequest pageRequest) {
        log.info("开始查询FAQ问题列表，分类参数：[{}]", category);
        
        // 设置默认分页参数
        Integer pageNum = pageRequest.getPage() != null ? pageRequest.getPage() : 1;
        Integer pageSize = pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10;
        Page<FAQ> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<FAQ> wrapper = new LambdaQueryWrapper<>();
        
        if (category != null && !category.trim().isEmpty()) {
            log.info("添加分类过滤条件：[{}]", category);
            wrapper.eq(FAQ::getCategory, category.trim());
        } else {
            log.info("未指定分类，查询所有分类的问题");
        }
        // 移除手动的deleted条件，让@TableLogic自动处理
        wrapper.orderByDesc(FAQ::getCreatedAt);
        
        IPage<FAQ> faqPage = faqMapper.selectPage(page, wrapper);
        log.info("数据库查询结果：总数={}, 当前页记录数={}", faqPage.getTotal(), faqPage.getRecords().size());
        
        List<FAQQuestionListResponse> questionList = faqPage.getRecords().stream()
                .map(faq -> {
                    log.debug("处理FAQ记录：ID={}, 分类=[{}], 问题=[{}]", faq.getId(), faq.getCategory(), faq.getQuestion());
                    FAQQuestionListResponse response = new FAQQuestionListResponse();
                    response.setQuestionId(faq.getId());
                    response.setCategory(faq.getCategory());
                    response.setQuestion(faq.getQuestion());
                    response.setCreatedAt(faq.getCreatedAt());
                    return response;
                })
                .collect(Collectors.toList());
        
        log.info("最终返回结果：共{}条记录", questionList.size());
        return BaseListResponse.<FAQQuestionListResponse>builder()
                .list(questionList)
                .total(faqPage.getTotal())
                .page(pageRequest.getPage())
                .pageSize(pageRequest.getPageSize())
                .build();
    }
    
    /**
     * 根据ID获取FAQ详情
     */
    public FAQDetailResponse getFAQDetail(Long questionId) {
        LambdaQueryWrapper<FAQ> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FAQ::getId, questionId);
        // 移除手动的deleted条件，让@TableLogic自动处理
        
        FAQ faq = faqMapper.selectOne(wrapper);
        if (faq == null) {
            return null;
        }
        
        // 增加浏览量
        faq.setViewCount(faq.getViewCount() == null ? 1 : faq.getViewCount() + 1);
        faqMapper.updateById(faq);
        
        FAQDetailResponse response = new FAQDetailResponse();
        response.setCategory(faq.getCategory());
        response.setQuestion(faq.getQuestion());
        response.setAnswer(faq.getAnswer());
        response.setViewCount(faq.getViewCount());
        response.setCreatedAt(faq.getCreatedAt());
        response.setUpdatedAt(faq.getUpdatedAt());
        
        return response;
    }
    
    /**
     * 创建FAQ
     */
    public FAQOperationResponse createFAQ(String category, String question, String answer) {
        try {
            FAQ faq = FAQ.builder()
                    .category(category)
                    .question(question)
                    .answer(answer)
                    .viewCount(0)
                    .build();
            
            faqMapper.insert(faq);
            
            return FAQOperationResponse.builder()
                    .success(true)
                    .questionId(faq.getId())
                    .message("创建成功")
                    .build();
        } catch (Exception e) {
            log.error("创建FAQ失败", e);
            return FAQOperationResponse.builder()
                    .success(false)
                    .message("创建失败: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * 更新FAQ
     */
    public FAQOperationResponse updateFAQ(Long questionId, String category, String question, String answer) {
        try {
            FAQ existingFaq = faqMapper.selectById(questionId);
            if (existingFaq == null || existingFaq.getDeleted()) {
                return FAQOperationResponse.builder()
                        .success(false)
                        .questionId(questionId)
                        .message("FAQ不存在")
                        .build();
            }
            
            FAQ updateFaq = FAQ.builder()
                    .id(questionId)
                    .category(category != null ? category : existingFaq.getCategory())
                    .question(question != null ? question : existingFaq.getQuestion())
                    .answer(answer != null ? answer : existingFaq.getAnswer())
                    .viewCount(existingFaq.getViewCount())
                    .build();
            
            faqMapper.updateById(updateFaq);
            
            return FAQOperationResponse.builder()
                    .success(true)
                    .questionId(questionId)
                    .message("更新成功")
                    .build();
        } catch (Exception e) {
            log.error("更新FAQ失败", e);
            return FAQOperationResponse.builder()
                    .success(false)
                    .questionId(questionId)
                    .message("更新失败: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * 删除FAQ
     */
    public FAQOperationResponse deleteFAQ(Long questionId) {
        try {
            FAQ faq = FAQ.builder()
                    .id(questionId)
                    .deleted(true)
                    .build();
            
            int result = faqMapper.updateById(faq);
            
            return FAQOperationResponse.builder()
                    .success(result > 0)
                    .questionId(questionId)
                    .message(result > 0 ? "删除成功" : "删除失败")
                    .build();
        } catch (Exception e) {
            log.error("删除FAQ失败", e);
            return FAQOperationResponse.builder()
                    .success(false)
                    .questionId(questionId)
                    .message("删除失败: " + e.getMessage())
                    .build();
        }
    }
    

}