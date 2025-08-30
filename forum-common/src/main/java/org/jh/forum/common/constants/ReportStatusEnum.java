package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 举报状态枚举
 *
 * @author zzb
 */
@Getter
@AllArgsConstructor
public enum ReportStatusEnum {
    PENDING("pending", "未处理"),
    SUCCESS("success", "举报成功"),
    FAILURE("failure", "举报失败");

    @EnumValue
    @JsonValue
    private final String value;

    private final String desc;
}
