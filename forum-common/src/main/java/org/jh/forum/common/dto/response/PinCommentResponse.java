package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qianqianzyk
 */
@Data
public class PinCommentResponse {
    @Schema(description = "置顶状态")
    private Boolean status;
}
