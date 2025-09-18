package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.ReportTypeEnum;
import org.jh.forum.common.dto.PictureInfoDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 获取举报信息元素
 *
 * @author zzb
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetReportInfoElement implements Serializable {
    @Schema(description = "举报人ID")
    private Long userId;

    @Schema(description = "举报类型")
    private ReportTypeEnum type;

    @Schema(description = "详细描述")
    private String reason;

    @Schema(description = "举报时间")
    private LocalDateTime createdAt;

    @Schema(description = "举报图片列表")
    private List<PictureInfoDTO> pictures;
}
