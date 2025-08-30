package org.jh.forum.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 附件类型枚举
 *
 * @author SugarMGP
 */
@Getter
@AllArgsConstructor
public enum AttachmentTypeEnum {
    PICTURE("picture", "图片"),
    DOCUMENT("document", "文档"),
    VIDEO("video", "视频");

    @EnumValue
    @JsonValue
    private final String value;

    private final String desc;
}