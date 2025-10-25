package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * FAQ问题详情请求参数
 *
 * @author ZeroHzzzz
 */
@Data
public class FAQQuestionDetailRequest implements Serializable {
    @Schema(description = "FAQ问题ID")
    @NotNull
    private Long questionId;
}