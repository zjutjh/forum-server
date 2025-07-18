package org.jh.forum.api.dubbo.service;


import org.jh.forum.common.dto.request.BaseListRequest;
import org.jh.forum.common.dto.request.GetAdminPostListRequest;
import org.jh.forum.common.dto.request.GetPostListRequest;
import org.jh.forum.common.dto.request.PublishPostRequest;
import org.jh.forum.common.dto.response.*;

/**
 * @author SugarMGP
 */
public interface PostService {
    void publishPost(PublishPostRequest request);

    BaseListResponse<GetPostListElement> getPostList(GetPostListRequest request);

    BaseListResponse<GetMyPostListElement> getMyPostList(BaseListRequest request);

    GetPostInfoResponse getPostInfo(Long id);

    void deletePost(Long id);

    BaseListResponse<GetAdminPostListElement> getAdminPostList(GetAdminPostListRequest request);

    GetAdminPostInfoResponse getAdminPostInfo(Long id);
}