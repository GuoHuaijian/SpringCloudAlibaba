package com.pzhu.spring.cloud.alibaba.rocketmq.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;

/**
 * 消息队列提供者
 * @Author Guo Huaijian
 * @Date 2020/3/16 15:02
 */
@SpringBootApplication
@EnableBinding({Source.class})
public class RocketMQProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(RocketMQProviderApplication.class, args);
    }

}
