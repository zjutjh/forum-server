package org.jh.forum.common.filters;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;

/**
 * Dubbo 请求全局过滤器
 * @author Patrick_Star
 * @date 2025/4/6
 */
@Service
@Slf4j
@Activate(group = PROVIDER)
public class DubboRequestFilter implements Filter {

    private final Map<String, Method> methodCache = new ConcurrentHashMap<>();

    private static final String LOG_DELIMITER = "|";

    private static final Integer MAX_ARGS_LENGTH = 1024;

    private static String ipHost;

    static {
        try {
            ipHost = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("getHostAddress error", e);
            ipHost = "";
        }
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String serviceName = invocation.getServiceName();
        String methodName = invocation.getMethodName();
        // 转成 jsonString 来避免后续执行过程中因修改入参而给排查问题带来可能的误导
        String jsonArgs = JSON.toJSONString(invocation.getArguments());
        Long startTime = System.currentTimeMillis();
        Result result = invoker.invoke(invocation);
        Result appResponse = ((AsyncRpcResult) result).getAppResponse();
        Long rt = System.currentTimeMillis() - startTime;
        boolean hasErr = result.hasException();

        String jsonResp = JSON.toJSONString(appResponse.getValue());
        String invokeLog = buildInvokeLog(serviceName, methodName, jsonArgs, rt, jsonResp);
        if (hasErr) {
            log.error("[Dubbo Request] {}", invokeLog);
        } else {
            log.info("[Dubbo Request] {}", invokeLog);
        }
        if (hasErr) {
            logError(serviceName, methodName, jsonArgs, result.getException());
        }
        return result;
    }

    private static void logError(String serviceName, String methodName, String jsonArgs, Throwable e) {
        String sb = ipHost +
                LOG_DELIMITER +
                serviceName +
                LOG_DELIMITER +
                methodName +
                LOG_DELIMITER +
                (jsonArgs.length() > MAX_ARGS_LENGTH ? jsonArgs.substring(0, MAX_ARGS_LENGTH) : jsonArgs);
        log.error("[Dubbo Request error] {}", sb, e);
    }

    private String buildInvokeLog(String serviceName, String methodName, String jsonArgs, Long rt, String jsonResp) {
        return ipHost +
                LOG_DELIMITER +
                serviceName +
                LOG_DELIMITER +
                methodName +
                LOG_DELIMITER +
                (jsonArgs.length() > MAX_ARGS_LENGTH ? jsonArgs.substring(0, MAX_ARGS_LENGTH) : jsonArgs) +
                LOG_DELIMITER +
                (jsonResp.length() > MAX_ARGS_LENGTH ? jsonResp.substring(0, MAX_ARGS_LENGTH) : jsonResp) +
                LOG_DELIMITER +
                rt;
    }
}
