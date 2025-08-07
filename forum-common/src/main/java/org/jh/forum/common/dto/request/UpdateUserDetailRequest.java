package org.jh.forum.common.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.GenderEnum;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author MeaquaOWO
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateUserDetailRequest {
    @NotNull
    private String avatar;

    @NotNull
    private String nickname;

    @NotNull
    private String signature;

    @NotNull
    private GenderEnum gender;

    @NotNull
    private String profile;

    @NotNull
    private String email;

    @NotNull
    private String college;

    private LocalDate birthday;

    @NotNull
    private Boolean birthdayVisible;

    @NotNull
    private Boolean collegeVisible;

    @NotNull
    private Boolean realnameVisible;
}
