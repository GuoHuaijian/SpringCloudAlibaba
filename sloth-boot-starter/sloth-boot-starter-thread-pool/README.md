# Sloth Boot Starter Thread Pool

## 简介

`sloth-boot-starter-thread-pool` 提供动态线程池管理能力，支持多线程池配置、TTL（TransmittableThreadLocal）上下文传递、Java 21 虚拟线程、Actuator 监控端点和 Micrometer 指标采集。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-thread-pool</artifactId>
</dependency>
```

## 配置项

| 配置键 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.thread-pool.enabled` | boolean | `true` | 是否启用线程池 Starter |
| `sloth.thread-pool.virtual-enabled` | boolean | `false` | 是否启用 Java 21 虚拟线程 |
| `sloth.thread-pool.pools.<name>.core-size` | int | `8` | 核心线程数 |
| `sloth.thread-pool.pools.<name>.max-size` | int | `32` | 最大线程数 |
| `sloth.thread-pool.pools.<name>.queue-capacity` | int | `1024` | 阻塞队列容量 |
| `sloth.thread-pool.pools.<name>.keep-alive-time` | int | `60` | 空闲线程存活时间（秒） |
| `sloth.thread-pool.pools.<name>.thread-name-prefix` | String | `sloth-async-` | 线程名前缀 |
| `sloth.thread-pool.pools.<name>.rejected-policy` | String | `CALLER_RUNS` | 拒绝策略 |

## 默认线程池

| 名称 | 核心 | 最大 | 队列 | 前缀 |
|------|------|------|------|------|
| `default` | 8 | 32 | 1024 | `sloth-async-` |
| `scheduled` | 4 | 4 | 0 | `sloth-scheduled-` |

## 核心组件

| 组件 | 说明 |
|------|------|
| `VisibleThreadPoolExecutor` | 可观测的线程池执行器，暴露活跃线程数、队列大小等运行指标 |
| `ThreadPoolRegistry` | 线程池注册表，管理所有线程池实例 |
| `TtlTaskDecorator` | 任务装饰器，通过 TTL 实现主线程上下文（TraceId/UserContext）自动传递 |
| `AsyncExceptionHandler` | 异步方法未捕获异常的统一处理器 |
| `ThreadPoolEndpoint` | Actuator 端点 `/actuator/threadPools`，查看所有线程池状态 |
| `ThreadPoolMetrics` | Micrometer 指标采集，自动注册线程池相关 Meter |
| `LogRejectedExecutionHandler` | 拒绝策略，记录告警日志并支持 CallerRuns 降级 |

## 使用示例

### @Async 异步方法

```java
@Service
public class NotificationService {

    // 默认使用 slothTaskExecutor 线程池
    @Async
    public void sendEmail(String to, String content) {
        // 异步发送邮件，TraceId 和 UserContext 自动传递
        log.info("发送邮件, traceId={}", TraceContext.getTraceId());
    }

    // 指定使用虚拟线程执行器
    @Async("slothVirtualThreadExecutor")
    public void callThirdPartyApi(String url) {
        // I/O 密集型任务适合虚拟线程
    }

    // 指定自定义线程池
    @Async("slothTaskExecutor")
    public void processReport(Long reportId) {
        // 长时间运行的任务
    }
}
```

### 虚拟线程配置（需要 JDK 21+）

```yaml
sloth:
  thread-pool:
    virtual-enabled: true
```

启用后注册 `slothVirtualThreadExecutor` Bean，适用于 I/O 密集型任务。通过 `@Async("slothVirtualThreadExecutor")` 使用。

### 自定义线程池配置

```yaml
sloth:
  thread-pool:
    pools:
      default:
        core-size: 16
        max-size: 64
        queue-capacity: 2048
        thread-name-prefix: "biz-async-"
        rejected-policy: CALLER_RUNS
      report:
        core-size: 4
        max-size: 8
        queue-capacity: 256
        thread-name-prefix: "report-"
        rejected-policy: CALLER_RUNS
```

### Actuator 监控

启动后访问 `/actuator/threadPools` 查看所有线程池的实时状态：

```json
{
  "default": {
    "poolSize": 8,
    "activeCount": 3,
    "queueSize": 12,
    "completedTaskCount": 15832,
    "rejectedCount": 0
  }
}
```

## FAQ

**Q: TraceId 在异步线程中丢失了怎么办？**
A: 本 Starter 已内置 `TtlTaskDecorator`，会自动通过 TTL 传递上下文。确保项目中引入了 `transmittable-thread-local` 依赖。

**Q: 虚拟线程有什么限制？**
A: 虚拟线程仅适用于 I/O 密集型任务，不适合 CPU 密集型计算。需要 JDK 21+，无需额外 JVM 参数（JDK 21 正式版已正式支持）。

**Q: 拒绝策略 `CALLER_RUNS` 和线程池满时的行为？**
A: 当线程池和队列都满时，`CALLER_RUNS` 会在调用者线程（通常是 Tomcat 线程）中执行任务，避免任务被丢弃，同时记录 WARN 日志。

**Q: 如何新增自定义线程池？**
A: 在 `sloth.thread-pool.pools` 下添加新条目即可。框架会自动创建并注册到 `ThreadPoolRegistry`，可通过 `/actuator/threadPools` 查看。
