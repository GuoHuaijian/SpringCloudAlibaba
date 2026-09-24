package com.sloth.boot.starter.sentinel.datasource;

import com.sloth.boot.starter.sentinel.config.SentinelProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Sentinel Nacos 数据源配置测试。
 * <p>
 * 通过覆写注册方法避免真实连接 Nacos，聚焦启动容错与条件行为。
 */
@DisplayName("NacosDataSourceConfig 测试")
class NacosDataSourceConfigTest {

    private final SentinelProperties sentinelProperties = new SentinelProperties();

    @Test
    @DisplayName("未配置 Nacos 地址时不注册任何数据源")
    void doesNothingWithoutServerAddr() {
        RecordingConfig config = new RecordingConfig(sentinelProperties, new MockEnvironment());

        config.init();

        assertThat(config.registered).isEmpty();
    }

    @Test
    @DisplayName("配置 config server-addr 时注册全部四类规则数据源")
    void registersAllDataSources() {
        MockEnvironment environment = new MockEnvironment()
            .withProperty("spring.cloud.nacos.config.server-addr", "127.0.0.1:8848");
        RecordingConfig config = new RecordingConfig(sentinelProperties, environment);

        config.init();

        assertThat(config.registered).containsExactly("flow", "degrade", "system", "param");
    }

    @Test
    @DisplayName("仅配置 discovery server-addr 时也能注册")
    void fallsBackToDiscoveryServerAddr() {
        MockEnvironment environment = new MockEnvironment()
            .withProperty("spring.cloud.nacos.discovery.server-addr", "127.0.0.1:8848");
        RecordingConfig config = new RecordingConfig(sentinelProperties, environment);

        config.init();

        assertThat(config.registered).isNotEmpty();
    }

    @Test
    @DisplayName("Nacos 不可用时初始化失败不阻断启动")
    void swallowsRegistrationFailure() {
        MockEnvironment environment = new MockEnvironment()
            .withProperty("spring.cloud.nacos.config.server-addr", "127.0.0.1:8848");
        RecordingConfig config = new RecordingConfig(sentinelProperties, environment);
        config.failure = new IllegalStateException("nacos unavailable");

        assertThatCode(config::init).doesNotThrowAnyException();

        assertThat(config.registered).isEmpty();
    }

    @Test
    @DisplayName("无数据源时销毁不抛异常")
    void destroyWithoutDataSources() {
        RecordingConfig config = new RecordingConfig(sentinelProperties, new MockEnvironment());

        assertThatCode(config::destroy).doesNotThrowAnyException();
    }

    /**
     * 记录型测试子类：覆写注册方法以隔离真实 Nacos 连接。
     */
    private static class RecordingConfig extends NacosDataSourceConfig {

        private final List<String> registered = new ArrayList<>(4);
        private RuntimeException failure;

        RecordingConfig(SentinelProperties sentinelProperties, MockEnvironment environment) {
            super(sentinelProperties, environment);
        }

        @Override
        protected void registerFlowRules(String serverAddr, String applicationName) {
            if (failure != null) {
                throw failure;
            }
            registered.add("flow");
        }

        @Override
        protected void registerDegradeRules(String serverAddr, String applicationName) {
            registered.add("degrade");
        }

        @Override
        protected void registerSystemRules(String serverAddr, String applicationName) {
            registered.add("system");
        }

        @Override
        protected void registerParamFlowRules(String serverAddr, String applicationName) {
            registered.add("param");
        }
    }
}
