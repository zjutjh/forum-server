package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.UserStatusEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 普通用户账号列表项
 *
 * @author SugarMGP
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUserListElement implements Serializable {
    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户头像URL")
    private String avatar;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户学号")
    private String studentId;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "注册时间")
    private LocalDateTime createdAt;

    @Schema(description = "被举报次数")
    private Integer reportCount;

    @Schema(description = "状态")
    private UserStatusEnum status;

    @Schema(description = "禁言时间（status为muted时生效）")
    private LocalDateTime mutedUntil;
}