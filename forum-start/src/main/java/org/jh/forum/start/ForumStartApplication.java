package org.jh.forum.start;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.naming.NamingService;

@SpringBootApplication
@EnableAspectJAutoProxy
@DubboComponentScan(basePackages = "org.jh.forum.server.dubbo")
@ComponentScan(basePackages = {"org.jh.forum.server",
        "org.jh.forum.start"})
@EnableJpaRepositories(basePackages = "org.jh.forum.server.repository")
@EntityScan(basePackages = "org.jh.forum.server.entity")
public class ForumStartApplication {

    @NacosInjected
    private NamingService namingService;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private Integer serverPort;

    public static void main(String[] args) {
        SpringApplication.run(ForumStartApplication.class, args);
    }

}
