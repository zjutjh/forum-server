package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.message.PostListElement;
import org.jh.forum.api.dubbo.service.PostService;
import org.jh.forum.common.dto.request.GetPostListRequest;
import org.jh.forum.common.dto.request.PublishPostRequest;
import org.jh.forum.server.manger.PostManager;

import jakarta.annotation.Resource;
import java.util.List;

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
        postManager.publishPost(request.getTitle(), request.getContent(), request.getCategory(), request.getTopics(), request.getAttachmentIds());
    }

    @Override
    public List<PostListElement> getPostList(GetPostListRequest request) {
        List<PostListElement> postList;
        if (request.getSortType() == 1) {
            postList = postManager.getPostList(request.getCategory());
        } else {
            postList = postManager.getHotPostList(request.getCategory());
        }
        return postList;
    }

    @Override
    public List<PostListElement> getMyPostList() {
        return postManager.getMyPostList(StpUtil.getLoginIdAsLong());
    }
}
