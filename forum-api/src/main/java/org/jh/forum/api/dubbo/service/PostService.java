package org.jh.forum.api.dubbo.service;


import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;

/**
 * @author SugarMGP
 */
public interface PostService {
    void publishPost(PublishPostRequest request);

    BaseListResponse<GetPostListElement> getPostList(GetPostListRequest request);

    BaseListResponse<GetPersonalPostListElement> getPersonalPostList(GetPersonalPostRequest request);

    GetPostInfoResponse getPostInfo(Long id);

    void deletePost(Long id);

    BaseListResponse<GetAdminPostListElement> getAdminPostList(GetAdminPostListRequest request);

    GetAdminPostInfoResponse getAdminPostInfo(Long id);

    TopFivePostList getTopFivePosts();

    void restorePost(Long id);

    void pinPost(PinPostRequest request);

    void topPost(TopPostRequest request);
}