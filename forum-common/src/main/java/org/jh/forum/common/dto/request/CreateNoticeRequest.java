package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.NoticeTypeEnum;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateNoticeRequest {
    @Schema(description = "接收人ID")
    @NotNull(message = "接收人ID不能为空")
    private Long receiverId;

    @Schema(description = "通知类型")
    @NotNull(message = "通知类型不能为空")
    private NoticeTypeEnum type;

    @Schema(description = "位置类型（帖子/评论等）")
    @NotNull(message = "位置类型不能为空")
    private NoticeTypeEnum positionType;

    @Schema(description = "关联位置ID（如帖子ID）")
    @NotNull(message = "位置ID不能为空")
    private Long positionId;

    @Schema(description = "关联评论ID（如有）")
    private Long commentId;

    @Schema(description = "附加属性信息（JSON格式字符串）")
    private String attribute;


}
