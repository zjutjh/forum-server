package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.dto.request.AdminRegisterRequest;
import org.jh.forum.common.dto.response.LoginResponse;

/**
 * 登录服务接口
 *
 * @author SugarMGP
 */
public interface LoginService {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    LoginResponse userLogin(String username, String password);

    /**
     * 管理员登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    LoginResponse adminLogin(String username, String password);


    /**
     * 注册管理员
     *
     * @param request 注册管理员请求参数
     */
    void adminRegister(AdminRegisterRequest request);
}