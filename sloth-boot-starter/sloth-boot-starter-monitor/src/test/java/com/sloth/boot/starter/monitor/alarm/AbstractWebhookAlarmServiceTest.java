package com.sloth.boot.starter.monitor.alarm;

import com.sloth.boot.starter.monitor.config.MonitorProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Webhook 告警基类测试。
 */
@DisplayName("AbstractWebhookAlarmService 测试")
class AbstractWebhookAlarmServiceTest {

    private final RestTemplate restTemplate = mock(RestTemplate.class);
    private final MonitorProperties monitorProperties = new MonitorProperties();
    private final TestWebhookAlarmService alarmService =
        new TestWebhookAlarmService(restTemplate, monitorProperties);

    @Test
    @DisplayName("未配置 webhook 时不发起 HTTP 调用")
    void doesNothingWithoutWebhook() {
        assertThatCode(() -> alarmService.send("标题", "内容"))
            .doesNotThrowAnyException();
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("webhook 调用失败时记录日志而不向调用方抛出")
    void swallowsSendFailure() {
        monitorProperties.getAlarm().setWebhook("https://example.invalid/webhook");
        doThrow(new ResourceAccessException("connection refused"))
            .when(restTemplate)
            .postForEntity(anyString(), any(), eq(String.class));

        assertThatCode(() -> alarmService.send("标题", "内容"))
            .doesNotThrowAnyException();
    }

    /**
     * 记录型测试子类。
     */
    private static class TestWebhookAlarmService extends AbstractWebhookAlarmService {

        TestWebhookAlarmService(RestTemplate restTemplate, MonitorProperties monitorProperties) {
            super(restTemplate, monitorProperties);
        }

        @Override
        protected Object buildPayload(AlarmMessage message) {
            return Map.of("title", String.valueOf(message.getTitle()));
        }
    }
}
