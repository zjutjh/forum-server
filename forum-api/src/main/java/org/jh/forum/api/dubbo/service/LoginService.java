package org.jh.forum.api.dubbo.service;

import org.jh.forum.api.dubbo.message.LoginReq;

/**
 * @author SugarMGP
 */
public interface LoginService {
    String login(LoginReq request);
}