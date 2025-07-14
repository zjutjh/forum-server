package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zzb
 */
@Getter
@AllArgsConstructor
public enum HandleReportEnum {
    NO_PUNISHMENT("no_punishment", "无处罚"),
    SHORT_MUTE("short_mute", "短期禁言(1天)"),
    LONG_MUTE("long_mute", "长期禁言(7天)"),
    CUSTOM_MUTE("custon_mute", "自定义禁言时长"),
    BAN_ACCOUNT("ban_account", "封禁账号");

    @EnumValue
    @JsonValue
    private final String value;

    private final String desc;
}
