package org.jh.forum.common.constants;

import lombok.Getter;
import lombok.Setter;

/**
 * 统一错误码枚举
 * @author Patrick_Star
 * @date 2025/4/6
 */
@Getter
public enum ExceptionEnum {
    UNKNOWN_ERROR("200500", "未知错误"),
    ;

    private String errorCode;
    private String errorMsg;

    ExceptionEnum(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

}
