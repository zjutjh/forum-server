package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;

/**
 * 评论服务接口
 *
 * @author qianqianzyk
 */
public interface CommentService {

    /**
     * 发表评论
     *
     * @param request 发布评论请求
     */
    void publishComment(PublishCommentRequest request);

    /**
     * 点赞评论
     *
     * @param id 评论ID
     * @return 当前点赞状态
     */
    UpvoteCommentResponse upvoteComment(Long id);

    /**
     * 置顶评论
     *
     * @param id 评论ID
     * @return 当前置顶状态
     */
    PinCommentResponse pinComment(Long id);

    /**
     * 删除评论
     *
     * @param id 评论ID
     */
    void removeComment(Long id);

    /**
     * 用户获取评论列表
     *
     * @param request 获取评论列表请求
     * @return 分页结果
     */
    BaseListResponse<CommentElement> getCommentList(GetCommentListRequest request);

    /**
     * 用户获取回复列表（评论详情页）
     *
     * @param request 获取回复列表请求
     * @return 父评论信息与分页结果
     */
    GetCommentReplyListResponse getReplyList(GetReplyListRequest request);

    /**
     * 用户获取个人评论列表
     *
     * @param request 获取列表请求
     * @return 分页结果
     */
    BaseListResponse<PersonalCommentListElement> getPersonalCommentList(BaseListRequest request);

    /**
     * 管理员获取评论列表
     *
     * @param request 管理员获取评论列表请求
     * @return 分页结果
     */
    BaseListResponse<CommentElement> getAdminCommentList(GetCommentListAdminRequest request);

    /**
     * 管理员获取回复列表
     *
     * @param request 管理员获取回复列表请求
     * @return 分页结果
     */
    BaseListResponse<ReplyElement> getAdminReplyList(GetReplyListAdminRequest request);

    /**
     * 管理员更改评论状态
     *
     * @param request 管理员更改评论状态请求
     */
    void adminChangeCommentStatus(ChangeCommentStatusRequest request);
}
