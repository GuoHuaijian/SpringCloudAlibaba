package com.sloth.boot.starter.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * Redis 序列化配置。
 * <p>
 * 注：Spring Data Redis 的 {@link GenericJackson2JsonRedisSerializer} 仍使用 Jackson 2.x ObjectMapper。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class RedisSerializerConfig {

    @Bean
    @ConditionalOnMissingBean
    public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer(RedisProperties redisProperties) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        if (redisProperties.isEnableTypeInfo()) {
            // 默认类型信息开启时仅允许安全的基础类型与本框架包前缀，防止反序列化 gadget 注入
            objectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                    .allowIfSubType("java.lang.")
                    .allowIfSubType("java.util.")
                    .allowIfSubType("java.time.")
                    .allowIfSubType("com.sloth.")
                    .build(),
                ObjectMapper.DefaultTyping.NON_FINAL);
        }
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
}
