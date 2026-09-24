package com.sloth.boot.starter.gateway.filter;

import com.sloth.boot.common.constant.HeaderConstant;
import com.sloth.boot.starter.gateway.config.GatewayProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 认证全局过滤器测试。
 */
@DisplayName("HeaderForwardingFilter 测试")
class HeaderForwardingFilterTest {

    private final GatewayProperties gatewayProperties = new GatewayProperties();

    @Test
    @DisplayName("客户端伪造的身份头被丢弃并覆盖为默认值")
    void spoofedIdentityHeadersAreStripped() {
        HeaderForwardingFilter filter = new HeaderForwardingFilter(gatewayProperties);
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/user")
                .header(HeaderConstant.TOKEN, "any-token")
                .header(HeaderConstant.USER_ID, "999")
                .header(HeaderConstant.USERNAME, "attacker")
                .header(HeaderConstant.INNER_CALL, "true")
        );
        AtomicReference<ServerWebExchange> forwarded = new AtomicReference<>();

        filter.filter(exchange, capturingChain(forwarded)).block();

        assertThat(forwarded.get()).isNotNull();
        assertThat(forwarded.get().getRequest().getHeaders().getFirst(HeaderConstant.USER_ID)).isEqualTo("0");
        assertThat(forwarded.get().getRequest().getHeaders().getFirst(HeaderConstant.USERNAME)).isEqualTo("gateway");
        assertThat(forwarded.get().getRequest().getHeaders().getFirst(HeaderConstant.INNER_CALL)).isEqualTo("false");
    }

    @Test
    @DisplayName("无 token 访问非白名单路径返回 401")
    void missingTokenRejected() {
        HeaderForwardingFilter filter = new HeaderForwardingFilter(gatewayProperties);
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/user").build());

        filter.filter(exchange, capturingChain(new AtomicReference<>())).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("白名单路径跳过 token 校验但仍清洗身份头")
    void whiteListPathSkipsTokenButStripsHeaders() {
        gatewayProperties.setWhiteList(Set.of("/public"));
        HeaderForwardingFilter filter = new HeaderForwardingFilter(gatewayProperties);
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/public/info")
                .header(HeaderConstant.USER_ID, "999")
                .build());
        AtomicReference<ServerWebExchange> forwarded = new AtomicReference<>();

        filter.filter(exchange, capturingChain(forwarded)).block();

        assertThat(forwarded.get()).isNotNull();
        assertThat(forwarded.get().getRequest().getHeaders().getFirst(HeaderConstant.USER_ID)).isEqualTo("0");
        assertThat(exchange.getResponse().getStatusCode()).isNotEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("租户头合法时被保留")
    void tenantHeaderPreserved() {
        HeaderForwardingFilter filter = new HeaderForwardingFilter(gatewayProperties);
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/user")
                .header(HeaderConstant.TOKEN, "any-token")
                .header(HeaderConstant.TENANT_ID, "tenant-a")
        );
        AtomicReference<ServerWebExchange> forwarded = new AtomicReference<>();

        filter.filter(exchange, capturingChain(forwarded)).block();

        assertThat(forwarded.get()).isNotNull();
        assertThat(forwarded.get().getRequest().getHeaders().getFirst(HeaderConstant.TENANT_ID)).isEqualTo("tenant-a");
    }

    private GatewayFilterChain capturingChain(AtomicReference<ServerWebExchange> captured) {
        return exchange -> {
            captured.set(exchange);
            return Mono.empty();
        };
    }
}
