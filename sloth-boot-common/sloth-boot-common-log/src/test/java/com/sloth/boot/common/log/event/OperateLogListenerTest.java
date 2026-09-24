package com.sloth.boot.common.log.event;

import com.sloth.boot.common.log.OperateLogHandler;
import com.sloth.boot.common.log.config.LogProperties;
import com.sloth.boot.common.log.model.OperateLogDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * 操作日志监听器测试。
 */
@DisplayName("OperateLogListener 测试")
class OperateLogListenerTest {

    @Test
    @DisplayName("handler 抛出异常时不影响调用方")
    void handlerExceptionDoesNotPropagate() {
        OperateLogHandler failingHandler = dto -> {
            throw new IllegalStateException("persist failed");
        };
        OperateLogListener listener = new OperateLogListener(new LogProperties(), failingHandler);
        OperateLogEvent event = new OperateLogEvent(buildDto());

        assertThatCode(() -> listener.handleOperateLogEvent(event)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("handler 正常执行时被调用")
    void handlerInvoked() {
        AtomicBoolean invoked = new AtomicBoolean(false);
        OperateLogHandler handler = dto -> invoked.set(true);
        OperateLogListener listener = new OperateLogListener(new LogProperties(), handler);

        listener.handleOperateLogEvent(new OperateLogEvent(buildDto()));

        assertThat(invoked).isTrue();
    }

    @Test
    @DisplayName("printOperateLog=false 时仍执行 handler")
    void handlerInvokedWhenPrintDisabled() {
        AtomicBoolean invoked = new AtomicBoolean(false);
        LogProperties logProperties = new LogProperties();
        logProperties.setPrintOperateLog(false);
        OperateLogListener listener = new OperateLogListener(logProperties, dto -> invoked.set(true));

        listener.handleOperateLogEvent(new OperateLogEvent(buildDto()));

        assertThat(invoked).isTrue();
    }

    private OperateLogDTO buildDto() {
        OperateLogDTO dto = new OperateLogDTO();
        dto.setModule("test");
        dto.setMethod("TestService#save");
        dto.setRequestUrl("/api/test");
        dto.setRequestMethod("POST");
        dto.setRequestParams("{\"password\":\"secret\"}");
        dto.setResponseResult("{\"data\":1}");
        dto.setStatus(0);
        return dto;
    }
}
