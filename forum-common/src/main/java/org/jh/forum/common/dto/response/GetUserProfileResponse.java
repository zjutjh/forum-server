package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.GenderEnum;

import java.time.LocalDate;

/**
 * @author MeaquaOWO
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetUserProfileResponse {
    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像URL")
    private String avatar;

    @Schema(description = "用户个性签名")
    private String signature;

    @Schema(description = "用户简介")
    private String profile;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "用户性别")
    private GenderEnum gender;

    @Schema(description = "是否为自己")
    private Boolean isSelf;


    @Schema(description = "用户背景图URL")
    private String background;

    // 动态字段（根据权限显示）
    @Schema(description = "真实姓名（根据可见性展示，可为null）")
    private String realname;

    @Schema(description = "学院（根据可见性展示，可为null）")
    private String college;

    @Schema(description = "生日（根据可见性展示，可为null）")
    private LocalDate birthday;
}
