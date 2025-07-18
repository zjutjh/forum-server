package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.jh.forum.common.constants.UserTypeEnum;

/**
 * @author MangoGovo
 */
@Data
@Builder
public class LoginResponse {
    @Schema(description = "用户类型")
    private UserTypeEnum userType;
}
