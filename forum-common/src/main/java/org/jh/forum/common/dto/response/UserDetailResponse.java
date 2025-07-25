package org.jh.forum.common.dto.response;

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
public class UserDetailResponse {
    private Long id;
    private String nickname;
    private String avatarUrl;
    private String signature;
    private String profile;
    private String email;
    private GenderEnum gender;
    private Boolean isSelf;

    // 动态字段（根据权限显示）
    private String realname;
    private Long collegeId;         // 根据 collegeVisible 设置
    private LocalDate birthday;     // 根据 birthdayVisible 设置

}
