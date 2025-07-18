package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author MangoGovo
 */
@Data
public class LoginRequest {
    @NotBlank
    @Schema(description = "用户名")
    private String username;

    @NotBlank
    @Schema(description = "密码")
    private String password;

    @NotNull
    @Schema(description = "用户类型 (1,学生) (2,管理员)")
    @Max(value = 2)
    @Min(value = 1)
    private Integer loginType;
}
