package org.jh.forum.common.dto.response;

import org.jh.forum.common.constants.AnnouncementTypeEnum;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查询公告列表响应最小DTO（用户版本）
 * 
 * @author SituChengiang
 */
@Data
@Schema(description = "查询公告列表响应")
public class ListAnnouncementTinyResponse {
    
    @Schema(description = "页码")
    private Integer page;

    @JsonProperty("page_size")
    @Schema(description = "每页数量")
    private Integer pageSize;

    @Schema(description = "总数")
    private Long total;

    @Schema(description = "列表")
    private List<AnnouncementItemResponse> list;

    @Data
    @Schema(description = "公告列表项")
    public static class AnnouncementItemResponse {

        @Schema(description = "公告ID", example = "1")
        private Long id;

        @Schema(description = "公告标题", example = "重要系统维护通知")
        private String title;

        @Schema(description = "类型筛选", example = "systematic")
        private AnnouncementTypeEnum type ;

        @Schema(description = "创建用户昵称", example = "admin")
        private String creator;

        @Schema(description = "更新用户昵称", example = "suadmin")
        private String updator;

        @JsonProperty("updated_at")
        @Schema(description = "更新时间", example = "2025-06-07T09:00:00.000Z")
        private String updatedAt;

        @JsonProperty("published_at")
        @Schema(description = "实际发布时间", example = "2025-06-07T09:00:00.000Z")
        private String publishedAt;

        @Schema(description = "是否置顶", example = "false")
        private boolean sticky;
    }
}
