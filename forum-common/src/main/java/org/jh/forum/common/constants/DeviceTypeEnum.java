package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备类型枚举
 *
 * @author SugarMGP
 */
@AllArgsConstructor
@Getter
public enum DeviceTypeEnum {
    WEB("web", "网页端"),
    PHONE("phone", "手机端"),
    DESKTOP("desktop", "桌面端");

    @EnumValue
    @JsonValue
    private final String value;
    private final String desc;
}
