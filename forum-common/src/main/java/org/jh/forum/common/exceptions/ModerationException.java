package org.jh.forum.common.exceptions;

import com.aliyun.green20220302.models.TextModerationPlusResponseBody;
import lombok.Getter;

import java.util.List;

/**
 * @author SugarMGP
 */
@Getter
public class ModerationException extends RuntimeException {
    private final List<TextModerationPlusResponseBody.TextModerationPlusResponseBodyDataResult> results;
    private final String requestId;

    public ModerationException(String requestId, List<TextModerationPlusResponseBody.TextModerationPlusResponseBodyDataResult> results) {
        super();
        this.requestId = requestId;
        this.results = results;
    }
}
