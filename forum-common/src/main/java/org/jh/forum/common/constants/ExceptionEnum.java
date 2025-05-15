package org.jh.forum.common.constants;

import lombok.Getter;

/**
 * 统一错误码枚举
 *
 * @author Patrick_Star
 * @date 2025/4/6
 */
@Getter
public enum ExceptionEnum {
    INVALID_PARAMETER("200000", "参数错误"),
    DATABASE_ERROR("200001", "数据库异常"),
    JSON_PARSE_ERROR("200002", "json解析失败"),
    EXCEED_MAX_GET_LOCK_COUNT("200003", "获取锁超出限制"),
    UNKNOWN_ERROR("200500", "未知错误"),
    ;

    private String errorCode;
    private String errorMsg;

    ExceptionEnum(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

}
