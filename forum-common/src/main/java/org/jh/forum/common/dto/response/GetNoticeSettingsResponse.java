package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 获取用户通知设置
 *
 * @author SugarMGP
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetNoticeSettingsResponse implements Serializable {
    @Schema(description = "点赞提醒开关")
    private Boolean upvoteNotice;

    @Schema(description = "评论提醒开关")
    private Boolean commentNotice;
}
