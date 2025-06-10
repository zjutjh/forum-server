package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author MangoGovo
 */

@Getter
public enum UserTypeEnum {
    STUDENT("Student"), ADMIN("Admin"), SUPER_ADMIN("SuperAdmin"),
    ;

    private final String value;

    UserTypeEnum(String value) {
        this.value = value;
    }
}