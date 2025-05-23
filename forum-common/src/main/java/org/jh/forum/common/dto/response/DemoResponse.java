package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author MangoGovo
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DemoResponse {
    @Schema(description = "greet")
    @NotBlank
    private String greet;
}
