package org.jh.forum.start.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.common.exceptions.BaseException;
import org.jh.forum.start.models.AjaxResult;
import org.jh.forum.start.utils.HandlerUtils;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;

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

    @ExceptionHandler(BaseException.class)
    @ResponseBody
    public AjaxResult<Object> handleAppException(BaseException e, HttpServletRequest request) {
        HandlerUtils.logException(e, request);
        if (e instanceof ApiException) {
            return AjaxResult.fail(e.getErrorCode(), e.getErrorMsg());
        }
        return AjaxResult.fail();
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseBody
    public AjaxResult<Object> handleNotFoundException(NoResourceFoundException e, HttpServletRequest request) {
        HandlerUtils.logException(e, request);
        return AjaxResult.fail(ExceptionEnum.NOT_FOUND_ERROR);
    }

}
