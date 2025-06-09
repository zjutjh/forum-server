package org.jh.forum.server.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.PostService;
import org.jh.forum.api.dubbo.PublishPostReq;
import org.jh.forum.api.dubbo.ServiceResult;
import org.jh.forum.server.manger.PostManager;

import jakarta.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * @author SugarMGP
 */
@DubboService(version = "1.0.0")
@Slf4j
public class PostServiceImpl implements PostService {
    @Resource
    private PostManager postManager;

    @Override
    public ServiceResult publishPost(PublishPostReq request) {
        postManager.publishPost(request);
        return ServiceResult.newBuilder().setIsSuccess(true).build();
    }

    @Override
    public CompletableFuture<ServiceResult> publishPostAsync(PublishPostReq request) {
        return CompletableFuture.supplyAsync(() -> publishPost(request));
    }
}
