package org.jh.forum.common.exceptions;

import lombok.Getter;
import org.jh.forum.common.constants.ExceptionEnum;

/**
 * 业务异常，自动捕获并返回给前端
 *
 * @author Patrick_Star
 * @date 2025/4/6
 */
@Getter
public class ApiException extends RuntimeException {
    private final Integer errorCode;
    private final String errorMsg;

    public ApiException(Integer errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ApiException(Integer errorCode, String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ApiException(ExceptionEnum e) {
        this(e.getErrorCode(), e.getErrorMsg());
    }

    public ApiException(ExceptionEnum e, Throwable cause) {
        this(e.getErrorCode(), e.getErrorMsg(), cause);
    }
}
