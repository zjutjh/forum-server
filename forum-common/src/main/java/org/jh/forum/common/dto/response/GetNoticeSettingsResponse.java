package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author SugarMGP
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetNoticeSettingsResponse {
    @Schema(description = "点赞提醒开关")
    private Boolean upvoteNotice;

    @Schema(description = "评论提醒开关")
    private Boolean commentNotice;
}
