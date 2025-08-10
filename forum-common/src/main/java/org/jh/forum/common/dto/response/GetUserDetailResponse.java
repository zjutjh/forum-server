package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.GenderEnum;
import org.jh.forum.common.constants.UserStatusEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author MeaquaOWO
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetUserDetailResponse {
    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像URL")
    private String avatar;


    @Schema(description = "用户背景图URL")
    private String background;

    @Schema(description = "用户个性签名")
    private String signature;

    @Schema(description = "用户简介")
    private String profile;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "用户性别")
    private GenderEnum gender;

    @Schema(description = "真实姓名")
    private String realname;

    @Schema(description = "学号")
    private String studentId;

    @Schema(description = "学院")
    private String college;

    @Schema(description = "生日")
    private LocalDate birthday;

    @Schema(description = "注册时间")
    private LocalDateTime createdAt;

    @Schema(description = "状态")
    private UserStatusEnum status;
}
