package com.sloth.boot.starter.monitor.config;

import com.sloth.boot.starter.monitor.alarm.AlarmService;
import com.sloth.boot.starter.monitor.alarm.DingTalkAlarmService;
import com.sloth.boot.starter.monitor.alarm.WeChatAlarmService;
import com.sloth.boot.starter.monitor.collector.SystemResourceCollector;
import com.sloth.boot.starter.monitor.endpoint.InfoEndpoint;
import com.sloth.boot.starter.monitor.endpoint.JvmInfoEndpoint;
import com.sloth.boot.starter.monitor.endpoint.MetricsSummaryEndpoint;
import com.sloth.boot.starter.monitor.endpoint.SystemResourceEndpoint;
import com.sloth.boot.starter.monitor.health.NacosHealthIndicator;
import com.sloth.boot.starter.monitor.metrics.BusinessMetrics;
import com.sloth.boot.starter.monitor.event.SlowOperationEventListener;
import com.sloth.boot.starter.monitor.metrics.HttpMetricsFilter;
import com.sloth.boot.starter.monitor.metrics.JvmMetricsConfig;
import com.sloth.boot.starter.monitor.service.JvmInfoService;
import com.sloth.boot.starter.monitor.service.MetricsSummaryService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 监控自动配置。
 * <p>
 * 注册 {@link RestTemplate}、{@link AlarmService}、{@link NacosHealthIndicator}、
 * {@link HttpMetricsFilter}、{@link SlowOperationEventListener}、{@link JvmMetricsConfig}、
 * {@link InfoEndpoint}、{@link JvmInfoEndpoint}、{@link MetricsSummaryEndpoint}、
 * {@link SystemResourceEndpoint}、{@link BusinessMetrics}、{@link JvmInfoService}、
 * {@link MetricsSummaryService}、{@link SystemResourceCollector}，支持条件装配。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(MonitorProperties.class)
