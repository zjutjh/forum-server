package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改通知设置请求
 *
 * @author SugarMGP
 */
@Data
public class UpdateNoticeSettingsRequest implements Serializable {
    @NotNull
    @Schema(description = "点赞消息开关")
    private Boolean upvoteNotice;

    @NotNull
    @Schema(description = "评论消息开关")
    private Boolean commentNotice;
}