package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
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
    @JsonValue
    private final String value;
}