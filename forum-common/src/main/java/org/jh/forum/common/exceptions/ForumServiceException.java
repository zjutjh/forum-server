package org.jh.forum.common.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.util.StringUtil;

/**
 * @author Patrick_Star
 * @date 2025/4/6
 */
@Setter
@Getter
public class ForumServiceException extends RuntimeException {
    private String errorCode;
    private String errorMsg;

    private ForumServiceException() {
    }

    public ForumServiceException(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        if (StringUtil.isNotBlank(errorMsg)) {
            return errorMsg;
        }
        return getMessage();
    }
}
