package org.jh.forum.server.dubbo;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.CorrectDataService;
import org.jh.forum.api.dubbo.PublishPostReq;
import org.jh.forum.api.dubbo.PublishPostResp;
import org.jh.forum.api.dubbo.ServiceResult;
import org.jh.forum.server.demos.nacosconfig.NacosConfigDemoConfiguration;

import java.util.concurrent.CompletableFuture;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@DubboService(version = "1.0.0")
@Slf4j
public class CorrectDataServerImpl implements CorrectDataService {

    @Override
    @SentinelResource(value = "publishPost")
    public PublishPostResp publishPost(PublishPostReq request) {
        log.info(JSON.toJSONString(NacosConfigDemoConfiguration.user));
        return PublishPostResp.newBuilder().setBaseResult(ServiceResult.newBuilder().setIsSuccess(true).build()).build();
    }

    @Override
    public CompletableFuture<PublishPostResp> publishPostAsync(PublishPostReq request) {
        return null;
    }

    @Override
    public PublishPostResp publishComment(PublishPostReq request) {
        log.info("publishComment");
        return PublishPostResp.newBuilder().setBaseResult(ServiceResult.newBuilder().setIsSuccess(true).build()).build();
    }

    @Override
    public CompletableFuture<PublishPostResp> publishCommentAsync(PublishPostReq request) {
        return null;
    }
}
