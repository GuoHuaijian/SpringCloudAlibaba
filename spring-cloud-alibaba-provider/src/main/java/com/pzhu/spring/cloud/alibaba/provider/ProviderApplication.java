package com.pzhu.spring.cloud.alibaba.provider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 服务提供者
 * @Author Guo Huaijian
 * @Date 2020/3/16 15:02
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.pzhu.spring.cloud.alibaba.provider.dao")
public class ProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }
}
