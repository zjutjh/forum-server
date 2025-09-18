package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.UserTypeEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 超管获取管理员列表元素
 *
 * @author SugarMGP
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetAdminListElement implements Serializable {
    @Schema(description = "账号ID")
    private Long id;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "类型（admin/super_admin）")
    private UserTypeEnum type;

    @Schema(description = "注册时间")
    private LocalDateTime createdAt;

    @Schema(description = "处理举报次数")
    private Integer reportCount;
}