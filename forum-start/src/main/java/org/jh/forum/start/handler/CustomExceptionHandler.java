package org.jh.forum.start.handler;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.exceptions.ForumServiceException;
import org.jh.forum.start.models.AjaxResult;
import org.jh.forum.start.models.ErrorDetail;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;

/**
 * 处理 ForumServiceException 及其子类，最先执行
 *
 * @author Patrick_Star
 * @date 2025/4/24
 */
@ControllerAdvice
@Slf4j
@Order(80)
@RequiredArgsConstructor
public class CustomExceptionHandler {

    @ExceptionHandler(ForumServiceException.class)
    @ResponseBody
    public AjaxResult<Object> handleAppException(ForumServiceException e, HttpServletRequest request) {
        ErrorDetail errorDetail = ErrorDetail.builder().code(e.getErrorCode())
                .message(e.getErrorMsg()).build();
        log.error("[{}] | {} | request={}", Instant.now(), request.getRequestURI(),
                JSON.toJSONString(request.getParameterMap()), e);
        return AjaxResult.FAIL(errorDetail);
    }

}
