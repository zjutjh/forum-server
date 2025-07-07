package org.jh.forum.api.dubbo.service;


import org.jh.forum.api.dubbo.message.GetPostListReq;
import org.jh.forum.api.dubbo.message.PostListElement;
import org.jh.forum.api.dubbo.message.PublishPostReq;

import java.util.List;

/**
 * @author SugarMGP
 */
public interface PostService {
    void publishPost(PublishPostReq request);

    List<PostListElement> getPostList(GetPostListReq request);

    List<PostListElement> getMyPostList();
}