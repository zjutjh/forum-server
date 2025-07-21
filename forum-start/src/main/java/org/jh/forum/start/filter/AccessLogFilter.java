package org.jh.forum.start.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

/**
 * @author SugarMGP
 */
@Slf4j
@Component
public class AccessLogFilter implements Filter {

    static {
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
    }

    private static AnsiColor getStatusColor(int status) {
        return switch (status / 100) {
            case 2 -> AnsiColor.GREEN;
            case 4 -> AnsiColor.YELLOW;
            case 5 -> AnsiColor.RED;
            default -> AnsiColor.DEFAULT;
        };
    }

    private static AnsiColor getMethodColor(String method) {
        return switch (method) {
            case "GET" -> AnsiColor.BLUE;
            case "POST" -> AnsiColor.CYAN;
            case "PUT" -> AnsiColor.YELLOW;
            case "DELETE" -> AnsiColor.RED;
            default -> AnsiColor.MAGENTA;
        };
    }

    private static String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        return ip == null ? request.getRemoteAddr() : ip;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest req) || !(response instanceof HttpServletResponse res)) {
            chain.doFilter(request, response);
            return;
        }

        Instant start = Instant.now();
        chain.doFilter(request, response);
        Instant end = Instant.now();
        long durationMs = Duration.between(start, end).toMillis();

        int status = res.getStatus();
        String method = req.getMethod();
        String uri = req.getRequestURI();
        String query = req.getQueryString();
        String ip = getRemoteAddr(req);

        // 彩色状态码
        String colorStatus = AnsiOutput.toString(
                getStatusColor(status),
                status,
                AnsiColor.DEFAULT
        );

        // 彩色方法
        String colorMethod = AnsiOutput.toString(
                getMethodColor(method),
                method,
                AnsiColor.DEFAULT
        );

        log.info("{} | {}ms | {} {} | IP: {}",
                colorStatus,
                durationMs,
                String.format("%-6s", colorMethod),
                uri + (query != null ? "?" + query : ""),
                ip
        );

    }
}