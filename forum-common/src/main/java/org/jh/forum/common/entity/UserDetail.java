package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * @author SugarMGP
 * @TableName user_detail
 */
@TableName(value = "user_detail")
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetail {
    @TableId(value = "user_id", type = IdType.INPUT)
    private Long userId;

    private String signature;

    private String profile;

    private String email;

    private LocalDate birthday;

    private String backgroundImage;

    private Boolean birthdayVisible;

    private Boolean collegeVisible;

    private Boolean realnameVisible;

    private Boolean studentIdVisible;
}