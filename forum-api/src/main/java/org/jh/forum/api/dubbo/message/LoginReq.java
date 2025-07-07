package org.jh.forum.api.dubbo.message;

import lombok.Builder;
import lombok.Data;

/**
 * @author SugarMGP
 */
@Data
@Builder
public class LoginReq {
    private String username;
    private String password;
    private Integer loginType;
}