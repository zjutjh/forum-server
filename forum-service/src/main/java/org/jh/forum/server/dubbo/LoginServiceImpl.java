package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.LoginService;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.GenderEnum;
import org.jh.forum.common.constants.UserTypeEnum;
import org.jh.forum.common.entity.User;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.manager.UserManager;
import org.jh.forum.server.mapper.UserMapper;

import java.util.Objects;

/**
 * @author MangoGovo
 */
@DubboService
@Slf4j
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {
    private UserMapper userMapper;
    private UserManager userManager;

    /**
     * 学生登录
     * TODO 接入用户中心
     * TODO 处理统一密码修改之后数据库同步问题
     *
     * @return 用户类型
     */
    @Override
    public UserTypeEnum login(String username, String password, UserTypeEnum loginType) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getStudentId, username));
        if (Objects.isNull(user)) {
            // 首次登录, 数据库创建对象
            // 统一登录,下面的字段从统一拿
            user = User.builder()
                    .nickname(getRandomNickname())
                    .realname("default")
                    .studentId(username)
                    .password(BCrypt.hashpw(password))
                    .college("")
                    .gender(EnumUtil.getBy(GenderEnum::getDesc, "男"))
                    .role(loginType)
                    .reportCount(0)
                    .resolvedReportCount(0).build();
            userMapper.insert(user);
            userManager.insertUserDetail(user.getId());
        } else if (!BCrypt.checkpw(password, user.getPassword()) || !user.getRole().equals(loginType)) {
            // 数据库密码校验错误
            throw new ApiException(ExceptionEnum.WRONG_USERNAME_OR_PASSWORD);
        }
        StpUtil.login(user.getId());
        return user.getRole();
    }

    private String getRandomNickname() {
        for (int i = 0; i < 10; i++) {
            String nickname = "精小弘" + RandomUtil.randomNumbers(6);
            if (!userMapper.exists(new LambdaQueryWrapper<User>().eq(User::getNickname, nickname))) {
                return nickname;
            }
        }
        return "精小弘" + IdUtil.objectId();
    }
}
