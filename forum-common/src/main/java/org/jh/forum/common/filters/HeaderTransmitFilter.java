package org.jh.forum.common.filters;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;

/**
 * @author MangoGovo
 */
@Slf4j
@Activate(group = {CONSUMER})
public class HeaderTransmitFilter implements Filter {

    /**
     * 需要透传到Dubbo的请求头
     */
    private final List<String> HEADER_LIST = List.of("X-JH-Operator");

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return invoker.invoke(invocation);
        }
        for (String header : HEADER_LIST) {
            HttpServletRequest request = attrs.getRequest();
            String value = request.getHeader(header);
            if (value != null) {
                RpcContext.getClientAttachment().setAttachment(header, value);
            }
        }
        return invoker.invoke(invocation);
    }
}
