package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 置顶公告请求DTO
 *
 * @author SituChengxiang
 */
@Data
public class StickyAnnouncementRequest implements Serializable {
    @NotNull
    @Min(1)
    @Schema(description = "公告ID")
    private Long id;

    @NotNull
    @Schema(description = "是否置顶")
    private Boolean sticky;
}