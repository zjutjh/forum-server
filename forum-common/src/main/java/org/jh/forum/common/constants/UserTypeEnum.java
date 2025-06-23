package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author MangoGovo
 */
@Getter
@AllArgsConstructor
public enum UserTypeEnum {
    STUDENT("student"),
    ADMIN("admin"),
    SUPER_ADMIN("super_admin");

    @EnumValue
    private final String value;
}