package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author SugarMGP
 */
@Data
public class MuteUserRequest {
    @Schema(description = "用户ID")
    @NotNull
    private Long id;

    @Schema(description = "禁言时长（小时，为0则解除禁言）")
    @NotNull
    private Integer hours;
}
