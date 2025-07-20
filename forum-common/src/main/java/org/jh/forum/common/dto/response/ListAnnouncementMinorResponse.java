package org.jh.forum.common.dto.response;

import java.util.List;

/**
 * @author SituChengxiang(SK)
 */
public record ListAnnouncementMinorResponse(List<AnnouncementMinorResponse> announcements) {
    public record AnnouncementMinorResponse(Long id, String title, Boolean sticky) {
        // 公告 id，公告标题，是否置顶，三个字段目前足够了
    }
}