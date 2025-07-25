package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 置顶帖子请求DTO
 *
 * @author SugarMGP
 */
@Data
@Schema(description = "置顶/取消置顶帖子请求")
public class PinPostRequest {
    @NotNull
    @Schema(description = "帖子ID")
    private Long id;

    @NotNull
    @Schema(description = "是否置顶")
    private Boolean pinned;
}