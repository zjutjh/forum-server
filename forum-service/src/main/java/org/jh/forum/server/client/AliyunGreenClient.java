package org.jh.forum.server.client;

import cn.hutool.core.util.EnumUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.aliyun.green20220302.Client;
import com.aliyun.green20220302.models.TextModerationPlusRequest;
import com.aliyun.green20220302.models.TextModerationPlusResponse;
import com.aliyun.green20220302.models.TextModerationPlusResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.RiskLevelEnum;
import org.jh.forum.common.constants.TextModerationServiceEnum;
import org.jh.forum.common.exceptions.ModerationException;

/**
 * @author SugarMGP
 */
@Slf4j
@RequiredArgsConstructor
public class AliyunGreenClient {
    private final boolean enabled;
    private final Client client;

    @SneakyThrows
    public void checkText(String text, TextModerationServiceEnum service) {
        if (!enabled) {
            return;
        }

        JSONObject params = new JSONObject();
        params.put("content", text);

        TextModerationPlusRequest req = new TextModerationPlusRequest();
        req.setService(service.getServiceName());
        req.setServiceParameters(params.toJSONString());

        TextModerationPlusResponse resp = client.textModerationPlus(req);
        if (resp.getStatusCode() != 200) {
            throw new RuntimeException("阿里云内容审核 HTTP 请求失败，状态码：" + resp.getStatusCode());
        }

        TextModerationPlusResponseBody body = resp.getBody();
        if (body.getCode() != 200) {
            throw new RuntimeException("阿里云内容审核返回错误码：" + body.getCode() + ", msg: " + body.getMessage());
        }

        log.info("阿里云内容审核, params: {}, service: {}, response: {}", params.toJSONString(), service.getServiceName(), JSON.toJSONString(body));

        TextModerationPlusResponseBody.TextModerationPlusResponseBodyData data = body.getData();
        if (data == null || data.getResult() == null) {
            return;
        }

        RiskLevelEnum level = EnumUtil.getBy(RiskLevelEnum::getValue, data.getRiskLevel());
        if (level == RiskLevelEnum.HIGH) {
            throw new ModerationException(body.getRequestId(), data.getResult());
        }
    }
}
