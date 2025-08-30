package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 个人主页置顶帖子请求DTO
 *
 * @author SugarMGP
 */
@Data
public class TopPostRequest {
    @NotNull
    @Schema(description = "帖子ID")
    private Long id;

    @NotNull
    @Schema(description = "是否置顶")
    private Boolean topped;
}