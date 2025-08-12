package org.jh.forum.server.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.FAQService;
import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.FAQDetailResponse;
import org.jh.forum.common.dto.response.FAQQuestionListElement;
import org.jh.forum.server.manager.FAQManager;

import jakarta.annotation.Resource;

/**
 * FAQ服务实现类
 *
 * @author ZeroHzzzz
 */
@DubboService
public class FAQServiceImpl implements FAQService {

    @Resource
    private FAQManager faqManager;

    @Override
    public BaseListResponse<FAQQuestionListElement> getFaqQuestions(FAQQuestionListRequest request) {
        return faqManager.getFaqQuestions(request);
    }

    @Override
    public FAQDetailResponse getFaqDetail(FAQQuestionDetailRequest request) {
        return faqManager.getFaqDetail(request.getQuestionId());
    }

    @Override
    public void createFaq(FAQQuestionCreateRequest request) {
        faqManager.createFaq(request.getCategory(), request.getQuestion(), request.getAnswer());
    }

    @Override
    public void updateFaq(FAQQuestionUpdateRequest request) {
        faqManager.updateFaq(request.getQuestionId(), request.getCategory(), request.getQuestion(), request.getAnswer());
    }

    @Override
    public void deleteFaq(FAQQuestionDeleteRequest request) {
        faqManager.deleteFaq(request.getQuestionId());
    }
}