package org.jh.forum.common.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.GenderEnum;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(max = 20)
    private String signature;

    @NotNull
    private GenderEnum gender;

    @NotNull
    @Size(max = 50)
    private String profile;

    @NotNull
    private String email;

    @NotNull
    private String collegeId;

    private LocalDate birthday;

    @NotNull
    private Boolean birthdayVisible;

    @NotNull
    private Boolean collegeVisible;

    @NotNull
    private Boolean realnameVisible;

    @NotNull
    private Boolean studentIdVisible;
}
