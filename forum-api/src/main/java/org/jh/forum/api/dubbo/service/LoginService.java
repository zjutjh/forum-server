package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.constants.UserTypeEnum;
import org.jh.forum.common.dto.response.LoginResponse;

/**
 * @author SugarMGP
 */
public interface LoginService {
    LoginResponse userLogin(String username, String password);
    LoginResponse adminLogin(String username, String password);

}