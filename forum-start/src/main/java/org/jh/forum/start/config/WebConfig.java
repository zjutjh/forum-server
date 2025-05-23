package org.jh.forum.start.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.util.ClassUtils;

/**
 * @author MangoGovo
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * 请求路径添加统一前缀
     */
    @Value("${api.prefix}")
    String prefix;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(prefix, c -> {
            String packageName = ClassUtils.getPackageName(c);
            return packageName.startsWith("org.jh.forum.start.controller");
        });
    }
}