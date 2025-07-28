package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.UserService;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.dto.request.UpdateUserDetailRequest;
import org.jh.forum.common.dto.response.GetUserDetailResponse;
import org.jh.forum.common.entity.User;
import org.jh.forum.common.entity.UserDetail;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.mapper.UserDetailMapper;
import org.jh.forum.server.mapper.UserMapper;

import jakarta.annotation.Resource;


/**
 * @author MeaquaOWO
 */
@DubboService
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserDetailMapper userDetailMapper;

    @Override
    public GetUserDetailResponse getUserProfile(Long userId) {
        User userEntity = userMapper.selectById(userId);
        if (userEntity == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        UserDetail detailEntity = userDetailMapper.selectById(userId);
        if (detailEntity == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }

        GetUserDetailResponse resp = new GetUserDetailResponse();
        resp.setNickname(userEntity.getNickname());
        resp.setAvatar(userEntity.getAvatar());
        resp.setSignature(detailEntity.getSignature());
        resp.setProfile(detailEntity.getProfile());
        resp.setEmail(detailEntity.getEmail());
        resp.setGender(userEntity.getGender());
        resp.setIsSelf(userId.equals(StpUtil.getLoginIdAsLong()));
        resp.setRealname(detailEntity.getRealnameVisible() ? userEntity.getRealname() : null);
        resp.setCollegeId(detailEntity.getCollegeVisible() ? userEntity.getCollegeId() : null);
        resp.setBirthday(detailEntity.getBirthdayVisible() ? detailEntity.getBirthday() : null);
        return resp;
    }

    @Override
    public void updateUserProfile(UpdateUserDetailRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        User userEntity = userMapper.selectById(userId);
        UserDetail detailEntity = userDetailMapper.selectById(userId);

        userEntity.setAvatar(request.getAvatar());
        userEntity.setNickname(request.getNickname());
        userEntity.setGender(request.getGender());
        userEntity.setCollegeId(request.getCollegeId());
        detailEntity.setSignature(request.getSignature());
        detailEntity.setEmail(request.getEmail());
        detailEntity.setBirthday(request.getBirthday());
        detailEntity.setProfile(request.getProfile());
        detailEntity.setBirthdayVisible(request.getBirthdayVisible());
        detailEntity.setCollegeVisible(request.getCollegeVisible());
        detailEntity.setRealnameVisible(request.getRealnameVisible());


        userMapper.updateById(userEntity);
        userDetailMapper.updateById(detailEntity);
    }
}
