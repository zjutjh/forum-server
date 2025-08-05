package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jh.forum.common.constants.UserStatusEnum;

import jakarta.validation.constraints.NotNull;

/**
 * @author SugarMGP
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetUserListRequest extends BaseListRequest {
    @Schema(description = "账号状态（空则不做筛选）")
    private UserStatusEnum status;

    @Schema(description = "被举报记录（no-无, yes-有, all-全部）")
    @NotNull
    private String reported;

    @Schema(description = "搜索关键词（用户名/学号）")
    private String keyword;
}
