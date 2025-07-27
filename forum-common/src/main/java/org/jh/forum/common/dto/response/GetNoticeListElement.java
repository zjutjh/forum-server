package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.NoticePositionTypeEnum;
import org.jh.forum.common.constants.NoticeTypeEnum;
import org.jh.forum.common.dto.UserInfoDTO;

import java.time.LocalDateTime;


/**
 * @author lyyzzz
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetNoticeListElement {
    @Schema(description = "通知ID")
    private Long id;

    @Schema(description = "发送人信息")
    private UserInfoDTO senderInfo;

    @Schema(description = "消息类型（赞/收藏/评论/at）")
    private NoticeTypeEnum type;

    @Schema(description = "位置类型（帖子/评论）")
    private NoticePositionTypeEnum positionType;

    @Schema(description = "关联位置ID（帖子ID/评论所属帖子ID）")
    private Long positionId;

    @Schema(description = "关联评论ID（若有）")
    private Long commentId;

    @Schema(description = "是否已读")
    private Boolean isRead;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}