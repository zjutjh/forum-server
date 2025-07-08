package org.jh.forum.server.manger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.entity.UserDetail;
import org.jh.forum.server.mapper.UserDetailMapper;
import org.jh.forum.server.mapper.UserMapper;
import org.springframework.stereotype.Service;

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
}
