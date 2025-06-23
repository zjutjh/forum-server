package org.jh.forum.start.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpInterface;
import lombok.AllArgsConstructor;
import org.jh.forum.server.manger.UserManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author MangoGovo
 */
@Configuration
@AllArgsConstructor
public class SaTokenConfig implements StpInterface, WebMvcConfigurer {
    final private UserManager userManager;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return List.of();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        try {
            Long userId = Long.valueOf((String) loginId);
            return userManager.getRoleList(userId);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
    }
}