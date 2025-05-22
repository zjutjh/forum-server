package org.jh.forum.server.dubbo;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.fastjson2.JSON;
import com.google.protobuf.Any;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.util.StringUtil;
import org.jh.forum.api.dubbo.CorrectDataService;
import org.jh.forum.api.dubbo.PublishPostReq;
import org.jh.forum.api.dubbo.PublishPostResp;
import org.jh.forum.api.dubbo.ServiceResult;
import org.jh.forum.common.exceptions.ServiceException;
import org.jh.forum.server.config.service.NacosConfigAService;
import org.jh.forum.server.manger.PostManager;

import jakarta.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * Dubbo 接口的实现类，请手动对本类下所有方法进行弱依赖处理，确保异常不会影响下游服务
 *
 * @author Patrick_Star
 * @version 1.0
 */
@DubboService(version = "1.0.0")
@Slf4j
public class CorrectDataServerImpl implements CorrectDataService {

    @Resource
    private PostManager postManager;

    @Override
    @SentinelResource(value = "publishPost")
    public ServiceResult publishPost(PublishPostReq request) {
        log.info(JSON.toJSONString(NacosConfigAService.nacosConfigA));

        if (StringUtil.isBlank(request.getContext())) {
            throw new ServiceException(123, "context is null");
        }
        if (StringUtil.isBlank(request.getTitle())) {
            throw new RuntimeException("123");
        }
        PublishPostResp resp = PublishPostResp.newBuilder().setPostId("11").build();
        Any any = Any.pack(resp);
        return ServiceResult.newBuilder().setIsSuccess(true).setData(any).build();
    }

    @Override
    public CompletableFuture<ServiceResult> publishPostAsync(PublishPostReq request) {
        return null;
    }

    @Override
    public ServiceResult publishComment(PublishPostReq request) {
        log.info("publishComment");
        PublishPostResp resp = null;
        try {
            resp = PublishPostResp.newBuilder().setPostId(postManager.genPostId(request.getUid())).build();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Any any = Any.pack(resp);
        return ServiceResult.newBuilder().setIsSuccess(true).setData(any).build();
    }


    @Override
    public CompletableFuture<ServiceResult> publishCommentAsync(PublishPostReq request) {
        return null;
    }
}
