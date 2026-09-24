package com.sloth.boot.starter.monitor.alarm;

import cn.hutool.core.util.StrUtil;
import com.sloth.boot.starter.monitor.config.MonitorProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

/**
 * Webhook 告警抽象基类。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractWebhookAlarmService implements AlarmService {

    protected final RestTemplate restTemplate;
    protected final MonitorProperties monitorProperties;

    /**
     * 发送简单告警。
     *
     * @param title   标题
     * @param content 内容
     */
    @Override
    public void send(String title, String content) {
        AlarmMessage message = new AlarmMessage();
        message.setTitle(title);
        message.setContent(content);
        message.setTime(LocalDateTime.now());
        send(message);
    }

    /**
     * 发送告警。
     *
     * @param message 告警消息
     */
    @Override
    public void send(AlarmMessage message) {
        if (message.getIp() == null) {
            message.setIp(resolveIp());
        }
        String webhook = monitorProperties.getAlarm().getWebhook();
        if (StrUtil.isBlank(webhook)) {
            log.warn("[Monitor] 未配置告警 webhook，忽略发送, title={}", message.getTitle());
            return;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            restTemplate.postForEntity(buildWebhookUrl(webhook, message),
                new HttpEntity<>(buildPayload(message), headers), String.class);
        } catch (RestClientException ex) {
            // 告警为旁路能力，失败不能影响主流程；Webhook URL 可能携带签名/token，
            // 异常信息可能回显完整 URL，故只记录异常类型与告警标题，不输出 URL 与异常报文
            log.error("[Monitor] 告警发送失败, title={}, errorType={}",
                message.getTitle(), ex.getClass().getSimpleName());
        }
    }

    protected String buildWebhookUrl(String webhook, AlarmMessage message) {
        return webhook;
    }

    protected abstract Object buildPayload(AlarmMessage message);

    private String resolveIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            return "unknown";
        }
    }
}
