package com.sloth.boot.starter.monitor.config;

import com.sloth.boot.starter.monitor.metrics.BusinessMetrics;
import com.sloth.boot.starter.monitor.metrics.JvmMetricsConfig;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("MonitorAutoConfiguration 条件装配测试")
class MonitorAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner =
        new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(MonitorAutoConfiguration.class))
            .withBean(MeterRegistry.class, SimpleMeterRegistry::new);

    @Test
    @DisplayName("默认配置下注册核心 Bean")
    void registersCoreBeansByDefault() {
        contextRunner.run(context -> {
            assertThat(context.getBeansOfType(JvmMetricsConfig.class)).isNotEmpty();
            assertThat(context.getBeansOfType(BusinessMetrics.class)).isNotEmpty();
        });
    }

    @Test
    @DisplayName("sloth.monitor.enabled=false 时不注册任何 Bean")
    void disabledByProperty() {
        contextRunner.withPropertyValues("sloth.monitor.enabled=false").run(context -> {
            assertThat(context).doesNotHaveBean(JvmMetricsConfig.class);
            assertThat(context).doesNotHaveBean(BusinessMetrics.class);
        });
    }

    @Test
    @DisplayName("告警开启时注册 RestTemplate")
    void alarmEnabledRegistersRestTemplate() {
        contextRunner.withPropertyValues("sloth.monitor.alarm.enabled=true").run(context -> {
            assertThat(context.getBeansOfType(RestTemplate.class)).isNotEmpty();
        });
    }

    @Test
    @DisplayName("告警 RestTemplate 使用带超时的请求工厂")
    void restTemplateUsesTimeoutRequestFactory() {
        RestTemplate restTemplate = new MonitorAutoConfiguration().restTemplate();

        assertThat(restTemplate.getRequestFactory()).isInstanceOf(SimpleClientHttpRequestFactory.class);
    }

    @Test
    @DisplayName("用户自定义 RestTemplate 可覆盖默认")
    void customRestTemplateOverrides() {
        contextRunner.withPropertyValues("sloth.monitor.alarm.enabled=true")
            .withBean("customRestTemplate", RestTemplate.class, () -> mock(RestTemplate.class))
            .run(context -> {
                assertThat(context.getBeansOfType(RestTemplate.class)).isNotEmpty();
            });
    }

    @Test
    @DisplayName("默认不注册 Actuator 端点暴露后处理器")
    void exposurePostProcessorDisabledByDefault() {
        contextRunner.run(context ->
            assertThat(context).doesNotHaveBean("monitorActuatorExposurePostProcessor"));
    }

    @Test
    @DisplayName("sloth.monitor.expose-endpoints=true 时注册端点暴露后处理器")
    void exposurePostProcessorEnabledByProperty() {
        contextRunner.withPropertyValues("sloth.monitor.expose-endpoints=true").run(context ->
            assertThat(context).hasBean("monitorActuatorExposurePostProcessor"));
    }
}
