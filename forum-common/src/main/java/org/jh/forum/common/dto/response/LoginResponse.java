package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * @author MangoGovo
 */
@Data
@Builder
public class LoginResponse {
    @Schema(description = "用户类型(Student,Admin,SuperAdmin)")
    private String userType;
}
