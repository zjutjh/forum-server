package org.jh.forum.start.handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.start.models.AjaxResult;
import org.jh.forum.start.utils.HandlerUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 处理参数校验相关异常
 *
 * @author patrick_star
 * @date 2025/4/24
 */
@Order(10)
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ValidateExceptionHandler {

    /**
     * 参数校验错误拦截处理
     *
     * @param e 错误信息集合
     * @return 错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public AjaxResult<Object> validationBodyException(MethodArgumentNotValidException e, HttpServletRequest request) {
        HandlerUtils.logException(e, request);
        return AjaxResult.fail(ExceptionEnum.INVALID_PARAMETER);
    }

    /**
     * SQL执行失败错误（重复主键）拦截处理
     *
     * @param e 错误信息集合
     * @return 错误信息
     */

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseBody
    public AjaxResult<Object> validationBodyException(SQLIntegrityConstraintViolationException e, HttpServletRequest request) {
        HandlerUtils.logException(e, request);
        return AjaxResult.fail(ExceptionEnum.SERVER_ERROR);
    }

    /**
     * Json解析失败错误（字段填写错误或漏填必选值）拦截处理
     *
     * @param e 错误信息集合
     * @return 错误信息
     */
    @ExceptionHandler(JsonMappingException.class)
    @ResponseBody
    public AjaxResult<Object> validationBodyException(JsonMappingException e, HttpServletRequest request) {
        HandlerUtils.logException(e, request);
        return AjaxResult.fail(ExceptionEnum.INVALID_PARAMETER);
    }

    /**
     * Json格式错误拦截处理
     *
     * @param e 错误信息集合
     * @return 错误信息
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public AjaxResult<Object> validationBodyException(HttpMessageNotReadableException e, HttpServletRequest request) {
        HandlerUtils.logException(e, request);
        return AjaxResult.fail(ExceptionEnum.INVALID_PARAMETER);
    }
}
