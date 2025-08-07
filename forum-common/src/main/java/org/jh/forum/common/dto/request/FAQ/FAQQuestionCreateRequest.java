package org.jh.forum.common.dto.request.FAQ;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.validation.ValidFAQCategory;

/**
 * FAQ问题创建请求
 *
 * @author ZeroHzzzz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "FAQ问题创建请求参数")
public class FAQQuestionCreateRequest{
    
    @Schema(description = "FAQ类别", required = true, example = "猜你想问")
    @NotNull(message = "分类不能为空")
    @ValidFAQCategory(message = "无效的FAQ分类，请选择：账号问题、学院问题、帖子问题、猜你想问")
    private String category;
    
    @Schema(description = "问题描述", required = true, example = "如何重置密码？")
    @NotBlank(message = "问题描述不能为空")
    @Size(max = 200, message = "问题描述最多200字符")
    private String question;
    
    @Schema(description = "问题答案", required = true, example = "1. 点击登录页面的\"忘记密码\"\n2. 输入您的注册邮箱\n3. 按照邮件中的指引重置密码")
    @NotBlank(message = "问题答案不能为空")
    @Size(max = 500, message = "问题答案最多500字符")
    private String answer;
}
