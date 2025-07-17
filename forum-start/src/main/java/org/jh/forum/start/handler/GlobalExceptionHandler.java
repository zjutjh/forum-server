package org.jh.forum.start.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.start.models.AjaxResult;
import org.jh.forum.start.utils.HandlerUtils;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 未被其余handler处理，则最终进入该handler处理，处理Exception子类
 *
 * @author MangoGovo
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
        HandlerUtils.logException(e, request);
        return AjaxResult.fail(ExceptionEnum.SERVER_ERROR);
    }
}
