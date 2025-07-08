package org.jh.forum.common.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.jh.forum.common.constants.ExceptionEnum;
import org.springframework.http.HttpStatus;


/**
 * 业务异常
 *
 * @author MangoGovo
 */
@Setter
@Getter
public class ApiException extends BaseException {
    private Integer statusCode;

    /**
     * 业务异常构造函数
     *
     * @param statusCode Http状态码
     * @param errorCode  错误码
     * @param errorMsg   错误信息
     */
    public ApiException(Integer statusCode, Integer errorCode, String errorMsg) {
        super(errorCode, errorMsg);
        this.statusCode = statusCode;
    }

    /**
     * 包装错误枚举为业务异常
     *
     * @param e 错误枚举
     */
    public ApiException(ExceptionEnum e) {
        super(e.getErrorCode(), e.getErrorMsg());
        this.statusCode = HttpStatus.OK.value();
    }

    /**
     * 包装错误枚举为业务异常
     *
     * @param e     错误枚举
     * @param cause 错误原因
     */
    public ApiException(ExceptionEnum e, Throwable cause) {
        super(e.getErrorCode(), e.getErrorMsg(), cause);
        this.statusCode = HttpStatus.OK.value();
    }

    /**
     * 包装RPC调用异常为业务异常
     *
     * @param e RPC调用异常
     */
    public ApiException(ForumServiceException e) {
        super(e.getErrorCode(), e.getErrorMsg(), e.getCause());
        this.statusCode = HttpStatus.OK.value();
    }
}
