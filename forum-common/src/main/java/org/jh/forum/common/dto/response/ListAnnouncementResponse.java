package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * 查询公告列表响应DTO
 */
@Data
@Schema(description = "查询公告列表响应")
public class ListAnnouncementResponse {

    @Schema(description = "总记录数", example = "100")
    private Long total;

    @Schema(description = "当前页码", example = "1")
    private Integer page;

    @Schema(description = "每页大小", example = "8")
    private Integer size;

    @Schema(description = "总页数", example = "8")
    private Integer pages;

    @Schema(description = "公告列表")
    private List<AnnouncementItemResponse> list;

    @Data
    @Schema(description = "公告列表项")
    public static class AnnouncementItemResponse {

        @Schema(description = "公告ID", example = "1")
        private Integer id;

        @Schema(description = "公告标题", example = "重要系统维护通知")
        private String title;

        @Schema(description = "公告类型", example = "系统公告")
        private String type;

        @Schema(description = "状态：0草稿、1已发布、2待发布", example = "1")
        private Integer status;

        @Schema(description = "状态名称", example = "已发布")
        private String statusName;

        @Schema(description = "创建用户ID", example = "123")
        private Integer creatorId;

        @Schema(description = "创建时间", example = "2025-05-30 09:00:00")
        private String createdAt;

        @Schema(description = "更新时间", example = "2025-05-30 09:00:00")
        private String updatedAt;
    }
}
