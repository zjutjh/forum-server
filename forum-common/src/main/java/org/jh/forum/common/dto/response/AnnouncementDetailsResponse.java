package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 公告详情响应DTO
 * @author SituChengxiang
 */
@Data
@Schema(description = "公告详情响应")
public class AnnouncementDetailsResponse {

    @Schema(description = "公告ID", example = "1")
    private Integer id;

    @Schema(description = "公告标题", example = "重要系统维护通知")
    private String title;

    @Schema(description = "公告内容", example = "系统将于今晚进行维护升级...")
    private String content;

    @Schema(description = "公告类型", example = "系统公告")
    private String type;

    @Schema(description = "状态：0草稿、1已发布、2待发布", example = "0")
    private Integer status;

    @Schema(description = "状态名称", example = "草稿")
    private String statusName;

    @Schema(description = "创建用户ID", example = "123")
    private Integer creatorId;

    @Schema(description = "更新用户ID", example = "123")
    private Integer updatorId;

    @Schema(description = "创建时间", example = "1955-09-06T13:10:21.927Z")
    private String createdAt;

    @Schema(description = "更新时间", example = "1955-09-06T13:10:21.927Z")
    private String updatedAt;

    @Schema(description = "定时发布时间", example = "1955-09-06T13:10:21.927Z")
    private String scheduledAt;

    @Schema(description = "是否删除", example = "false")
    private Boolean deleted;

    @Schema(description = "附加属性", example = "{\"sticky\": true}")
    private String attribute;
}
