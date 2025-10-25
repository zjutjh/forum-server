package org.jh.forum.server.client;

import cn.hutool.core.util.EnumUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.aliyun.green20220302.Client;
import com.aliyun.green20220302.models.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.constants.RiskLevelEnum;
import org.jh.forum.common.constants.TextModerationServiceEnum;
import org.jh.forum.common.dto.response.ModerationResultResponse;
import org.jh.forum.common.exceptions.ModerationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

        log.info("阿里云文本审核, content: {}, service: {}, response: {}", text, service.getServiceName(), JSON.toJSONString(body));

        TextModerationPlusResponseBody.TextModerationPlusResponseBodyData data = body.getData();
        if (Objects.isNull(data) || Objects.isNull(data.getResult())) {
            return;
        }

        RiskLevelEnum level = EnumUtil.getBy(RiskLevelEnum::getValue, data.getRiskLevel());
        if (RiskLevelEnum.HIGH.equals(level)) {
            List<ModerationResultResponse.Label> labels = data.getResult().stream()
                    .map(result ->
                            ModerationResultResponse.Label.builder()
                                    .description(result.getDescription())
                                    .keywords(result.getRiskWords())
                                    .build()
                    ).toList();
            throw new ModerationException(body.getRequestId(), labels);
        }
    }

    @SneakyThrows
    public void checkImage(String imageUrl) {
        if (!enabled) {
            return;
        }

        Map<String, String> serviceParams = new HashMap<>();
        serviceParams.put("imageUrl", imageUrl);

        ImageModerationRequest req = new ImageModerationRequest();
        req.setService("baselineCheck_pro");
        req.setServiceParameters(JSON.toJSONString(serviceParams));

        ImageModerationResponse resp = client.imageModeration(req);
        if (resp.getStatusCode() != 200) {
            throw new RuntimeException("阿里云图片审核 HTTP 请求失败，状态码：" + resp.getStatusCode());
        }

        ImageModerationResponseBody body = resp.getBody();
        if (body.getCode() != 200) {
            throw new RuntimeException("阿里云图片审核返回错误码：" + body.getCode() + ", msg: " + body.getMsg());
        }

        log.info("阿里云图片审核, imageUrl: {}, response: {}", imageUrl, JSON.toJSONString(body));

        ImageModerationResponseBody.ImageModerationResponseBodyData data = body.getData();
        if (Objects.isNull(data) || Objects.isNull(data.getResult())) {
            return;
        }

        RiskLevelEnum level = EnumUtil.getBy(RiskLevelEnum::getValue, data.getRiskLevel());
        if (RiskLevelEnum.HIGH.equals(level)) {
            List<ModerationResultResponse.Label> labels = data.getResult().stream()
                    .map(result ->
                            ModerationResultResponse.Label.builder()
                                    .description(result.getDescription())
                                    .build()
                    ).toList();
            throw new ModerationException(body.getRequestId(), labels);
        }
    }
}
