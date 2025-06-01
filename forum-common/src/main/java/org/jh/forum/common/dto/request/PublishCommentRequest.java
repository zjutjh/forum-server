package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

/**
 * @author qianqianzyk
 */
@Data
public class PublishCommentRequest {
    @Schema(description = "评论内容")
    @NotBlank
    private String comment;

    @Schema(description = "评论图片")
    private String attachmentUrl;

    @Schema(description = "@列表")
    private String[] atList;
}
