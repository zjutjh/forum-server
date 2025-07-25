package org.jh.forum.common.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.GenderEnum;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {

    private Long id;                // 用户ID
    private String nickname;        // 昵称
    private String realname;        // 真实姓名
    private String studentId;       // 学号
    private GenderEnum gender;      // 性别（0-男，1-女，2-保密）
    private String avatarUrl;       // 头像URL
    private String phone;           // 手机号

    private String signature;       // 个性签名
    private String profile;         // 个人简介
    private String email;           // 邮箱
    private LocalDate birthday;     // 生日

    @NotNull
    private Boolean birthdayVisible; // 生日是否可见
    private Boolean collegeVisible;  // 学院是否可见
    private Boolean realnameVisible; // 真实姓名是否可见
}
