package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.jh.forum.common.constants.UserTypeEnum;

@Data
public class AdminRegisterRequest {
    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    String username;

    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    String password;

    @Schema(description = "用户类型")
    @NotNull
    UserTypeEnum userType;

    @Schema(description = "密钥")
    @NotBlank()
    @Length(min = 32)
    String key;
}
