package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.FAQDetailResponse;
import org.jh.forum.common.dto.response.FAQQuestionListElement;

/**
 * FAQ服务接口
 *
 * @author ZeroHzzzz
 */
public interface FAQService {
    /**
     * 根据分类获取FAQ问题列表
     */
    BaseListResponse<FAQQuestionListElement> getFaqQuestions(FAQQuestionListRequest request);

    /**
     * 获取FAQ问题详情
     */
    FAQDetailResponse getFaqDetail(FAQQuestionDetailRequest request);

    /**
     * 创建FAQ问题
     */
    void createFaq(FAQQuestionCreateRequest request);

    /**
     * 更新FAQ问题
     */
    void updateFaq(FAQQuestionUpdateRequest request);

    /**
     * 删除FAQ问题
     */
    void deleteFaq(FAQQuestionDeleteRequest request);
}