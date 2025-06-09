package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import com.google.protobuf.Any;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.*;
import org.jh.forum.server.manger.PostManager;

import jakarta.annotation.Resource;
import java.util.List;
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
    public ServiceResult getPostList(GetPostListReq request) {
        List<PostListElement> postList;
        if (request.getSortType() == 1) {
            postList = postManager.getPostList(request.getCategoryId());
        } else {
            postList = postManager.getHotPostList(request.getCategoryId());
        }
        GetPostListResp resp = GetPostListResp.newBuilder().addAllPosts(postList).build();
        return ServiceResult.newBuilder().setIsSuccess(true).setData(Any.pack(resp)).build();
    }

    @Override
    public ServiceResult getMyPostList(GetMyPostListReq request) {
        List<PostListElement> postList = postManager.getMyPostList(StpUtil.getLoginIdAsLong());
        GetPostListResp resp = GetPostListResp.newBuilder().addAllPosts(postList).build();
        return ServiceResult.newBuilder().setIsSuccess(true).setData(Any.pack(resp)).build();
    }

    @Override
    public CompletableFuture<ServiceResult> publishPostAsync(PublishPostReq request) {
        return CompletableFuture.supplyAsync(() -> publishPost(request));
    }

    @Override
    public CompletableFuture<ServiceResult> getPostListAsync(GetPostListReq request) {
        return CompletableFuture.supplyAsync(() -> getPostList(request));
    }

    @Override
    public CompletableFuture<ServiceResult> getMyPostListAsync(GetMyPostListReq request) {
        return CompletableFuture.supplyAsync(() -> getMyPostList(request));
    }
}
