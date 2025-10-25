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
     *
     * @param request 获取问题列表请求
     * @return 分页结果
     */
    BaseListResponse<FAQQuestionListElement> getFaqQuestions(FAQQuestionListRequest request);

    /**
     * 获取FAQ问题详情
     *
     * @param request 获取详情请求
     * @return FAQ详情
     */
    FAQDetailResponse getFaqDetail(FAQQuestionDetailRequest request);

    /**
     * 创建FAQ问题
     *
     * @param request 创建问题请求
     */
    void createFaq(FAQQuestionCreateRequest request);

    /**
     * 更新FAQ问题
     *
     * @param request 更新问题请求
     */
    void updateFaq(FAQQuestionUpdateRequest request);

    /**
     * 删除FAQ问题
     *
     * @param request 删除问题请求
     */
    void deleteFaq(FAQQuestionDeleteRequest request);
}