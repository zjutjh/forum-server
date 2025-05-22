package org.jh.forum.common.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.jh.forum.common.constants.ExceptionEnum;


/**
 * Rpc调用异常, 只在Rpc调用时使用, 需要在控制层封装成业务异常
 *
 * @author MangoGovo
 */
@Setter
@Getter
public class ServiceException extends BaseException {
    public ServiceException(Integer errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public ServiceException(ExceptionEnum e) {
        super(e.getErrorCode(), e.getErrorMsg());
    }
}
