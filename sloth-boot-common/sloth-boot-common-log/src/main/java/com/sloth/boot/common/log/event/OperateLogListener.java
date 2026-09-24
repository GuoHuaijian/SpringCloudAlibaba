package com.sloth.boot.common.log.event;

import com.sloth.boot.common.log.OperateLogHandler;
import com.sloth.boot.common.log.model.OperateLogDTO;
import com.sloth.boot.common.log.config.LogProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

/**
 * 操作日志监听器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class OperateLogListener {

    /**
     * 控制台打印错误信息的最大长度。
     */
    private static final int ERROR_MSG_MAX_LENGTH = 200;

    private final LogProperties logProperties;
    private final OperateLogHandler operateLogHandler;

    /**
     * 处理操作日志事件。
     * <p>
     * 控制台日志只输出元数据（不打印请求参数/响应结果等可能含敏感信息的完整负载）；
     * handler 异常被捕获记录，不会传播给业务调用方。
     *
     * @param event 操作日志事件
     */
    @EventListener
    public void handleOperateLogEvent(OperateLogEvent event) {
        OperateLogDTO dto = event.operateLog();
        if (logProperties.isPrintOperateLog()) {
            log.info("[Operate] traceId={}, module={}, type={}, method={}, url={} {}, operator={}({}), ip={}, cost={}ms, status={}, error={}",
                dto.getTraceId(), dto.getModule(), dto.getOperateType(), dto.getMethod(),
                dto.getRequestMethod(), dto.getRequestUrl(),
                dto.getOperatorName(), dto.getOperatorId(), dto.getOperatorIp(),
                dto.getCostTime(), dto.getStatus(), truncate(dto.getErrorMsg()));
        }
        try {
            operateLogHandler.handle(dto);
        } catch (Exception ex) {
            log.error("Operate log handle failed, method={}, url={}", dto.getMethod(), dto.getRequestUrl(), ex);
        }
    }

    /**
     * 截断错误信息，避免超长内容刷屏。
     *
     * @param errorMsg 错误信息
     * @return 截断后的错误信息
     */
    private String truncate(String errorMsg) {
        if (errorMsg == null || errorMsg.length() <= ERROR_MSG_MAX_LENGTH) {
            return errorMsg;
        }
        return errorMsg.substring(0, ERROR_MSG_MAX_LENGTH) + "...";
    }
}
