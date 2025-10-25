package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jh.forum.common.constants.PostCategoryEnum;
import org.jh.forum.common.constants.PostStatusEnum;

import java.time.LocalDate;

/**
 * 管理员获取帖子列表请求
 *
 * @author SugarMGP
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetAdminPostListRequest extends BaseListRequest {
    @Schema(description = "帖子板块")
    private PostCategoryEnum category;

    @Schema(description = "帖子状态")
    private PostStatusEnum status;

    @Schema(description = "帖子标题")
    private String title;

    @Schema(description = "发帖人")
    private String publisher;

    @Schema(description = "发帖日（yyyy-MM-dd）")
    private LocalDate date;
}
