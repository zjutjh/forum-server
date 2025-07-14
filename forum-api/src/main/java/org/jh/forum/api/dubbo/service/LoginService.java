package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.constants.UserTypeEnum;

/**
 * @author SugarMGP
 */
public interface LoginService {
    UserTypeEnum login(String username, String password, Integer loginType);
}