package com.sloth.boot.starter.gateway.config;

import com.sloth.boot.starter.gateway.filter.BlackListGlobalFilter;
import com.sloth.boot.starter.gateway.filter.HeaderForwardingFilter;
import com.sloth.boot.starter.gateway.filter.RequestLogGlobalFilter;
import com.sloth.boot.starter.gateway.filter.RetryGlobalFilter;
import com.sloth.boot.starter.gateway.filter.TraceIdGlobalFilter;
import com.sloth.boot.starter.gateway.handler.GatewayExceptionHandler;
import com.sloth.boot.starter.gateway.route.DynamicRouteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.gateway.route.InMemoryRouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.web.cors.reactive.CorsWebFilter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gateway 自动配置测试。
 * <p>
 * 通过 {@link ApplicationContextRunner} 验证条件装配：核心过滤器注册、
 * 属性开关（enabled / dynamic-route-enabled / retry.enabled）与用户 Bean 覆盖。
 */
@DisplayName("GatewayAutoConfiguration 测试")
class GatewayAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(GatewayAutoConfiguration.class))
        .withBean(RouteDefinitionWriter.class, InMemoryRouteDefinitionRepository::new);

    @Test
    @DisplayName("默认注册核心过滤器、异常处理器与动态路由")
    void defaultRegistersCoreComponents() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(GatewayProperties.class);
            assertThat(context).hasSingleBean(TraceIdGlobalFilter.class);
            assertThat(context).hasSingleBean(HeaderForwardingFilter.class);
            assertThat(context).hasSingleBean(RequestLogGlobalFilter.class);
            assertThat(context).hasSingleBean(BlackListGlobalFilter.class);
            assertThat(context).hasSingleBean(GatewayExceptionHandler.class);
            assertThat(context).hasSingleBean(DynamicRouteService.class);
            assertThat(context).hasSingleBean(CorsWebFilter.class);
            assertThat(context).doesNotHaveBean(RetryGlobalFilter.class);
        });
    }

    @Test
    @DisplayName("sloth.gateway.enabled=false 时不注册任何组件")
    void disabledByPropertySkipsRegistration() {
        contextRunner.withPropertyValues("sloth.gateway.enabled=false").run(context -> {
            assertThat(context).doesNotHaveBean(GatewayProperties.class);
            assertThat(context).doesNotHaveBean(TraceIdGlobalFilter.class);
            assertThat(context).doesNotHaveBean(GatewayExceptionHandler.class);
            assertThat(context).doesNotHaveBean(DynamicRouteService.class);
        });
    }

    @Test
    @DisplayName("动态路由可通过 dynamic-route-enabled 关闭")
    void dynamicRouteCanBeDisabled() {
        contextRunner.withPropertyValues("sloth.gateway.dynamic-route-enabled=false").run(context -> {
            assertThat(context).hasSingleBean(TraceIdGlobalFilter.class);
            assertThat(context).doesNotHaveBean(DynamicRouteService.class);
        });
    }

    @Test
    @DisplayName("重试过滤器仅在 retry.enabled=true 时注册")
    void retryFilterOnlyWhenEnabled() {
        contextRunner.withPropertyValues("sloth.gateway.retry.enabled=true")
            .run(context -> assertThat(context).hasSingleBean(RetryGlobalFilter.class));
    }

    @Test
    @DisplayName("用户自定义过滤器 Bean 优先生效")
    void userDefinedFilterWins() {
        contextRunner.withBean(TraceIdGlobalFilter.class, TraceIdGlobalFilter::new)
            .run(context -> assertThat(context).hasSingleBean(TraceIdGlobalFilter.class));
    }
}
