package com.pzhu.spring.cloud.alibaba.rocketmq.consumer.receive;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

/**
 * @Author Guo Huaijian
 * @Date 2020/3/17 1:58
 */
@Service
public class ConsumerReceive {

    @StreamListener("input")
    public void receiveInput(String message) {
        System.out.println("Receive input: " + message);
    }
}
