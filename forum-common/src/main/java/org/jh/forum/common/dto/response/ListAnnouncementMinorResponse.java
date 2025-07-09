package org.jh.forum.common.dto.response;

import java.util.List;

public record ListAnnouncementMinorResponse(List<AnnouncementMinorResponse> announcements) {
    public record AnnouncementMinorResponse(Long id, String title, Boolean sticky) {
    }
}