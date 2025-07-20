package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.NoticeTypeEnum;
import org.jh.forum.common.dto.UserInfoDTO;

import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetNoticeListElement {
    @Schema(description = "通知ID")
    private Long id;

    @Schema(description = "发送人信息")
    @JsonProperty("sender_info")
    private UserInfoDTO senderInfo;

    @Schema(description = "消息类型（赞/收藏/评论/at）")
    private NoticeTypeEnum type;

    @Schema(description = "位置类型（帖子/评论）")
    @JsonProperty("position_type")
    private NoticeTypeEnum positionType;

    @Schema(description = "关联位置ID（帖子ID/评论所属帖子ID）")
    @JsonProperty("position_id")
    private Long positionId;

    @Schema(description = "关联评论ID（若有）")
    @JsonProperty("comment_id")
    private Long commentId;

    @Schema(description = "创建时间")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

}