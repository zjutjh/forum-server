package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lyyzzz
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UnreadCheckResponse {
    @Schema(description = "未读通知数")
    private Integer unreadNoticeCount;

    @Schema(description = "未读公告数")
    private Integer unreadAnnouncementCount;
}
