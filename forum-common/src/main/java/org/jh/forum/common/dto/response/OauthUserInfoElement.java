package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.GenderEnum;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OauthUserInfoElement {
    @Schema(description = "性别")
    GenderEnum gender;
    @Schema(description = "姓名")
    String name;
    @Schema(description = "学号")
    String studentId;
    @Schema(description = "学生类型(本科生,研究生)")
    String studentType;
}
