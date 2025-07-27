package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.NoticePositionTypeEnum;
import org.jh.forum.common.constants.NoticeTypeEnum;

import jakarta.validation.constraints.NotNull;

/**
 * @author lyyzzz
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateNoticeRequest {
    @Schema(description = "接收人ID")
    @NotNull
    private Long receiverId;

    @Schema(description = "通知类型")
    @NotNull
    private NoticeTypeEnum type;

    @Schema(description = "位置类型（帖子/评论等）")
    @NotNull
    private NoticePositionTypeEnum positionType;

    @Schema(description = "关联位置ID（如帖子ID）")
    @NotNull
    private Long positionId;

    @Schema(description = "关联评论ID（如有）")
    private Long commentId;
}
