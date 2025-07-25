package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.PostService;
import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.common.entity.Post;
import org.jh.forum.server.manager.PostManager;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @author SugarMGP
 */
@DubboService
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
        if ("new".equals(request.getSortType())) {
            postList = postManager.getPostList(request.getCategory(), request.getPage(), request.getPageSize());
        } else {
            postList = postManager.getHotPostList(request.getCategory(), request.getPage(), request.getPageSize());
        }
        return postList;
    }

    @Override
    public BaseListResponse<GetPersonalPostListElement> getPersonalPostList(GetPersonalPostRequest request) {
        return postManager.getPersonalPostList(request);
    }

    @Override
    public GetPostInfoResponse getPostInfo(Long id) {
        return postManager.getPostInfo(id, StpUtil.getLoginIdAsLong());
    }

    @Override
    public void deletePost(Long id) {
        boolean isAdmin = StpUtil.hasRole("admin") || StpUtil.hasRole("super_admin");
        postManager.deletePost(id, isAdmin);
    }

    @Override
    public BaseListResponse<GetAdminPostListElement> getAdminPostList(GetAdminPostListRequest request) {
        return postManager.getAdminPostList(request);
    }

    @Override
    public GetAdminPostInfoResponse getAdminPostInfo(Long id) {
        return postManager.getAdminPostInfo(id);
    }

    @Override
    public TopFivePostList getTopFivePosts() {
        List<Post> list = postManager.getTopFivePosts();

        List<TopFivePostList.TopFivePostListElement> topPosts = list.stream()
                .map(post -> new TopFivePostList.TopFivePostListElement(
                        post.getId(),
                        post.getTitle()))
                .toList();

        return new TopFivePostList(topPosts);
    }

    @Override
    public void restorePost(Long id) {
        postManager.restorePost(id);
    }

    @Override
    public void pinPost(PinPostRequest request) {
        postManager.pinPost(request.getId(), request.getPinned());
    }

    @Override
    public void topPost(TopPostRequest request) {
        postManager.topPost(request.getId(), request.getTopped());
    }
}
