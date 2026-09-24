package com.sloth.boot.starter.rocketmq.consumer;

import com.sloth.boot.starter.rocketmq.config.RocketMQProperties;
import com.sloth.boot.starter.rocketmq.model.BaseMessage;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * 消息监听基类测试：重点覆盖幂等标记与消费失败的交互。
 */
@DisplayName("AbstractMessageListener 测试")
class AbstractMessageListenerTest {

    private static final String IDEMPOTENT_KEY = "sloth:mq:consume:msg-100";

    private final RocketMQProperties properties = new RocketMQProperties();
    private final StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    @SuppressWarnings("unchecked")
    private final ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    @SuppressWarnings("unchecked")
    private final ObjectProvider<StringRedisTemplate> provider = mock(ObjectProvider.class);
    private final RecordingListener listener = new RecordingListener(properties, provider);

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(provider.getIfAvailable()).thenReturn(stringRedisTemplate);
    }

    @Test
    @DisplayName("消费成功保留幂等标记")
    void keepsIdempotentMarkOnSuccess() {
        lenient().when(valueOperations.setIfAbsent(eq(IDEMPOTENT_KEY), eq("1"), any(Duration.class)))
            .thenReturn(true);

        listener.onMessage(messageExt());

        assertThat(listener.processed).isTrue();
        verify(stringRedisTemplate, never()).delete(anyString());
    }

    @Test
    @DisplayName("重复消息跳过业务处理")
    void skipsDuplicateMessage() {
        lenient().when(valueOperations.setIfAbsent(eq(IDEMPOTENT_KEY), eq("1"), any(Duration.class)))
            .thenReturn(false);

        listener.onMessage(messageExt());

        assertThat(listener.processed).isFalse();
        verify(stringRedisTemplate, never()).delete(anyString());
    }

    @Test
    @DisplayName("消费失败回滚幂等标记，保证重试不被吞掉")
    void rollsBackIdempotentMarkOnFailure() {
        lenient().when(valueOperations.setIfAbsent(eq(IDEMPOTENT_KEY), eq("1"), any(Duration.class)))
            .thenReturn(true);
        listener.failure = new IllegalStateException("业务处理失败");

        assertThatThrownBy(() -> listener.onMessage(messageExt()))
            .isInstanceOf(IllegalStateException.class);

        verify(stringRedisTemplate).delete(IDEMPOTENT_KEY);
    }

    @Test
    @DisplayName("无 Redis 时跳过幂等检查直接消费")
    void processesWithoutRedis() {
        lenient().when(provider.getIfAvailable()).thenReturn(null);

        listener.onMessage(messageExt());

        assertThat(listener.processed).isTrue();
        verifyNoInteractions(stringRedisTemplate);
    }

    @Test
    @DisplayName("幂等关闭时不访问 Redis")
    void doesNotTouchRedisWhenIdempotentDisabled() {
        properties.setIdempotentEnabled(false);

        listener.onMessage(messageExt());

        assertThat(listener.processed).isTrue();
        verifyNoInteractions(stringRedisTemplate);
    }

    private MessageExt messageExt() {
        MessageExt messageExt = new MessageExt();
        messageExt.setTopic("TEST_TOPIC");
        messageExt.setBody("{\"msgId\":\"msg-100\"}".getBytes(StandardCharsets.UTF_8));
        return messageExt;
    }

    /**
     * 记录型测试子类。
     */
    private static class RecordingListener extends AbstractMessageListener<BaseMessage> {

        private boolean processed;
        private RuntimeException failure;

        RecordingListener(RocketMQProperties properties, ObjectProvider<StringRedisTemplate> provider) {
            super(properties, provider);
        }

        @Override
        protected void onMessage(BaseMessage message) {
            processed = true;
            if (failure != null) {
                throw failure;
            }
        }
    }
}
