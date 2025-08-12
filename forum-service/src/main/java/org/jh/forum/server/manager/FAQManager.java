package org.jh.forum.server.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.FAQCategoryEnum;
import org.jh.forum.common.dto.request.FAQQuestionListRequest;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.FAQDetailResponse;
import org.jh.forum.common.dto.response.FAQQuestionListElement;
import org.jh.forum.common.entity.FAQ;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.mapper.FAQMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FAQ业务管理器
 *
 * @author ZeroHzzzz
 */
@Service
@RequiredArgsConstructor
public class FAQManager {
    private final FAQMapper faqMapper;

    /**
     * 根据分类获取FAQ列表
     */
    public BaseListResponse<FAQQuestionListElement> getFaqQuestions(FAQQuestionListRequest request) {
        IPage<FAQ> page = new Page<>(request.getPage(), request.getPageSize());
        LambdaQueryWrapper<FAQ> wrapper = new LambdaQueryWrapper<>();

        if (request.getCategory() == null) {
            wrapper.eq(FAQ::getIsPicked, true);
        } else {
            wrapper.eq(FAQ::getCategory, request.getCategory());
        }
        wrapper.orderByDesc(FAQ::getUpdatedAt);

        faqMapper.selectPage(page, wrapper);
        List<FAQQuestionListElement> questionList = page.getRecords().stream()
                .map(faq -> FAQQuestionListElement.builder()
                        .questionId(faq.getId())
                        .question(faq.getQuestion())
                        .category(faq.getCategory())
                        .updatedAt(faq.getUpdatedAt())
                        .build())
                .toList();

        return BaseListResponse.<FAQQuestionListElement>builder()
                .list(questionList)
                .total(page.getTotal())
                .page(request.getPage())
                .pageSize(request.getPageSize())
                .build();
    }

    /**
     * 根据ID获取FAQ详情
     */
    public FAQDetailResponse getFaqDetail(Long questionId) {
        FAQ faq = faqMapper.selectById(questionId);
        if (faq == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }

        FAQDetailResponse response = FAQDetailResponse.builder()
                .category(faq.getCategory())
                .answer(faq.getAnswer())
                .viewCount(faq.getViewCount())
                .updatedAt(faq.getUpdatedAt())
                .question(faq.getQuestion())
                .build();
        faqMapper.incrementViewCount(questionId);
        return response;
    }

    /**
     * 创建FAQ
     */
    public void createFaq(FAQCategoryEnum category, String question, String answer) {
        FAQ faq = FAQ.builder()
                .category(category)
                .question(question)
                .answer(answer)
                .viewCount(0)
                .build();
        faqMapper.insert(faq);
    }

    /**
     * 更新FAQ
     */
    public void updateFaq(Long questionId, FAQCategoryEnum category, String question, String answer, Boolean isPicked) {
        FAQ faq = faqMapper.selectById(questionId);
        if (faq == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }

        faq.setCategory(category);
        faq.setQuestion(question);
        faq.setAnswer(answer);
        faq.setIsPicked(isPicked);
        faqMapper.updateById(faq);
    }

    /**
     * 删除FAQ
     */
    public void deleteFaq(Long questionId) {
        FAQ existingFaq = faqMapper.selectById(questionId);
        if (existingFaq == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        faqMapper.deleteById(questionId);
    }
}