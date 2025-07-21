package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 置顶公告请求DTO
 *
 * @author SituChengxiang
 */
@Data
@Schema(description = "置顶/取消置顶公告请求")
public class StickyAnnouncementRequest {
    @NotNull
    @Min(1)
    @Schema(description = "公告ID")
    private Long id;

    @NotNull
    @Schema(description = "是否置顶")
    private Boolean sticky;
}