package org.jh.forum.common.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.GenderEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUpdateAdminRequest {
    private String userId;
    private String realname;
    private String studentId;
    private LocalDateTime mutedUntil;
    private String avatar;       // 头像
    private String nickname;        // 昵称
    private String signature;       // 个性签名
    private GenderEnum gender;      //性别
    private String profile;         // 个人简介
    private String email;           // 邮箱
    private LocalDate birthday;     // 生日
    private Boolean birthdayVisible; // 生日可见性
    private Boolean collegeVisible; // 学院可见性
    private Boolean realnameVisible; // 真实姓名可见性
}
