package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新用户背景图片请求
 *
 * @author MeaquaOWO
 */
@Data
public class UpdateBackgroundImageRequest {
    @Schema(description = "背景图片链接")
    private String backgroundImage;
}
