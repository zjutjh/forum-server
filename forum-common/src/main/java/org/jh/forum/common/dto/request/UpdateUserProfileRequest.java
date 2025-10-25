package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jh.forum.common.annotation.NoExternalLink;
import org.jh.forum.common.constants.GenderEnum;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 修改用户资料请求
 *
 * @author MeaquaOWO
 */
@Data
public class UpdateUserProfileRequest implements Serializable {
    @NotNull
    @Schema(description = "用户头像URL")
    private String avatar;

    @NotBlank
    @Size(max = 12)
    @Schema(description = "用户昵称")
    @NoExternalLink
    private String nickname;

    @NotNull
    @Size(max = 20)
    @Schema(description = "用户个性签名")
    @NoExternalLink
    private String signature;

    @NotNull
    @Schema(description = "用户性别")
    private GenderEnum gender;

    @NotNull
    @Size(max = 50)
    @Schema(description = "用户简介")
    @NoExternalLink
    private String profile;

    @NotNull
    @Email
    @Schema(description = "用户邮箱")
    @Size(max = 40)
    private String email;

    @NotNull
    @Schema(description = "学院代号")
    private String collegeId;

    @Schema(description = "生日")
    @Past
    private LocalDate birthday;

    @NotNull
    @Schema(description = "生日是否可见")
    private Boolean birthdayVisible;

    @NotNull
    @Schema(description = "真实姓名是否可见")
    private Boolean realnameVisible;

    @NotNull
    @Schema(description = "学号是否可见")
    private Boolean studentIdVisible;
}
