package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.dto.request.BaseListRequest;
import org.jh.forum.common.dto.request.FAQ.*;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.FAQ.*;

import java.util.List;

/**
 * FAQ服务接口
 *
 * @author ZeroHzzzz
 */
public interface FAQService {
    
    /**
     * 获取FAQ分类列表
     * @return FAQ分类列表
     */
    List<String> getFAQCategories();
    
    /**
     * 根据分类获取FAQ问题列表
     * @param categoryRequest 分类请求参数
     * @param pageRequest 分页请求参数
     * @return FAQ问题列表
     */
    BaseListResponse<FAQQuestionListResponse> getFAQQuestions(FAQQuestionListRequest categoryRequest, BaseListRequest pageRequest);
    
    /**
     * 获取FAQ问题详情
     * @param request 问题详情请求参数
     * @return FAQ问题详情
     */
    FAQDetailResponse getFAQDetail(FAQQuestionDetailRequest request);
    
    /**
     * 创建FAQ问题
     * @param request 创建请求参数
     * @return 创建的问题ID
     */
    Long createFAQ(FAQQuestionCreateRequest request);
    
    /**
     * 更新FAQ问题
     * @param request 更新请求参数
     */
    void updateFAQ(FAQQuestionUpdateRequest request);
    
    /**
     * 删除FAQ问题
     * @param request 删除请求参数
     */
    void deleteFAQ(FAQQuestionDeleteRequest request);
}