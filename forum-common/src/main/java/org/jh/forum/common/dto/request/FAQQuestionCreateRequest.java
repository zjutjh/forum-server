package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jh.forum.common.constants.FAQCategoryEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * FAQ问题创建请求
 *
 * @author ZeroHzzzz
 */
@Data
public class FAQQuestionCreateRequest implements Serializable {
    @Schema(description = "FAQ类别")
    @NotNull
    private FAQCategoryEnum category;

    @Schema(description = "问题描述")
    @NotBlank
    @Size(max = 200)
    private String question;

    @Schema(description = "问题答案")
    @NotBlank
    @Size(max = 500)
    private String answer;
}
