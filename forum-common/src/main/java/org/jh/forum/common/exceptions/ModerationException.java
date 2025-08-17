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

    public ModerationException(List<TextModerationPlusResponseBody.TextModerationPlusResponseBodyDataResult> results) {
        super();
        this.results = results;
    }
}
