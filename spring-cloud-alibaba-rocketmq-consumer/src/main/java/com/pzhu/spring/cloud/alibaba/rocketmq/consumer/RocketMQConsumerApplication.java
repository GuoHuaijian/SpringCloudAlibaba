package com.pzhu.spring.cloud.alibaba.rocketmq.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;

/**
 * 消息队列消费者
 * @Author Guo Huaijian
 * @Date 2020/3/17 1:59
 */
@SpringBootApplication
@EnableBinding({Sink.class})
public class RocketMQConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RocketMQConsumerApplication.class, args);
    }

}
