package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.constants.UserTypeEnum;
import org.jh.forum.common.dto.response.LoginResponse;

/**
 * @author SugarMGP
 */
public interface LoginService {
    LoginResponse login(String username, String password, UserTypeEnum loginType);
}