package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jh.forum.common.constants.AnnouncementTypeEnum;

/**
 * 用户公告列表查询请求DTO
 *
 * @author SituChengxiang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "用户公告列表查询请求")
public class GetAnnouncementListRequest extends BaseListRequest {
    @Schema(description = "类型筛选 (空则全部)")
    private AnnouncementTypeEnum type;
}