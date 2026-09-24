package com.sloth.boot.starter.sms.core;

import com.sloth.boot.starter.sms.config.SmsProperties;
import com.sloth.boot.starter.sms.model.SendResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 阿里云短信客户端。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class AliyunSmsClient implements SmsClient {

    private final SmsProperties smsProperties;

    /**
     * 发送短信。
     *
     * @param phone        手机号
     * @param templateCode 模板编码
     * @param params       模板参数
     * @return 发送结果
     */
    @Override
    public SendResult send(String phone, String templateCode, Map<String, String> params) {
        log.warn("[SMS] 阿里云短信客户端当前为预留实现, phone={}, signName={}, templateCode={}, regionId={}",
            phone, smsProperties.getSignName(), templateCode, smsProperties.getRegionId());
        return SendResult.builder()
            .success(false)
            .message("阿里云短信客户端暂未接入")
            .build();
    }

    /**
     * 批量发送短信。
     *
     * @param phones       手机号列表
     * @param templateCode 模板编码
     * @param params       模板参数
     * @return 发送结果
     */
    @Override
    public SendResult batchSend(List<String> phones, String templateCode, Map<String, String> params) {
        log.warn("[SMS] 阿里云批量短信客户端当前为预留实现, phones={}, templateCode={}", phones, templateCode);
        return SendResult.builder()
            .success(false)
            .message("阿里云短信客户端暂未接入")
            .build();
    }
}
