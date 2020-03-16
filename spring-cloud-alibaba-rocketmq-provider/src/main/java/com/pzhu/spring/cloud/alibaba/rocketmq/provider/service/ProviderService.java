package com.pzhu.spring.cloud.alibaba.rocketmq.provider.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * @Author Guo Huaijian
 * @Date 2020/3/17 1:52
 */
@Service
public class ProviderService implements CommandLineRunner {

    @Autowired
    private MessageChannel output;

    @Autowired
    private ProviderService providerService;

    public void send(String message) {
        output.send(MessageBuilder.withPayload(message).build());
    }

    /**
     * 实现了 CommandLineRunner 接口，只是为了 Spring Boot 启动时执行任务，不必特别在意
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        providerService.send("Hello RocketMQ");
    }
}
