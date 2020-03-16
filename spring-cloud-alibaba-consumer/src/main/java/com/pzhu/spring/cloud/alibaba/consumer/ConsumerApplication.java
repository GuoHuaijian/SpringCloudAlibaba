package com.pzhu.spring.cloud.alibaba.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 服务消费者
 * @Author Guo Huaijian
 * @Date 2020/3/16 15:02
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.pzhu.spring.cloud.alibaba.consumer.service")
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

}
