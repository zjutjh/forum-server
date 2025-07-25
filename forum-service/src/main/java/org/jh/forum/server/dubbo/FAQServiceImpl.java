package org.jh.forum.server.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.FAQService;
import org.jh.forum.common.dto.request.BaseListRequest;
import org.jh.forum.common.dto.request.FAQ.*;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.FAQ.*;
import org.jh.forum.server.manager.FAQManager;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * FAQ服务实现类
 *
 * @author ZeroHzzzz
 */
@DubboService(version = "1.0.0")
@Slf4j
public class FAQServiceImpl implements FAQService {
    
    @Resource
    private FAQManager faqManager;

    @Override
    public List<FAQCategoryResponse> getFAQCategories() {
        try {
            return faqManager.getAllCategories();
        } catch (Exception e) {
            log.error("获取FAQ分类失败", e);
            throw e;
        }
    }

    @Override
    public BaseListResponse<FAQQuestionListResponse> getFAQQuestions(FAQQuestionListRequest categoryRequest, BaseListRequest pageRequest) {
        try {
            String category = categoryRequest != null ? categoryRequest.getCategory() : null;
            return faqManager.getFAQQuestions(category, pageRequest);
        } catch (Exception e) {
            log.error("获取FAQ问题列表失败, categoryRequest: {}", categoryRequest, e);
            throw e;
        }
    }

    @Override
    public FAQDetailResponse getFAQDetail(FAQQuestionDetailRequest request) {
        try {
            return faqManager.getFAQDetail(request.getQuestionId());
        } catch (Exception e) {
            log.error("获取FAQ详情失败, request: {}", request, e);
            throw e;
        }
    }

    @Override
    public FAQOperationResponse createFAQ(FAQQuestionCreateRequest request) {
        try {
            return faqManager.createFAQ(request.getCategory(), request.getQuestion(), request.getAnswer());
        } catch (Exception e) {
            log.error("创建FAQ失败, request: {}", request, e);
            throw e;
        }
    }

    @Override
    public FAQOperationResponse updateFAQ(FAQQuestionUpdateRequest request) {
        try {
            return faqManager.updateFAQ(request.getQuestionId(), request.getCategory(), request.getQuestion(), request.getAnswer());
        } catch (Exception e) {
            log.error("更新FAQ失败, request: {}", request, e);
            throw e;
        }
    }

    @Override
    public FAQOperationResponse deleteFAQ(FAQQuestionDeleteRequest request) {
        try {
            return faqManager.deleteFAQ(request.getQuestionId());
        } catch (Exception e) {
            log.error("删除FAQ失败, request: {}", request, e);
            throw e;
        }
    }
}