package org.jh.forum.start.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.cube.CubeException;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.exceptions.ApiException;
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

    @ExceptionHandler(ApiException.class)
    @ResponseBody
    public AjaxResult<Object> handleAppException(ApiException e, HttpServletRequest request) {
        HandlerUtils.logException(e, request);
        return AjaxResult.fail(e.getErrorCode(), e.getErrorMsg());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseBody
    public AjaxResult<Object> handleNotFoundException(NoResourceFoundException e, HttpServletRequest request) {
        HandlerUtils.logException(e, request);
        return AjaxResult.fail(ExceptionEnum.NOT_FOUND_ERROR);
    }

    @ExceptionHandler(SaTokenException.class)
    @ResponseBody
    public AjaxResult<Object> handleNotLoginException(SaTokenException e, HttpServletRequest request) {
        HandlerUtils.logException(e, request);
        if (e instanceof NotLoginException) {
            return AjaxResult.fail(ExceptionEnum.NOT_LOGIN);
        }
        if (e instanceof NotRoleException) {
            return AjaxResult.fail(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }
        return AjaxResult.fail(ExceptionEnum.SERVER_ERROR);
    }

    @ExceptionHandler(CubeException.class)
    @ResponseBody
    public AjaxResult<Object> handleCubeException(CubeException e, HttpServletRequest request) {
        HandlerUtils.logException(e, request);
        if (e.getCode() == 200504) {
            return AjaxResult.fail(ExceptionEnum.FILE_NOT_PICTURE);
        }
        return AjaxResult.fail(ExceptionEnum.SERVER_ERROR);
    }
}
