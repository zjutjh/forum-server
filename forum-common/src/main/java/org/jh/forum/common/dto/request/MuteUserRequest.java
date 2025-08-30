package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 管理员禁言用户请求
 *
 * @author SugarMGP
 */
@Data
public class MuteUserRequest {
    @Schema(description = "用户ID")
    @NotNull
    private Long id;

    @Schema(description = "禁言时长（小时，为0则解除禁言）")
    @NotNull
    @Max(value = 24 * 90)
    @Min(value = 0)
    private Integer hours;
}
