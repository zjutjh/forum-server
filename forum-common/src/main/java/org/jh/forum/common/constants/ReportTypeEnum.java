package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 举报原因类型枚举
 *
 * @author zzb
 */
@Getter
@AllArgsConstructor
public enum ReportTypeEnum {
    OTHER("other", "其他"),
    PORNOGRAPHY("pornography", "色情低俗"),
    CYBERBULLYING("cyberbullying", "网络暴力"),
    CONTENT_INFRINGEMENT("content_infringement", "内容侵权"),
    ILLEGAL_ACTIVITY("illegal_activity", "违法违规"),
    POLITICS_RELATED("politics_related", "政治相关"),
    TROLL_BEHAVIOR("troll_behavior", "恶意引战"),
    RUMOR_SPREADING("rumor_spreading", "造谣传谣"),

    SPEECH_VIOLATION("speech_violation", "言论违规"),
    PERSONAL_INFO_VIOLATION("personal_info_violation", "个人信息违规");

    @EnumValue
    @JsonValue
    private final String value;

    private final String desc;

}
