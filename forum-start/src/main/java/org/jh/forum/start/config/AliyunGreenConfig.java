package org.jh.forum.start.config;

import com.aliyun.green20220302.Client;
import com.aliyun.teaopenapi.models.Config;
import lombok.Data;
import org.jh.forum.server.client.AliyunGreenClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author SugarMGP
 */
@Configuration
public class AliyunGreenConfig {
    @Bean
    @ConfigurationProperties(prefix = "aliyun.green")
    public GreenProperties greenProperties() {
        return new GreenProperties();
    }

    @Bean
    public AliyunGreenClient aliyunGreenClient(GreenProperties greenProperties) throws Exception {
        Config config = new Config()
                .setAccessKeyId(greenProperties.getAccessKeyId())
                .setAccessKeySecret(greenProperties.getAccessKeySecret())
                .setRegionId(greenProperties.getRegionId())
                .setEndpoint(greenProperties.getEndpoint())
                .setReadTimeout(greenProperties.getReadTimeout())
                .setConnectTimeout(greenProperties.getConnectTimeout());
        return new AliyunGreenClient(greenProperties.enabled, new Client(config));
    }

    @Data
    public static class GreenProperties {
        private boolean enabled = true;
        private String accessKeyId;
        private String accessKeySecret;
        private String regionId;
        private String endpoint;
        private Integer readTimeout = 6000;
        private Integer connectTimeout = 3000;
    }
}
