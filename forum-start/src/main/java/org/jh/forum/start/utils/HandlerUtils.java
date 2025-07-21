package org.jh.forum.start.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author MangoGovo
 */
@Slf4j
public class HandlerUtils {
    /**
     * 错误日志
     *
     * @param e       错误异常
     * @param request Http请求对象
     */
    public static void logException(Throwable e, HttpServletRequest request) {
        String query = JSON.toJSONString(request.getParameterMap());
        String logLine = AnsiOutput.toString(
                AnsiColor.YELLOW, "⚠️ ERROR",
                AnsiColor.DEFAULT, " | params = " + query
        );
        log.error(logLine, e);
    }
}
