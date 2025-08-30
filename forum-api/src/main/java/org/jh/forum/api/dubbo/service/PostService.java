package org.jh.forum.api.dubbo.service;


import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;

/**
 * 帖子服务接口
 *
 * @author SugarMGP
 */
public interface PostService {

    /**
     * 发表帖子
     *
     * @param request 发帖请求参数
     */
    void publishPost(PublishPostRequest request);

    /**
     * 获取帖子列表
     *
     * @param request 帖子列表请求参数
     * @return 帖子列表分页结果
     */
    BaseListResponse<GetPostListElement> getPostList(GetPostListRequest request);

    /**
     * 获取个人帖子列表
     *
     * @param request 个人帖子列表请求参数
     * @return 个人帖子列表分页结果
     */
    BaseListResponse<GetPersonalPostListElement> getPersonalPostList(GetPersonalPostRequest request);

    /**
     * 获取帖子详情
     *
     * @param id 帖子ID
     * @return 帖子详情
     */
    GetPostInfoResponse getPostInfo(Long id);

    /**
     * 删除帖子
     *
     * @param id 帖子ID
     */
    void deletePost(Long id);

    /**
     * 获取管理员帖子列表
     *
     * @param request 管理员帖子列表请求参数
     * @return 管理员帖子列表分页结果
     */
    BaseListResponse<GetAdminPostListElement> getAdminPostList(GetAdminPostListRequest request);

    /**
     * 获取管理员帖子详情
     *
     * @param id 帖子ID
     * @return 管理员帖子详情
     */
    GetAdminPostInfoResponse getAdminPostInfo(Long id);

    /**
     * 获取五大热门帖子列表
     *
     * @return 五大热门帖子列表
     */
    TopFivePostList getTopFivePosts();

    /**
     * 恢复帖子
     *
     * @param id 帖子ID
     */
    void restorePost(Long id);

    /**
     * 置顶帖子
     *
     * @param request 置顶帖子请求参数
     */
    void pinPost(PinPostRequest request);

    /**
     * 个人主页置顶帖子
     *
     * @param request 请求参数
     */
    void topPost(TopPostRequest request);

    /**
     * 点赞帖子
     *
     * @param id 帖子ID
     * @return 点赞结果
     */
    UpvotePostResponse upvotePost(Long id);
}