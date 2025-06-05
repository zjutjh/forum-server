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

    @Schema(description = "公告类型", example = "0")
    private Integer type;

    @Schema(description = "状态", example = "0")
    private Integer status;

    @Schema(description = "创建用户的用户名", example = "admin")
    private String creator;

    @Schema(description = "更新用户的用户名", example = "suadmin")
    private String updator;

    @Schema(description = "创建时间", example = "2025-06-07T09:00:00.000Z")
    private String createdAt;

    @Schema(description = "更新时间", example = "2025-06-07T09:00:00.000Z")
    private String updatedAt;

    @Schema(description = "定时发布时间", example = "2025-06-07T09:00:00.000Z")
    private String scheduledAt;

    @Schema(description = "附加属性", example = "{\"sticky\": true}")
    private Object attribute;

    @Schema(description = "是否置顶", example = "false")
    private Boolean sticky;
}