package org.jh.forum.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zzb
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReportUserRequest {
    @Schema(description = "举报类型 1-其他 2-言论违规 3-个人信息违规",
            allowableValues = {"1", "2", "3"})
    @NotNull
    private Integer type;

    @Schema(description = "举报内容 选择其他时填写")
    private String reason;

    @Schema(description = "被举报的用户ID")
    @JsonProperty("user_id")
    @NotNull
    private Long userId;
}
