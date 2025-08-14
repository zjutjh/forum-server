package org.jh.forum.server.manager;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Collections.emptyList();
        }
        return List.of(user.getRole().getValue());
    }

    public UserInfoDTO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return UserInfoDTO.builder().build();
        }
        return UserInfoDTO.builder()
                .id(userId)
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
    }

    public void muteUser(long id, int hours) {
        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, id)
                .set(User::getMutedUntil, LocalDateTime.now().plusHours(hours))
        );
    }

    public void addMuteTime(long id, int hours) {
        User user = getUserOrThrow(id);
        LocalDateTime mutedUntil = user.getMutedUntil() == null ? LocalDateTime.now() : user.getMutedUntil();
        user.setMutedUntil(mutedUntil.plusHours(hours));
        userMapper.updateById(user);
    }

    public String generateRandomNickname() {
        for (int i = 0; i < 10; i++) {
            String nickname = "精小弘" + RandomUtil.randomNumbers(6);
            if (!userMapper.exists(new LambdaQueryWrapper<User>().eq(User::getNickname, nickname))) {
                return nickname;
            }
        }
        return "精小弘" + IdUtil.objectId();
    }

    public User getUserOrThrow(Long id) {
        return Optional.ofNullable(userMapper.selectById(id))
                .orElseThrow(() -> new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND));
    }
}
