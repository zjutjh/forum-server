package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 置顶公告请求DTO
 * 
 * @author SituChengxiang
 */
@Data
@Schema(description = "置顶/取消置顶公告请求")
public class StickyAnnouncementRequest {

    @NotNull(message = "公告ID不能为空")
    @Min(value = 1, message = "公告ID不能小于1")
    @Schema(description = "公告ID", example = "1", required = true)
    private Long id;

    @NotNull(message = "置顶状态不能为空")
    @Schema(description = "是否置顶", example = "true", required = true, allowableValues = { "true", "false" })
    private Boolean Sticky;
}