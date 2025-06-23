package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author SugarMGP
 */
@Getter
@AllArgsConstructor
public enum GenderEnum {
    FEMALE("female", "女"),
    MALE("male", "男"),
    UNKNOW("unknow", "保密");

    @EnumValue
    private final String value;
    private final String desc;
}