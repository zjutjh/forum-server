package org.jh.forum.start;

import java.util.TimeZone;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.naming.NamingService;

@SpringBootApplication
@EnableAspectJAutoProxy
@DubboComponentScan(basePackages = "org.jh.forum.server.dubbo")
@MapperScan(basePackages = "org.jh.forum.common.entity.mapper")
@ComponentScan(basePackages = { "org.jh.forum.server", "org.jh.forum.start" })
public class ForumStartApplication {

    @NacosInjected
    private NamingService namingService;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private Integer serverPort;

    public static void main(String[] args) {
        // 设置全局默认时区为UTC+8
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(ForumStartApplication.class, args);
    }
}
