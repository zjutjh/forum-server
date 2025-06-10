package org.jh.forum.server.dubbo;

import com.google.protobuf.Any;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.GetTopicIdReq;
import org.jh.forum.api.dubbo.GetTopicIdResp;
import org.jh.forum.api.dubbo.ServiceResult;
import org.jh.forum.api.dubbo.TopicService;
import org.jh.forum.server.manger.TopicManager;

import jakarta.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * @author SugarMGP
 */
@DubboService(version = "1.0.0")
@Slf4j
public class TopicServiceImpl implements TopicService {
    @Resource
    private TopicManager topicManager;

    @Override
    public ServiceResult getTopicId(GetTopicIdReq request) {
        GetTopicIdResp resp = GetTopicIdResp.newBuilder()
                .setId(topicManager.getTopicId(request.getName()))
                .build();
        return ServiceResult.newBuilder().setIsSuccess(true).setData(Any.pack(resp)).build();
    }

    @Override
    public CompletableFuture<ServiceResult> getTopicIdAsync(GetTopicIdReq request) {
        return CompletableFuture.supplyAsync(() -> getTopicId(request));
    }
}
