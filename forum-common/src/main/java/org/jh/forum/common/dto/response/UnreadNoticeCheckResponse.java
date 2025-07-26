package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * @author lyyzzz
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UnreadNoticeCheckResponse implements Serializable {
    @Schema(description = "未读通知数")
    @JsonProperty("unread_count")
    private Integer unreadCount;
}
