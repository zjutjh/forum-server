package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;

/**
 * 用户服务接口
 *
 * @author MeaquaOWO
 */
public interface UserService {

    /**
     * 获取用户信息（个人主页）
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    GetUserProfileResponse getUserProfile(Long userId);

    /**
     * 更新用户信息
     *
     * @param request 用户信息
     */
    void updateUserProfile(UpdateUserProfileRequest request);

    /**
     * 管理员获取用户列表
     *
     * @param request 用户列表请求参数
     * @return 用户列表分页响应
     */
    BaseListResponse<GetUserListElement> getUserList(GetUserListRequest request);

    /**
     * 管理员禁言用户
     *
     * @param request 禁言用户请求参数
     */
    void muteUser(MuteUserRequest request);

    /**
     * 管理员获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    AdminGetUserDetailResponse getUserDetail(Long id);

    /**
     * 超管获取管理员列表
     *
     * @param request 管理员列表请求参数
     * @return 管理员列表分页响应
     */
    BaseListResponse<GetAdminListElement> getAdminList(GetAdminListRequest request);

    /**
     * 更新用户背景图片
     *
     * @param request 更新用户背景图片请求
     */
    void updateBackgroundImage(UpdateBackgroundImageRequest request);

    /**
     * 获取用户通知设置
     *
     * @return 用户通知设置
     */
    GetNoticeSettingsResponse getNoticeSettings();

    /**
     * 更新用户通知设置
     *
     * @param request 更新用户通知设置请求
     */
    void updateNoticeSettings(UpdateNoticeSettingsRequest request);

    /**
     * 检查当前用户禁言状态
     *
     * @return 禁言状态响应
     */
    CheckMuteResponse checkMute();
}
