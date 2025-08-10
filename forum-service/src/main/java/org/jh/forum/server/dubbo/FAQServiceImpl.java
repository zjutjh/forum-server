package org.jh.forum.server.dubbo;

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
public class FAQServiceImpl implements FAQService {
    
    @Resource
    private FAQManager faqManager;

    @Override
    public List<String> getFAQCategories() {
        return faqManager.getAllCategories();
    }

    @Override
    public BaseListResponse<FAQQuestionListResponse> getFAQQuestions(FAQQuestionListRequest categoryRequest, BaseListRequest pageRequest) {
        String category = categoryRequest != null ? categoryRequest.getCategory() : null;
        return faqManager.getFAQQuestions(category, pageRequest);
    }

    @Override
    public FAQDetailResponse getFAQDetail(FAQQuestionDetailRequest request) {
        return faqManager.getFAQDetail(request.getQuestionId());
    }

    @Override
    public Long createFAQ(FAQQuestionCreateRequest request) {
        return faqManager.createFAQ(request.getCategory(), request.getQuestion(), request.getAnswer());
    }

    @Override
    public void updateFAQ(FAQQuestionUpdateRequest request) {
        faqManager.updateFAQ(request.getQuestionId(), request.getCategory(), request.getQuestion(), request.getAnswer());
    }

    @Override
    public void deleteFAQ(FAQQuestionDeleteRequest request) {
        faqManager.deleteFAQ(request.getQuestionId());
    }
}