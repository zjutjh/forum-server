package org.jh.forum.server.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.dto.UserInfoDTO;
import org.jh.forum.common.entity.User;
import org.jh.forum.common.entity.UserDetail;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.mapper.UserDetailMapper;
import org.jh.forum.server.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author MangoGovo
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserManager {
    private final UserMapper userMapper;
    private final UserDetailMapper userDetailMapper;

    public void insertUserDetail(Long userId) {
        userDetailMapper.insert(UserDetail.builder()
                .userId(userId)
                .email("")
                .profile("")
                .signature("")
                .birthday(null)
                .birthdayVisible(true)
                .collegeVisible(true)
                .realnameVisible(true)
                .build());
    }

    public List<String> getRoleList(Long userId) {
        String role = userMapper.selectById(userId).getRole().getValue();
        return List.of(role);
    }

    public UserInfoDTO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        return UserInfoDTO.builder()
                .id(userId)
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
    }

    public void muteUser(long id, int hours) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        user.setMutedUntil(hours == 0 ? null : LocalDateTime.now().plusHours(hours));
        userMapper.updateById(user);
    }
}
