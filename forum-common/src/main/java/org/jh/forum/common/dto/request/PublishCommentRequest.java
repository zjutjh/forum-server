package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jh.forum.common.annotation.NoExternalLink;
import org.jh.forum.common.constants.CommentTargetTypeEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 发表评论请求
 *
 * @author qianqianzyk
 */
@Data
public class PublishCommentRequest implements Serializable {
    @Schema(description = "评论目标类型")
    @NotNull
    private CommentTargetTypeEnum targetType;

    @Schema(description = "目标ID")
    @NotNull
    private Long targetId;

    @Schema(description = "评论内容，禁止发空评论")
    @NotBlank
    @Size(max = 400)
    @NoExternalLink
    private String content;

    @Schema(description = "评论图片Url，无即传空字符串")
    @NotNull
    private String picture;
}
