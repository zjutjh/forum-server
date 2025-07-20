package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.PostService;
import org.jh.forum.common.dto.request.BaseListRequest;
import org.jh.forum.common.dto.request.GetAdminPostListRequest;
import org.jh.forum.common.dto.request.GetPostListRequest;
import org.jh.forum.common.dto.request.PublishPostRequest;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.server.manager.PostManager;

import jakarta.annotation.Resource;

/**
 * @author SugarMGP
 */
@DubboService(version = "1.0.0")
@Slf4j
public class PostServiceImpl implements PostService {
    @Resource
    private PostManager postManager;

    @Override
    public void publishPost(PublishPostRequest request) {
        postManager.publishPost(request);
    }

    @Override
    public BaseListResponse<GetPostListElement> getPostList(GetPostListRequest request) {
        BaseListResponse<GetPostListElement> postList;
        if (request.getSortType() == 1) {
            postList = postManager.getPostList(request.getCategory(), request.getPage(), request.getPageSize());
        } else {
            postList = postManager.getHotPostList(request.getCategory(), request.getPage(), request.getPageSize());
        }
        return postList;
    }

    @Override
    public BaseListResponse<GetMyPostListElement> getMyPostList(BaseListRequest request) {
        return postManager.getMyPostList(StpUtil.getLoginIdAsLong(), request.getPage(), request.getPageSize());
    }

    @Override
    public GetPostInfoResponse getPostInfo(Long id) {
        return postManager.getPostInfo(id, StpUtil.getLoginIdAsLong());
    }

    @Override
    public void deletePost(Long id) {
        postManager.deletePost(id);
    }

    @Override
    public BaseListResponse<GetAdminPostListElement> getAdminPostList(GetAdminPostListRequest request) {
        return postManager.getAdminPostList(request);
    }

    @Override
    public GetAdminPostInfoResponse getAdminPostInfo(Long id) {
        return postManager.getAdminPostInfo(id);
    }
}
