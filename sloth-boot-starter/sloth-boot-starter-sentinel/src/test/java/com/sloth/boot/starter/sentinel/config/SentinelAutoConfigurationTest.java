package com.sloth.boot.starter.sentinel.config;

import com.sloth.boot.starter.sentinel.datasource.NacosDataSourceConfig;
import com.sloth.boot.starter.sentinel.handler.GlobalBlockHandler;
import com.sloth.boot.starter.sentinel.handler.SentinelBlockExceptionHandler;
import com.sloth.boot.starter.sentinel.health.SentinelHealthIndicator;
import com.sloth.boot.starter.sentinel.metrics.SentinelMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Sentinel 自动配置测试。
 * <p>
 * 验证条件装配：默认核心 Bean、属性开关、MeterRegistry 条件与 Servlet Web 环境下的
 * Block 异常处理器注册。
 */
@DisplayName("SentinelAutoConfiguration 测试")
class SentinelAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(SentinelAutoConfiguration.class));

    @Test
    @DisplayName("默认注册阻断处理器、Nacos 数据源配置与健康指示器")
    void defaultRegistersCoreBeans() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(GlobalBlockHandler.class);
            assertThat(context).hasSingleBean(NacosDataSourceConfig.class);
            assertThat(context).hasSingleBean(SentinelHealthIndicator.class);
            assertThat(context).hasSingleBean(SentinelProperties.class);
            assertThat(context).doesNotHaveBean(SentinelMetrics.class);
            assertThat(context).doesNotHaveBean(SentinelBlockExceptionHandler.class);
        });
    }

    @Test
    @DisplayName("存在 MeterRegistry 时注册 SentinelMetrics")
    void metricsRegisteredWhenMeterRegistryPresent() {
        contextRunner.withBean(MeterRegistry.class, SimpleMeterRegistry::new)
            .run(context -> assertThat(context).hasSingleBean(SentinelMetrics.class));
    }

    @Test
    @DisplayName("sloth.sentinel.enabled=false 时不注册任何组件")
    void disabledByPropertySkipsRegistration() {
        contextRunner.withPropertyValues("sloth.sentinel.enabled=false").run(context -> {
            assertThat(context).doesNotHaveBean(GlobalBlockHandler.class);
            assertThat(context).doesNotHaveBean(NacosDataSourceConfig.class);
            assertThat(context).doesNotHaveBean(SentinelHealthIndicator.class);
        });
    }

    @Test
    @DisplayName("Servlet Web 环境下注册 Block 异常处理器")
    void servletWebRegistersBlockExceptionHandler() {
        new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SentinelAutoConfiguration.class))
            .run(context -> {
                assertThat(context).hasSingleBean(SentinelBlockExceptionHandler.class);
                assertThat(context).hasSingleBean(GlobalBlockHandler.class);
            });
    }
}
