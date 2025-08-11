package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author SugarMGP
 */
@Data
public class UpdateNoticeSettingsRequest {
    @NotNull
    @Schema(description = "点赞消息开关")
    private Boolean upvoteNotice;

    @NotNull
    @Schema(description = "评论消息开关")
    private Boolean commentNotice;
}