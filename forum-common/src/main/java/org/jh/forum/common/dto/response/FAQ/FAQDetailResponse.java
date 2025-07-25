package org.jh.forum.common.dto.response.FAQ;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * FAQ详情响应对象
 *
 * @author ZeroHzzzz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "FAQ详情响应数据")
public class FAQDetailResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "问题ID", example = "1", required = true)
    private Long questionId;
    
    @Schema(description = "分类名称", example = "账号问题", required = true)
    private String category;
    
    @Schema(description = "问题描述", example = "如何重置密码？", required = true)
    private String question;
    
    @Schema(description = "问题答案", example = "1. 点击登录页面的\"忘记密码\"\n2. 输入您的注册邮箱\n3. 按照邮件中的指引重置密码", required = true)
    private String answer;
    
    @Schema(description = "浏览量", example = "2", required = true)
    private Integer viewCount;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private java.time.LocalDateTime createdAt;
    
    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    private java.time.LocalDateTime updatedAt;
}
