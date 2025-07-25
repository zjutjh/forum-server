package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author SugarMGP
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UploadPictureResponse {
    @Schema(description = "附件ID")
    private Long attachmentId;
}
