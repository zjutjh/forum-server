package org.jh.forum.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.AttachmentTypeEnum;

/**
 * 附件信息DTO（暂未使用）
 *
 * @author SugarMGP
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttachmentInfoDTO {
    @Schema(description = "附件URL")
    String url;

    @Schema(description = "附件类型")
    AttachmentTypeEnum type;

    @Schema(description = "原始文件名")
    String filename;
}
