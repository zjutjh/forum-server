package org.jh.forum.common.dto.request;

import lombok.Data;
import org.jh.forum.common.constants.UserTypeEnum;

@Data
public class AdminRegisterRequest {
    String username;
    String password;
    String key;
    UserTypeEnum userType;
}
