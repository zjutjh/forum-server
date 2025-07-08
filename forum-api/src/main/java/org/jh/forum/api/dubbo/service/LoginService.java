package org.jh.forum.api.dubbo.service;

/**
 * @author SugarMGP
 */
public interface LoginService {
    String login(String username, String password, Integer loginType);
}