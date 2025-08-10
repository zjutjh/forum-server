package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author MeaquaOWO
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateBackgroundImageRequest {
    @Schema(description = "背景图片链接")
    private String backgroundImage;
}
