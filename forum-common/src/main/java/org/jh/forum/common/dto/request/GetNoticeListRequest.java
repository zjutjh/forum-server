package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jh.forum.common.constants.NoticeTypeEnum;

import java.io.Serializable;

/**
 * @author lyyzzz
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetNoticeListRequest extends BaseListRequest implements Serializable {
    @Schema(description = "通知类型")
    private Integer type;
}
