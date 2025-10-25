package org.jh.forum.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 图片信息DTO
 *
 * @author SugarMGP
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PictureInfoDTO implements Serializable {
    @Schema(description = "图片URL")
    String url;

    @Schema(description = "缩略图URL")
    String thumbnailUrl;
}
