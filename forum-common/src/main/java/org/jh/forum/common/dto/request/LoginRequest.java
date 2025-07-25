package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jh.forum.common.constants.UserTypeEnum;

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
    @Schema(description = "用户类型")
    private UserTypeEnum loginType;
}
