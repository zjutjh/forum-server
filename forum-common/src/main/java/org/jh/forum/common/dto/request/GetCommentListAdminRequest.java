package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianqianzyk
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GetCommentListAdminRequest extends BaseListRequest{
    @Parameter(description = "帖子ID")
    @NotNull
    private Integer postId;

    @Parameter(description = "评论状态（全部，已删，未删）")
    @NotBlank
    private String status;
}
