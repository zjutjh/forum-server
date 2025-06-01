package org.jh.forum.server.config;

import lombok.Data;

/**
 * 对象存储配置
 *
 * @author SugarMGP
 */
@Data
public class ObjectStorageConfig {
    private String baseUrl;
    private String apiKey;
    private String bucketName;
}