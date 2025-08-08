package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.UserTypeEnum;

/**
 * @author MangoGovo
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponse {
    @Schema(description = "用户类型")
    private UserTypeEnum userType;

    @Schema(description = "统一学生信息")
    private OauthUserInfoElement userInfo;

}
