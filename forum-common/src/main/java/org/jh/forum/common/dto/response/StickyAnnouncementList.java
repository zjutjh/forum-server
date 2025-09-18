package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 置顶公告列表（首页）
 *
 * @author SituChengxiang(SK)
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StickyAnnouncementList implements Serializable {

    @Schema(description = "首页置顶公告列表")
    private List<StickyAnnouncementElement> announcements;

    @AllArgsConstructor
    @Data
    public static class StickyAnnouncementElement implements Serializable {
        @Schema(description = "公告ID")
        private Long id;

        @Schema(description = "公告标题")
        private String title;

        @Schema(description = "是否置顶")
        private Boolean sticky;
    }
}