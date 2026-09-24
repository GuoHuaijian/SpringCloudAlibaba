package com.sloth.boot.starter.sentinel.datasource;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import tools.jackson.core.type.TypeReference;
import com.sloth.boot.common.util.JsonUtil;
import com.sloth.boot.starter.sentinel.config.SentinelProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * Sentinel Nacos 数据源配置。
 * <p>
 * 初始化失败不会阻断应用启动（Sentinel 规则为增强能力，本地无规则时照常运行）；
 * {@link #destroy()} 负责关闭全部数据源，释放内部 Nacos ConfigService 连接与监听线程。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
public class NacosDataSourceConfig {

    private final SentinelProperties sentinelProperties;
    private final Environment environment;

    /**
     * 已创建的数据源，销毁时统一关闭。
     */
    private final List<ReadableDataSource<String, ?>> dataSources = new ArrayList<>(4);

    /**
     * 构造函数。
     *
     * @param sentinelProperties Sentinel 配置
     * @param environment        Spring 环境
     */
    public NacosDataSourceConfig(SentinelProperties sentinelProperties, Environment environment) {
        this.sentinelProperties = sentinelProperties;
        this.environment = environment;
    }

    /**
     * 初始化 Nacos 数据源。Nacos 不可用时记录错误并降级为无动态规则运行，不阻断启动。
     */
    @PostConstruct
    public void init() {
        String serverAddr = environment.getProperty("spring.cloud.nacos.config.server-addr",
            environment.getProperty("spring.cloud.nacos.discovery.server-addr"));
        String applicationName = environment.getProperty("spring.application.name", "application");
        if (serverAddr == null || serverAddr.isBlank()) {
            return;
        }
        try {
            registerFlowRules(serverAddr, applicationName);
            registerDegradeRules(serverAddr, applicationName);
            registerSystemRules(serverAddr, applicationName);
            registerParamFlowRules(serverAddr, applicationName);
        } catch (RuntimeException ex) {
            log.error("[Sentinel] Nacos 规则数据源初始化失败, serverAddr={}, 应用将以无动态规则方式启动",
                serverAddr, ex);
        }
    }

    /**
     * 关闭全部数据源，释放 Nacos 连接与监听资源。
     */
    @PreDestroy
    public void destroy() {
        for (ReadableDataSource<String, ?> dataSource : dataSources) {
            try {
                dataSource.close();
            } catch (Exception ex) {
                log.warn("[Sentinel] 关闭 Nacos 数据源失败", ex);
            }
        }
        dataSources.clear();
    }

    protected void registerFlowRules(String serverAddr, String applicationName) {
        ReadableDataSource<String, List<FlowRule>> dataSource = new NacosDataSource<>(
            serverAddr,
            sentinelProperties.getNacosGroupId(),
            applicationName + "-flow-rules",
            jsonConverter(new TypeReference<List<FlowRule>>() {
            })
        );
        FlowRuleManager.register2Property(dataSource.getProperty());
        dataSources.add(dataSource);
    }

    protected void registerDegradeRules(String serverAddr, String applicationName) {
        ReadableDataSource<String, List<DegradeRule>> dataSource = new NacosDataSource<>(
            serverAddr,
            sentinelProperties.getNacosGroupId(),
            applicationName + "-degrade-rules",
            jsonConverter(new TypeReference<List<DegradeRule>>() {
            })
        );
        DegradeRuleManager.register2Property(dataSource.getProperty());
        dataSources.add(dataSource);
    }

    protected void registerSystemRules(String serverAddr, String applicationName) {
        ReadableDataSource<String, List<SystemRule>> dataSource = new NacosDataSource<>(
            serverAddr,
            sentinelProperties.getNacosGroupId(),
            applicationName + "-system-rules",
            jsonConverter(new TypeReference<List<SystemRule>>() {
            })
        );
        SystemRuleManager.register2Property(dataSource.getProperty());
        dataSources.add(dataSource);
    }

    protected void registerParamFlowRules(String serverAddr, String applicationName) {
        ReadableDataSource<String, List<ParamFlowRule>> dataSource = new NacosDataSource<>(
            serverAddr,
            sentinelProperties.getNacosGroupId(),
            applicationName + "-param-rules",
            jsonConverter(new TypeReference<List<ParamFlowRule>>() {
            })
        );
        ParamFlowRuleManager.register2Property(dataSource.getProperty());
        dataSources.add(dataSource);
    }

    private <T> Converter<String, T> jsonConverter(TypeReference<T> typeReference) {
        return source -> JsonUtil.parseObject(source, typeReference);
    }
}
