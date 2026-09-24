package com.sloth.boot.starter.rocketmq.consumer;

import com.sloth.boot.common.constant.HeaderConstant;
import com.sloth.boot.common.util.JsonUtil;
import com.sloth.boot.starter.rocketmq.config.RocketMQProperties;
import com.sloth.boot.starter.rocketmq.interceptor.TraceMessageInterceptor;
import com.sloth.boot.starter.rocketmq.model.BaseMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 消息监听抽象基类。
 *
 * @param <T> 消息类型
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractMessageListener<T extends BaseMessage> implements RocketMQListener<MessageExt> {

    private final RocketMQProperties mqProperties;
    private final ObjectProvider<StringRedisTemplate> stringRedisTemplateProvider;

    /**
     * 构造函数。
     *
     * @param mqProperties                MQ 配置
     * @param stringRedisTemplateProvider Redis 模板提供者
     */
    protected AbstractMessageListener(RocketMQProperties mqProperties,
                                      ObjectProvider<StringRedisTemplate> stringRedisTemplateProvider) {
        this.mqProperties = mqProperties;
        this.stringRedisTemplateProvider = stringRedisTemplateProvider;
    }

    /**
     * RocketMQ 回调入口。
     *
     * @param messageExt 原始消息
     */
    @Override
    public final void onMessage(MessageExt messageExt) {
        long startTime = System.currentTimeMillis();
        TraceMessageInterceptor.restoreTrace(messageExt);
        T message = null;
        boolean markedByCurrentConsumer = false;
        try {
            message = parseMessage(messageExt);
            if (needIdempotentCheck(message)) {
                IdempotentMark mark = markConsumed(message);
                if (mark == IdempotentMark.DUPLICATE) {
                    log.warn("[MQ] RocketMQ 消息重复消费, topic={}, msgId={}",
                        messageExt.getTopic(), message.getMsgId());
                    return;
                }
                markedByCurrentConsumer = mark == IdempotentMark.MARKED;
            }
            onMessage(message);
            log.info("[MQ] RocketMQ 消息消费成功, topic={}, msgId={}, cost={}ms",
                messageExt.getTopic(), message.getMsgId(), System.currentTimeMillis() - startTime);
        } catch (Exception ex) {
            if (markedByCurrentConsumer && message != null) {
                rollbackIdempotentMark(message);
            }
            int reconsumeTimes = messageExt.getReconsumeTimes();
            log.error("[MQ] RocketMQ 消息消费失败, topic={}, msgId={}, retryTimes={}, maxRetry={}, cost={}ms",
                messageExt.getTopic(),
                messageExt.getMsgId(),
                reconsumeTimes,
                mqProperties.getMaxRetry(),
                System.currentTimeMillis() - startTime,
                ex);
            if (reconsumeTimes >= mqProperties.getMaxRetry()) {
                log.error("[MQ] RocketMQ 消息达到最大重试阈值, topic={}, msgId={}, retryTimes={}",
                    messageExt.getTopic(), messageExt.getMsgId(), reconsumeTimes);
            }
            throw ex;
        } finally {
            TraceMessageInterceptor.clearTrace();
        }
    }

    /**
     * 业务模板方法。
     *
     * @param message 业务消息
     */
    protected abstract void onMessage(T message);

    private T parseMessage(MessageExt messageExt) {
        String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        T message = JsonUtil.parseObject(body, resolveMessageClass());
        if (message.getMsgId() == null) {
            message.setMsgId(messageExt.getMsgId());
        }
        if (message.getTraceId() == null || message.getTraceId().isBlank()) {
            message.setTraceId(messageExt.getUserProperty(HeaderConstant.TRACE_ID));
        }
        return message;
    }

    @SuppressWarnings("unchecked")
    private Class<T> resolveMessageClass() {
        Class<?> currentClass = getClass();
        Type genericSuperclass = currentClass.getGenericSuperclass();
        while (!(genericSuperclass instanceof ParameterizedType) && currentClass.getSuperclass() != null) {
            currentClass = currentClass.getSuperclass();
            genericSuperclass = currentClass.getGenericSuperclass();
        }
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            return (Class<T>) parameterizedType.getActualTypeArguments()[0];
        }
        return (Class<T>) BaseMessage.class;
    }

    private boolean needIdempotentCheck(T message) {
        return mqProperties.isIdempotentEnabled() && message != null && message.getMsgId() != null;
    }

    private IdempotentMark markConsumed(T message) {
        StringRedisTemplate stringRedisTemplate = stringRedisTemplateProvider.getIfAvailable();
        if (stringRedisTemplate == null) {
            return IdempotentMark.NOT_MARKED;
        }
        String key = mqProperties.getConsumeIdempotentKeyPrefix() + message.getMsgId();
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofDays(1));
        return Boolean.TRUE.equals(success) ? IdempotentMark.MARKED : IdempotentMark.DUPLICATE;
    }

    /**
     * 消费失败时回滚幂等标记，保证消息重试时能被重新处理。
     *
     * @param message 业务消息
     */
    private void rollbackIdempotentMark(T message) {
        StringRedisTemplate stringRedisTemplate = stringRedisTemplateProvider.getIfAvailable();
        if (stringRedisTemplate == null) {
            return;
        }
        try {
            stringRedisTemplate.delete(mqProperties.getConsumeIdempotentKeyPrefix() + message.getMsgId());
            log.info("[MQ] 消费失败已回滚幂等标记, msgId={}", message.getMsgId());
        } catch (RuntimeException ex) {
            log.warn("[MQ] 回滚幂等标记失败, msgId={}", message.getMsgId(), ex);
        }
    }

    /**
     * 幂等标记结果。
     */
    private enum IdempotentMark {

        /** 本次消费成功写入标记。 */
        MARKED,

        /** 标记已存在，消息重复。 */
        DUPLICATE,

        /** 无 Redis 可用，未写入标记。 */
        NOT_MARKED
    }
}
