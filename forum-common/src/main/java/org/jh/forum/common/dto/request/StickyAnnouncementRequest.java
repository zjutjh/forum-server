package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建公告请求DTO
 * @author SituChengxiang
 */
@Data
@Schema(description = "置顶/取消置顶公告请求")
public class StickyAnnouncementRequest {

    @NotNull(message = "公告ID不能为空")
    @Schema(description = "公告ID", example = "1")
    private Integer id;

    @NotNull(message = "置顶状态不能为空")
    @Schema(description = "是否置顶", example = "true")
    private Boolean isSticky;
}