package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 
 * 公告操作响应DTO
 * 返回被操作公告的id
 * @author SituChengxiang
 */
@Data
@Schema(description = "公告响应")
public class AnnouncementOperationResponse {    @Schema(description = "公告ID", example = "1")
    @JsonProperty("announcement_id")
    private Long announceId;
}
