package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author  SituChengxiang
 */

@AllArgsConstructor
@Getter
public enum AnnouncementTypeEnum {
    /**
     * 公告类型枚举
     * 类型说明：
     * systematic: 系统公告
     * scholastic: 学校公告
     */
    SYSTEMATIC("systematic", "系统公告"),
    SCHOLASTIC("scholastic", "学校公告");

    @EnumValue
    private final String code;
    private final String description;

    public static AnnouncementTypeEnum fromCode(String code){
        for (AnnouncementTypeEnum type : AnnouncementTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的公告类型: " + code);
    }
}
