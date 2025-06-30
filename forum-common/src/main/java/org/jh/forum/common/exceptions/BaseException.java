package org.jh.forum.common.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Patrick_Star
 * @date 2025/4/6
 */
@Setter
@Getter
public class BaseException extends RuntimeException {
    private Integer errorCode;
    private String errorMsg;

    public BaseException(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        if (StringUtils.isNotBlank(errorMsg)) {
            return errorMsg;
        }
        return getMessage();
    }
}
