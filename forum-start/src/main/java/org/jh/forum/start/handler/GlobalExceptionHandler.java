package org.jh.forum.start.handler;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.start.models.AjaxResult;
import org.jh.forum.start.models.ErrorDetail;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;

/**
 * 未被其余handler处理，则最终进入该handler处理，处理Exception子类
 *
 * @author patrick_star
 * @date 2025/4/24
 */
@ControllerAdvice
@Slf4j
@Order(1000)
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public AjaxResult<Object> handleGlobalException(Exception e, HttpServletRequest request) {
        ErrorDetail errorDetail = ErrorDetail.builder().code(ExceptionEnum.UNKNOWN_ERROR.getErrorCode())
                .message(ExceptionEnum.UNKNOWN_ERROR.getErrorMsg()).build();
        log.error("[{}] | {} | request={}", Instant.now(), request.getRequestURI(),
                JSON.toJSONString(request.getParameterMap()), e);
        return AjaxResult.FAIL("请求异常", errorDetail);
    }

}