@ConditionalOnProperty(prefix = "sloth.monitor", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MonitorAutoConfiguration {

    /**
     * 注册 RestTemplate。
     *
     * @return RestTemplate
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sloth.monitor.alarm", name = "enabled", havingValue = "true")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 注册告警服务。
     *
     * @param restTemplate      RestTemplate
     * @param monitorProperties 监控配置
     * @return 告警服务
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sloth.monitor.alarm", name = "enabled", havingValue = "true")
    public AlarmService alarmService(RestTemplate restTemplate, MonitorProperties monitorProperties) {
        String type = monitorProperties.getAlarm().getType();
        if ("wechat".equalsIgnoreCase(type)) {
            return new WeChatAlarmService(restTemplate, monitorProperties);
        }
        return new DingTalkAlarmService(restTemplate, monitorProperties);
    }

    /**
     * 注册 Nacos 健康检查器。
     *
     * @param environment Spring 环境
     * @return 健康检查器
     */
    @Bean
    @ConditionalOnClass(name = "com.alibaba.cloud.nacos.NacosServiceManager")
    @ConditionalOnMissingBean(name = "nacosHealthIndicator")
    public NacosHealthIndicator nacosHealthIndicator(Environment environment, MonitorProperties monitorProperties) {
        return new NacosHealthIndicator(environment, monitorProperties);
    }

    /**
     * 注册 HTTP 指标过滤器。
     *
     * @param meterRegistry        指标注册中心
     * @param monitorProperties    配置
     * @param alarmServiceProvider 告警服务提供者
     * @return 过滤器注册器
     */
    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass(MeterRegistry.class)
    @ConditionalOnBean(MeterRegistry.class)
    @ConditionalOnMissingBean
    public FilterRegistrationBean<HttpMetricsFilter> httpMetricsFilter(MeterRegistry meterRegistry,
                                                                       MonitorProperties monitorProperties,
                                                                       ObjectProvider<AlarmService> alarmServiceProvider) {
        FilterRegistrationBean<HttpMetricsFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new HttpMetricsFilter(meterRegistry, monitorProperties, alarmServiceProvider.getIfAvailable()));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
        return registrationBean;
    }

    /**
     * 注册慢操作事件监听器。
     *
     * @param meterRegistry        指标注册中心
     * @param alarmServiceProvider 告警服务提供者
     * @return 慢操作事件监听器
     */
    @Bean
    @ConditionalOnClass(name = {"com.sloth.boot.common.log.event.SlowOperationEvent", "io.micrometer.core.instrument.MeterRegistry"})
    @ConditionalOnBean(MeterRegistry.class)
    @ConditionalOnMissingBean
    public SlowOperationEventListener slowOperationEventListener(MeterRegistry meterRegistry,
                                                                  ObjectProvider<AlarmService> alarmServiceProvider) {
        return new SlowOperationEventListener(meterRegistry, alarmServiceProvider);
    }

    /**
     * 注册 JVM 指标。
     *
     * @param meterRegistry 指标注册中心
     * @return JVM 指标配置
     */
    @Bean
    @ConditionalOnClass(MeterRegistry.class)
    @ConditionalOnBean(MeterRegistry.class)
    @ConditionalOnMissingBean
    public JvmMetricsConfig jvmMetricsConfig(MeterRegistry meterRegistry) {
        return new JvmMetricsConfig(meterRegistry);
    }

    /**
     * 注册应用信息端点。
     *
     * @param environment Spring 环境
     * @return 信息端点
     */
    @Bean
    @ConditionalOnClass(Endpoint.class)
    @ConditionalOnMissingBean
    public InfoEndpoint appInfoEndpoint(Environment environment) {
        return new InfoEndpoint(environment);
    }

    /**
     * 注册 JVM 信息端点。
     *
     * @param jvmInfoService JVM 信息采集服务
     * @return JVM 信息端点
     */
    @Bean
    @ConditionalOnClass(Endpoint.class)
    @ConditionalOnMissingBean
    public JvmInfoEndpoint jvmInfoEndpoint(JvmInfoService jvmInfoService) {
        return new JvmInfoEndpoint(jvmInfoService);
    }

    /**
     * 注册指标汇总端点。
     *
     * @param metricsSummaryService 指标汇总服务
     * @return 指标汇总端点
     */
    @Bean
    @ConditionalOnClass(Endpoint.class)
    @ConditionalOnMissingBean
    public MetricsSummaryEndpoint metricsSummaryEndpoint(MetricsSummaryService metricsSummaryService) {
        return new MetricsSummaryEndpoint(metricsSummaryService);
    }

    /**
     * 注册系统资源端点。
     *
     * @return 系统资源端点
     */
    @Bean
    @ConditionalOnClass(Endpoint.class)
    @ConditionalOnMissingBean
    public SystemResourceEndpoint systemResourceEndpoint() {
        return new SystemResourceEndpoint();
    }

    /**
     * 自定义 Actuator 端点暴露配置。
     *
     * @return Bean 后处理器
     */
    // ==================== 新增特性 ====================

    /**
     * 注册业务指标工具类。
     *
     * @param meterRegistry 指标注册中心
     * @return 业务指标工具
     */
    @Bean
    @ConditionalOnClass(MeterRegistry.class)
    @ConditionalOnBean(MeterRegistry.class)
    @ConditionalOnMissingBean
    public BusinessMetrics businessMetrics(MeterRegistry meterRegistry) {
        return new BusinessMetrics(meterRegistry);
    }

    /**
     * 注册 JVM 信息采集服务。
     *
     * @return JVM 信息采集服务
     */
    @Bean
    @ConditionalOnMissingBean
    public JvmInfoService jvmInfoService() {
        return new JvmInfoService();
    }

    /**
     * 注册指标汇总服务。
     *
     * @param meterRegistry 指标注册中心
     * @return 指标汇总服务
     */
    @Bean
    @ConditionalOnClass(MeterRegistry.class)
    @ConditionalOnBean(MeterRegistry.class)
    @ConditionalOnMissingBean
    public MetricsSummaryService metricsSummaryService(MeterRegistry meterRegistry) {
        return new MetricsSummaryService(meterRegistry);
    }

    /**
     * 自定义 Actuator 端点暴露配置。
     * <p>
     * 默认关闭（{@code sloth.monitor.expose-endpoints=true} 显式开启），开启后将监控相关端点
     * 合并进 {@code management.endpoints.web.exposure.include}，不覆盖用户已有的显式配置。
     *
     * @return Bean 后处理器
     */
    @Bean
    @ConditionalOnClass(WebEndpointProperties.class)
    @ConditionalOnProperty(prefix = "sloth.monitor", name = "expose-endpoints", havingValue = "true")
    @ConditionalOnMissingBean(name = "monitorActuatorExposurePostProcessor")
    public static BeanPostProcessor monitorActuatorExposurePostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof WebEndpointProperties webEndpointProperties) {
                    Collection<String> currentIncludes = webEndpointProperties.getExposure().getInclude();
                    Set<String> includes = new LinkedHashSet<>(currentIncludes == null ? Collections.emptySet() : currentIncludes);
                    includes.add("health");
                    includes.add("info");
                    includes.add("metrics");
                    includes.add("prometheus");
                    includes.add("threadPools");
                    includes.add("appInfo");
                    includes.add("jvmInfo");
                    includes.add("metricsSummary");
                    webEndpointProperties.getExposure().setInclude(includes);
                }
                return bean;
            }
        };
    }

    /**
     * 注册系统资源采集器。
     *
     * @param monitorProperties    监控配置
     * @param alarmServiceProvider 告警服务提供者
     * @param meterRegistry        指标注册中心
     * @return 系统资源采集器
     */
    @Bean
    @ConditionalOnClass(MeterRegistry.class)
    @ConditionalOnBean(MeterRegistry.class)
    @ConditionalOnMissingBean
    public SystemResourceCollector systemResourceCollector(MonitorProperties monitorProperties,
                                                           ObjectProvider<AlarmService> alarmServiceProvider,
                                                           MeterRegistry meterRegistry) {
        return new SystemResourceCollector(monitorProperties, alarmServiceProvider, meterRegistry);
    }

    /**
     * 启用定时任务支持。
     */
    @Configuration(proxyBeanMethods = false)
    @EnableScheduling
    @ConditionalOnProperty(prefix = "sloth.monitor", name = "scheduling-enabled", havingValue = "true", matchIfMissing = true)
    static class SchedulingConfiguration {
    }
}
