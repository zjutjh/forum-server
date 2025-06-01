package org.jh.forum.user.service;

import org.jh.forum.user.dto.EditUserDTO;
import org.jh.forum.user.dto.UserDetailDTO;
import org.jh.forum.user.entity.UserEntity;
import org.jh.forum.user.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 获取用户详情
     * 根据可见性字段过滤敏感信息
     * @param userId 用户ID
     * @return 用户详情 DTO
     */
    public UserDetailDTO getUserDetail(Long userId) {
        //查询用户，若不存在抛出异常
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        UserDetailDTO dto = new UserDetailDTO();
        //属性拷贝
        BeanUtils.copyProperties(user, dto);

        //根据可见性进行过滤
        if (!user.getRealNameVisible()) dto.setRealName(null);
        if (!user.getStudentIdVisible()) dto.setStudentId(null);
        return dto;
    }

    /**
     * 编辑用户资料
     * 处理昵称唯一性校验、字段更新
     * @param dto 编辑请求数据
     * @param userId 当前用户ID
     */
    public void editProfile(EditUserDTO dto, Long userId) {
        //查询用户
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));

        //昵称重复性校验
        if (dto.getNickname() != null && !dto.getNickname().isEmpty()) {
            //查询是否重复
            userRepository.findByNickname(dto.getNickname()).ifPresent(existing -> {
                if (!existing.getId().equals(userId)) {
                    throw new RuntimeException("昵称已存在");
                }
            });
            user.setNickname(dto.getNickname());
        }

        //更新其它资料
        user.setGender(dto.getGender());
        user.setSignature(dto.getSignature());
        user.setBirthday(dto.getBirthday());
        user.setEmail(dto.getEmail());
        user.setRealNameVisible(dto.getRealNameVisible());
        user.setStudentIdVisible(dto.getStudentIdVisible());
        user.setBirthdayVisible(dto.getBirthdayVisible());
        user.setEmailVisible(dto.getEmailVisible());

        userRepository.save(user);
    }
}