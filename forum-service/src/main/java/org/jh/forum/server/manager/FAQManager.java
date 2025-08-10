package org.jh.forum.server.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.FAQCategoryEnum;
import org.jh.forum.common.dto.request.BaseListRequest;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.FAQ.*;
import org.jh.forum.common.entity.FAQ;
import org.jh.forum.common.exceptions.ApiException;
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
@Component
public class FAQManager {
    
    @Autowired
    private FAQMapper faqMapper;
    
    /**
     * 获取所有分类
     */
    public List<String> getAllCategories() {
        return List.of(FAQCategoryEnum.getAllDescriptions());
    }
    
    /**
     * 根据分类获取FAQ列表
     */
    public BaseListResponse<FAQQuestionListResponse> getFAQQuestions(String category, BaseListRequest pageRequest) {
        // 设置默认分页参数
        Integer pageNum = pageRequest.getPage() != null ? pageRequest.getPage() : 1;
        Integer pageSize = pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10;
        Page<FAQ> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<FAQ> wrapper = new LambdaQueryWrapper<>();
        
        if (category != null && !category.trim().isEmpty()) {
            wrapper.eq(FAQ::getCategory, category.trim());
        }
        wrapper.orderByDesc(FAQ::getCreatedAt);
        
        IPage<FAQ> faqPage = faqMapper.selectPage(page, wrapper);
        
        List<FAQQuestionListResponse> questionList = faqPage.getRecords().stream()
                .map(faq -> {
                    FAQQuestionListResponse response = new FAQQuestionListResponse();
                    response.setQuestionId(faq.getId());
                    response.setCategory(faq.getCategory());
                    response.setQuestion(faq.getQuestion());
                    response.setCreatedAt(faq.getCreatedAt());
                    return response;
                })
                .collect(Collectors.toList());
        
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
    public Long createFAQ(String category, String question, String answer) {
        FAQ faq = FAQ.builder()
                .category(category)
                .question(question)
                .answer(answer)
                .viewCount(0)
                .build();
        
        faqMapper.insert(faq);
        return faq.getId();
    }
    
    /**
     * 更新FAQ
     */
    public void updateFAQ(Long questionId, String category, String question, String answer) {
        FAQ existingFaq = faqMapper.selectById(questionId);
        if (existingFaq == null || existingFaq.getDeleted()) {
            throw new ApiException(ExceptionEnum.FAQ_NOT_FOUND);
        }
        
        FAQ updateFaq = FAQ.builder()
                .id(questionId)
                .category(category != null ? category : existingFaq.getCategory())
                .question(question != null ? question : existingFaq.getQuestion())
                .answer(answer != null ? answer : existingFaq.getAnswer())
                .viewCount(existingFaq.getViewCount())
                .build();
        
        faqMapper.updateById(updateFaq);
    }
    
    /**
     * 删除FAQ
     */
    public void deleteFAQ(Long questionId) {
        FAQ existingFaq = faqMapper.selectById(questionId);
        if (existingFaq == null || existingFaq.getDeleted()) {
            throw new ApiException(ExceptionEnum.FAQ_NOT_FOUND);
        }
        
        FAQ faq = FAQ.builder()
                .id(questionId)
                .deleted(true)
                .build();
        
        int result = faqMapper.updateById(faq);
        if (result <= 0) {
            throw new ApiException(ExceptionEnum.SERVER_ERROR);
        }
    }

}