package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.FAQCategoryEnum;

import java.time.LocalDateTime;

/**
 * FAQ详情响应对象
 *
 * @author ZeroHzzzz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "FAQ详情响应数据")
public class FAQDetailResponse {
    @Schema(description = "分类名称")
    private FAQCategoryEnum category;

    @Schema(description = "问题描述")
    private String question;

    @Schema(description = "问题答案")
    private String answer;

    @Schema(description = "浏览量")
    private Integer viewCount;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
