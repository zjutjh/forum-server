package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Data;

/**
 * @author MangoGovo
 */
@Builder
@Data
public class DemoResponse {
    @Schema(description = "greet")
    @NotBlank
    private String greet;
}
