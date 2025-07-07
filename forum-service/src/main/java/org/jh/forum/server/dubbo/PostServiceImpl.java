package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.message.GetPostListReq;
import org.jh.forum.api.dubbo.message.PostListElement;
import org.jh.forum.api.dubbo.message.PublishPostReq;
import org.jh.forum.api.dubbo.service.PostService;
import org.jh.forum.common.constants.CategoryEnum;
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
    public void publishPost(PublishPostReq request) {
        CategoryEnum category = EnumUtil.getBy(CategoryEnum::getValue, request.getCategory());
        postManager.publishPost(request.getTitle(), request.getContent(), category, request.getTopics(), request.getAttachmentIds());
    }

    @Override
    public List<PostListElement> getPostList(GetPostListReq request) {
        List<PostListElement> postList;
        CategoryEnum category = EnumUtil.getBy(CategoryEnum::getValue, request.getCategory());
        if (request.getSortType() == 1) {
            postList = postManager.getPostList(category);
        } else {
            postList = postManager.getHotPostList(category);
        }
        return postList;
    }

    @Override
    public List<PostListElement> getMyPostList() {
        return postManager.getMyPostList(StpUtil.getLoginIdAsLong());
    }
}
