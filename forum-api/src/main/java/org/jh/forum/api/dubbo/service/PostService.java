package org.jh.forum.api.dubbo.service;


import org.jh.forum.api.dubbo.message.PostListElement;
import org.jh.forum.common.dto.request.GetPostListRequest;
import org.jh.forum.common.dto.request.PublishPostRequest;

import java.util.List;

/**
 * @author SugarMGP
 */
public interface PostService {
    void publishPost(PublishPostRequest request);

    List<PostListElement> getPostList(GetPostListRequest request);

    List<PostListElement> getMyPostList();
}