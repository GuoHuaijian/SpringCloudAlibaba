package com.sloth.boot.starter.sms.core;

import com.sloth.boot.starter.sms.config.SmsProperties;
import com.sloth.boot.starter.sms.model.SendResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 阿里云短信客户端（预留实现）测试。
 */
@DisplayName("AliyunSmsClient 测试")
class AliyunSmsClientTest {

    private final AliyunSmsClient client = new AliyunSmsClient(new SmsProperties());

    @Test
    @DisplayName("未接入实现发送时返回失败而非虚假成功")
    void sendReportsFailure() {
        SendResult result = client.send("13800000000", "SMS_12345", Map.of("code", "1234"));

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMsgId()).isNull();
        assertThat(result.getMessage()).contains("暂未接入");
    }

    @Test
    @DisplayName("未接入实现批量发送时返回失败而非虚假成功")
    void batchSendReportsFailure() {
        SendResult result = client.batchSend(List.of("13800000000"), "SMS_12345", Map.of());

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMsgId()).isNull();
        assertThat(result.getMessage()).contains("暂未接入");
    }
}
