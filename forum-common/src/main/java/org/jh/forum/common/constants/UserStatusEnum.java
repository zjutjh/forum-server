package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author SugarMGP
 */
@Getter
@AllArgsConstructor
public enum UserStatusEnum {
    NORMAL("normal", "正常"),
    PENDING("pending", "待审"),
    MUTED("muted", "禁言");

    @EnumValue
    @JsonValue
    private final String value;

    private final String desc;
}
