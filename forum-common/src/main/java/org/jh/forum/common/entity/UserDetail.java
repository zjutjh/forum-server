package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author SugarMGP
 * @TableName user_detail
 */
@TableName(value = "user_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetail {
    @TableId(value = "user_id", type = IdType.INPUT)
    private Long userId;

    private String signature;

    private String profile;

    private String email;

    private LocalDate birthday;

    private Boolean birthdayVisible;

    private Boolean collegeVisible;

    private Boolean realnameVisible;
}