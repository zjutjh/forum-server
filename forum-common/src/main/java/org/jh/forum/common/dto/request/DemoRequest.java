package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

/**
 * @author MangoGovo
 */
@Data
@Schema(description = "样例请求体")
public class DemoRequest {
    @Schema(description = "姓名", example = "MangoGovo")
    @NotBlank(message = "姓名不能为空")
    private String name;
}
