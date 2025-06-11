package org.jh.forum.server.manger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public List<String> getRoleList(Long userId) {
        String role = userMapper.selectById(userId).getRole();
        return List.of(role);
    }
}
