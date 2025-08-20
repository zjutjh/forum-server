package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.GenderEnum;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * @author MeaquaOWO
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateUserDetailRequest {
    @NotNull
    @Schema(description = "用户头像URL")
    private String avatar;

    @NotBlank
    @Size(max = 20)
    @Schema(description = "用户昵称")
    private String nickname;

    @NotNull
    @Size(max = 20)
    @Schema(description = "用户个性签名")
    private String signature;

    @NotNull
    @Schema(description = "用户性别")
    private GenderEnum gender;

    @NotNull
    @Size(max = 50)
    @Schema(description = "用户简介")
    private String profile;

    @NotNull
    @Email
    @Schema(description = "用户邮箱")
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
    @Schema(description = "学院是否可见")
    private Boolean collegeVisible;

    @NotNull
    @Schema(description = "真实姓名是否可见")
    private Boolean realnameVisible;

    @NotNull
    @Schema(description = "学号是否可见")
    private Boolean studentIdVisible;
}
