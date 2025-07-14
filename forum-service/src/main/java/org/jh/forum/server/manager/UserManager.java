package org.jh.forum.server.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.dto.UserInfoDTO;
import org.jh.forum.common.entity.User;
import org.jh.forum.common.entity.UserDetail;
import org.jh.forum.server.mapper.UserDetailMapper;
import org.jh.forum.server.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     * 批量获取用户昵称
     *
     * @param userIds 用户ID集合
     * @return 用户ID到昵称的映射
     */
    public Map<Long, String> getUsernamesByIds(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }

        // 使用MyBatis-Plus的in查询
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, userIds);
        List<User> users = userMapper.selectList(queryWrapper);

        // 转换为Map
        return users.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        User::getNickname,
                        (existing, replacement) -> existing // 处理重复键的情况
                ));
    }
}
