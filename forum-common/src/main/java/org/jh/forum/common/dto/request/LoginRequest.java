package org.jh.forum.common.dto.request;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jh.forum.common.constants.DeviceTypeEnum;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 登录请求
 *
 * @author MangoGovo
 */
@Data
public class LoginRequest implements Serializable {
    @NotBlank
    @Schema(description = "用户名")
    private String username;

    @NotBlank
    @Schema(description = "密码")
    private String password;

    @Schema(description = "设备类型")
    @JsonSetter(nulls = Nulls.SKIP)
    private DeviceTypeEnum deviceType = DeviceTypeEnum.WEB;
}
