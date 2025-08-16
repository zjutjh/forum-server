package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.CommentTargetTypeEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * @author qianqianzyk
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PublishCommentRequest {
    @Schema(description = "评论目标类型")
    @NotNull
    private CommentTargetTypeEnum targetType;

    @Schema(description = "目标ID")
    @NotNull
    private Long targetId;

    @Schema(description = "评论内容，禁止发空评论")
    @NotBlank
    @Size(max = 500)
    private String content;

    @Schema(description = "评论图片Url，无即传空字符串")
    @NotNull
    private String picture;
}
