package org.jh.forum.common.exceptions;

import lombok.Getter;
import org.jh.forum.common.dto.response.ModerationResultResponse;

import java.util.List;

/**
 * @author SugarMGP
 */
@Getter
public class ModerationException extends RuntimeException {
    private final List<ModerationResultResponse.Label> labels;
    private final String requestId;

    public ModerationException(String requestId, List<ModerationResultResponse.Label> labels) {
        super();
        this.requestId = requestId;
        this.labels = labels;
    }
}
