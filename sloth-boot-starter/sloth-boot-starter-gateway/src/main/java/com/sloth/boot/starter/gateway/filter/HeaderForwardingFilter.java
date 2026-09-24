package com.sloth.boot.starter.gateway.filter;

import com.sloth.boot.common.constant.HeaderConstant;
import com.sloth.boot.starter.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 认证全局过滤器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class HeaderForwardingFilter implements GlobalFilter, Ordered {

    private static final String DEFAULT_INNER_CALL_VALUE = "false";
    private static final String DEFAULT_USER_ID = "0";
    private static final String DEFAULT_USERNAME = "gateway";

    private final GatewayProperties gatewayProperties;

    /**
     * 构造函数。
     *
     * @param gatewayProperties Gateway 配置
     */
    public HeaderForwardingFilter(GatewayProperties gatewayProperties) {
        this.gatewayProperties = gatewayProperties;
    }

    /**
     * 执行认证与用户透传。
     * <p>
     * 客户端传入的 {@code X-User-Id}/{@code X-Username}/{@code X-Inner-Call} 身份头一律丢弃并由网关
     * 覆盖写入，防止身份伪造；白名单路径跳过 token 校验，但同样执行身份头清洗。
     *
     * @param exchange 请求上下文
     * @param chain    过滤器链
     * @return Mono
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        boolean whiteListed = isWhiteList(path);
        if (!whiteListed) {
            String token = exchange.getRequest().getHeaders().getFirst(HeaderConstant.TOKEN);
            if (token == null || token.isBlank()) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }
        ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
        // 丢弃入站身份头，防止客户端伪造身份
        builder.headers(headers -> {
            headers.remove(HeaderConstant.USER_ID);
            headers.remove(HeaderConstant.USERNAME);
            headers.remove(HeaderConstant.INNER_CALL);
        });
        builder.header(HeaderConstant.INNER_CALL, DEFAULT_INNER_CALL_VALUE);
        builder.header(HeaderConstant.USER_ID, DEFAULT_USER_ID);
        builder.header(HeaderConstant.USERNAME, DEFAULT_USERNAME);
        String tenantId = exchange.getRequest().getHeaders().getFirst(HeaderConstant.TENANT_ID);
        if (tenantId != null && !tenantId.isBlank()) {
            builder.header(HeaderConstant.TENANT_ID, tenantId);
        }
        return chain.filter(exchange.mutate().request(builder.build()).build());
    }

    private boolean isWhiteList(String path) {
        return gatewayProperties.getWhiteList().stream().anyMatch(path::startsWith);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
