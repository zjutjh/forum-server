package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qianqianzyk
 */
@Data
public class UpvoteCommentResponse {
    @Schema(description = "点赞状态")
    private Boolean status;
}
