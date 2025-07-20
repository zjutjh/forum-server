package org.jh.forum.common.dto.response;

import java.util.List;

/**
 * @author SituChengxiang(SK)
 */
public record StickyAnnouncementList(List<StickyAnnouncementElement> announcements) {
    public record StickyAnnouncementElement(Long id, String title, Boolean sticky) {
    }
}