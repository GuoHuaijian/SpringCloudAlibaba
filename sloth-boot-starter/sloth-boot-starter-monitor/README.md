# Sloth Boot Starter Monitor

## 简介

`sloth-boot-starter-monitor` 提供应用监控能力，包括健康检查（Redis/Nacos/RocketMQ）、钉钉/企业微信 Webhook 告警、JVM 指标采集、HTTP 接口慢请求监控和 Micrometer Tracing 链路追踪桥接。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-monitor</artifactId>
</dependency>

<!-- 钉钉告警签名需要 Hutool -->
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-crypto</artifactId>
</dependency>
```

## 配置项

| 配置键 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.monitor.enabled` | boolean | `true` | 是否启用监控 Starter |
| `sloth.monitor.slow-api-enabled` | boolean | `true` | 是否启用慢接口监控 |
| `sloth.monitor.slow-api-threshold` | long | `3000` | 慢接口告警阈值（毫秒） |
| `sloth.monitor.alarm.enabled` | boolean | `false` | 是否启用告警 |
| `sloth.monitor.alarm.type` | String | `dingtalk` | 告警类型，`dingtalk` 或 `wechat` |
| `sloth.monitor.alarm.webhook` | String | - | Webhook 地址 |
| `sloth.monitor.alarm.secret` | String | - | Webhook 签名密钥（钉钉加签） |
| `sloth.monitor.tracing.enabled` | boolean | `true` | 是否启用链路追踪 |
| `sloth.monitor.tracing.sampler-rate` | double | `1.0` | 链路采样率（0.0~1.0） |

## 核心组件

| 组件 | 说明 |
|------|------|
| `RedisHealthIndicator` | Redis 健康检查，报告连接状态和延迟 |
| `NacosHealthIndicator` | Nacos 健康检查（需引入 Nacos 依赖） |
| `RocketMQHealthIndicator` | RocketMQ 健康检查（需引入 RocketMQ 依赖） |
| `DingTalkAlarmService` | 钉钉机器人告警，支持加签验证 |
| `WeChatAlarmService` | 企业微信机器人告警 |
| `HttpMetricsFilter` | HTTP 指标过滤器，记录请求数、耗时，触发慢接口告警 |
| `JvmMetricsConfig` | JVM 指标注册，采集堆内存、GC、线程等指标 |
| `TraceContextBridge` | Micrometer Tracing 与自定义 TraceContext 的桥接器 |
| `InfoEndpoint` | Actuator `/actuator/appInfo` 端点，展示应用信息 |

## 使用示例

### 钉钉告警配置

```yaml
sloth:
  monitor:
    alarm:
      enabled: true
      type: dingtalk
      webhook: https://oapi.dingtalk.com/robot/send?access_token=YOUR_TOKEN
      secret: SECxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    slow-api-enabled: true
    slow-api-threshold: 3000
```

### 企业微信告警配置

```yaml
sloth:
  monitor:
    alarm:
      enabled: true
      type: wechat
      webhook: https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=YOUR_KEY
```

### 慢接口监控

```yaml
sloth:
  monitor:
    slow-api-enabled: true
    slow-api-threshold: 2000    # 超过 2 秒的接口触发告警
```

### 链路追踪配置

```yaml
sloth:
  monitor:
    tracing:
      sampler-rate: 0.5    # 50% 采样率
```

需配合 `micrometer-tracing-bridge-brave` 或 `micrometer-tracing-bridge-otel` 使用。`TraceContextBridge` 会自动将 Micrometer Tracer 的 traceId/spanId 同步回框架的 `TraceContext`，确保日志中输出统一的链路标识。

### Actuator 端点

监控相关端点需通过标准配置或 `sloth.monitor.expose-endpoints=true`（默认关闭，避免覆盖用户显式配置）暴露：

```yaml
sloth:
  monitor:
    expose-endpoints: true   # 将下列监控端点合并进 exposure.include
```

或使用标准 Spring Boot 配置：

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,appInfo,jvmInfo,metricsSummary,threadPools
```

| 端点 | 说明 |
|------|------|
| `/actuator/health` | 健康检查（含 Redis/Nacos/RocketMQ 子项） |
| `/actuator/info` | 应用基础信息 |
| `/actuator/metrics` | Micrometer 指标 |
| `/actuator/prometheus` | Prometheus 格式指标 |
| `/actuator/appInfo` | 自定义应用信息端点 |

## FAQ

**Q: 告警没有触发？**
A: 检查 `sloth.monitor.alarm.enabled` 是否为 `true`，以及 `webhook` 地址是否正确。钉钉机器人需配置 `secret`（加签密钥）。

**Q: Nacos/RocketMQ 健康检查未显示？**
A: 这两个检查器是条件注册的，仅在类路径中存在对应依赖时才会加载。Redis 同理。

**Q: 如何自定义告警消息内容？**
A: 实现 `AlarmService` 接口并注册为 Spring Bean，即可替换默认的钉钉/微信告警实现。

**Q: TraceContextBridge 有什么作用？**
A: 解决异步线程或日志框架中 traceId 丢失的问题。它从 Micrometer Tracer 获取当前 span 信息，同步到框架的 `TraceContext` 中，确保跨线程和日志中的链路 ID 一致。

**Q: 如何关闭监控模块？**
A: 配置 `sloth.monitor.enabled=false` 即可禁用整个监控模块。告警可单独通过 `sloth.monitor.alarm.enabled=false` 关闭。
