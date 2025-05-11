package org.jh.forum.start.models;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * 错误详情类
 *
 * @author patrick_star
 * @version 1.1
 */
@Data
@Builder
public final class ErrorDetail {
    private String code;
    private String message;
}
