# sloth-boot-example-observability

`sloth-boot-example-observability` 是 Sloth Boot 的 **可观测性演示模块**，演示如何基于 **OpenTelemetry + Tempo + Prometheus + Loki + Grafana** 构建端到端的 Metrics / Traces / Logs 三位一体可观测体系。

本模块同样采用 **整洁架构**（Adapter / Application / Infrastructure 三层分离），既是可观测性方案的参考实现，也是整洁架构在演示场景下的轻量样例。

## 模块用途

这个示例工程主要用来说明以下几件事：

- 如何一键拉起一整套可观测性基础设施（Tempo + Prometheus + Loki + Grafana + OTel Collector）
- 如何通过 **OTel Java Agent** 以零代码侵入方式采集三大信号
- 如何在应用侧注册 **自定义业务指标**（Counter / Histogram）
- 如何制造 **慢请求、业务异常、多跳调用链**，在 Grafana 面板上端到端观测
- 如何配置 **traceId 贯穿日志**，实现 metric ↔ trace ↔ log 三者跳转

## 可观测性架构

```text
                       ┌──────────────────────────┐
                       │   Demo 应用 (Spring Boot) │
                       │   + OTel Java Agent 2.10  │
                       └────────────┬─────────────┘
              OTLP (gRPC :4317/4318) │ 三大信号统一上报
                                    ▼
                       ┌──────────────────────────┐
                       │   OpenTelemetry Collector │
                       │  memory_limiter / filter /│
                       │  attributes / spanmetrics  │
                       └──┬─────────┬─────────┬─────┘
            traces        │ metrics │         │ logs
                ▼          ▼         ▼
        ┌─────────┐  ┌────────────┐  ┌─────────┐
        │  Tempo  │  │ Prometheus │  │  Loki   │
        │ (traces)│  │ (metrics)  │  │ (logs)  │
        └────┬────┘  └─────┬──────┘  └────┬────┘
             │             │              │
             └─────────────┼──────────────┘
                           ▼
                    ┌──────────────┐
                    │   Grafana    │  统一可视化 + 跨数据源联动
                    │  :3000       │
                    └──────────────┘
```

信号采集与联动要点：

- **Metrics**：OTel Agent 自动埋点 + 应用侧 `Meter` 自定义指标，经 Collector 导出至 Prometheus
- **Traces**：OTel Agent 自动埋点 HTTP/JDBC/线程池 span，经 Collector 导出至 Tempo；`spanmetrics` connector 把 span 衍生为指标供 Prometheus 分析
- **Logs**：Logback 输出含 `traceId/spanId`，经 Collector 导出至 Loki；Grafana 中可从 trace 直接跳转到对应日志
- **联动**：通过 `service_name` label 在 Prometheus 与 Loki 间统一，Grafana 变量可跨数据源联动

## 目录结构

```text
com.sloth.boot.example.observability
├── adapter/controller/                  # HTTP 入口
│   ├── DemoController.java              # 慢操作 / 异常 / 链路追踪 / 自定义指标演示
│   ├── LoadTestController.java          # 压测入口，批量产生可观测数据
│   ├── OrderController.java             # 订单下单 / 支付 / 分页 / 详情
│   ├── ProductController.java           # 商品列表 / 详情（调用链终端）
│   └── UserController.java              # 用户列表 / 详情
│
├── application/                         # 业务逻辑层
│   ├── command/                         # 写操作
│   │   ├── DemoCommand.java             # 演示业务命令（含 OTel 自定义指标）
│   │   ├── LoadTestCommand.java         # 压测命令
│   │   └── order/
│   │       ├── PlaceOrderCommand.java   # 级联下单（RestTemplate 自调形成多跳 span）
│   │       └── PayOrderCommand.java
│   ├── query/                           # 读操作
│   │   ├── GetOrderQuery / ListOrdersQuery
│   │   ├── GetUserQuery / ListUsersQuery
│   │   └── GetProductQuery / ListProductsQuery
│   ├── helper/
│   │   └── MetricsSupport.java          # OTel 指标懒加载工具（双检锁封装）
│   └── model/
│       ├── command (Form/Command)、vo (VO)、convert (MapStruct)
│       └── enums/order (OrderStatus、OrderErrorCode)、enums/user、enums/product
│
└── infrastructure/                      # 基础设施层
    ├── config/
    │   └── AppConfig.java               # OTel Meter / RestTemplate / EventPublisher
    ├── model/po/                         # 数据库实体
    ├── repository/mapper/               # MyBatis Mapper（含 XML）
    └── ...
```

外部基础设施配置位于模块根目录 `config/`：

```text
config/
├── otel-collector/otel-collector-config.yml   # Collector 三大信号处理管线
├── prometheus/prometheus.yml                  # 抓取配置
├── tempo/tempo-config.yml                     # 链路存储
└── grafana/
    ├── provisioning/                          # 数据源 + 仪表盘自动注入
    └── dashboards/                            # 预置仪表盘（HTTP指标 / JVM / 链路 / 日志）
```

## 核心设计

### 1. 零侵入采集：OTel Java Agent

应用代码不直接依赖 OTel SDK，由 Agent 在 JVM 级别完成自动埋点：

- HTTP 请求、JDBC 调用、线程池任务、日志 traceId 注入均为自动
- 应用侧仅通过 `GlobalOpenTelemetry.getMeter()` 获取 Agent 注入的 `Meter` 注册自定义业务指标

### 2. 自定义业务指标

通过 `MetricsSupport` 工具懒加载 OTel 指标句柄，避免重复样板代码：

- `demo.orders.created`（Counter）：下单计数
- `demo.order.create.latency` / `demo.order.pay.latency` / `demo.order.query.latency`（Histogram，ms）：各阶段延迟
- `demo.user.list.latency`（Histogram）：用户列表延迟
- `demo.errors` / `demo.custom.metric` / `demo.timer.processing`（演示指标）

### 3. 生产级 Collector 管线

`otel-collector-config.yml` 演示了生产实践配置：

- `memory_limiter` 防 OOM，置于所有 pipeline 最前
- `filter/healthcheck` 剔除 actuator 健康检查噪声 span
- `attributes/cleanup` 删除敏感请求头（cookie / authorization）
- `spanmetrics` connector 把 spans 衍生为指标导出到 Prometheus
- `transform/loki_labels` 统一 Loki 与 Prometheus 的 `service_name` label

### 4. traceId 全链路贯穿

`logback-spring.xml` 统一日志格式注入 `%X{traceId}` `%X{spanId}`，确保控制台、文件、Collector 三处出口一致；Grafana 中可从任意 trace 直接跳转到对应日志行。

## 演示接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/demo/slow` | GET | 模拟 2500–3500ms 慢请求，触发慢请求告警 |
| `/api/demo/error` | GET | 抛出业务异常并递增 `demo.errors`，演示异常链路 |
| `/api/demo/trace` | GET | 父 span + 异步子 span，演示 traceId 上下文透传 |
| `/api/demo/metrics` | GET | 递增 `demo.custom.metric`，记录 `demo.timer.processing` |
| `/api/demo/orders/place` | POST | 级联下单，自调 /users 与 /products 形成多跳调用链 |
| `/api/demo/orders/{id}/pay` | POST | 支付订单 |
| `/api/demo/orders` | GET | 订单分页查询 |
| `/api/demo/load-test?count=50` | POST | 并发压测，批量产生可观测数据 |

完整接口文档：启动后访问 `http://localhost:8080/doc.html`。

## 启动方式

### 方式一：Docker 一键启动（推荐）

```bash
# 先构建应用 jar
mvn -pl sloth-boot-example/sloth-boot-example-observability -am package -DskipTests

# 拉起全部基础设施 + Demo 应用
cd sloth-boot-example/sloth-boot-example-observability
docker-compose up -d --build
```

启动后访问：

- Grafana 仪表盘：`http://localhost:3000`（默认 admin / sloth-grafana，可通过 `GRAFANA_ADMIN_PASSWORD` 覆盖）
- Prometheus：`http://localhost:9091`
- Tempo：`http://localhost:3200`
- Loki：`http://localhost:3100`
- OTel Collector 指标端点：`http://localhost:8889`
- Demo 应用健康检查：`http://localhost:8080/actuator/health`
- Demo 接口文档：`http://localhost:8080/doc.html`

### 方式二：本地运行 + 远程基础设施

适合调试应用代码：本地用 OTel Agent 挂载运行 Demo，基础设施用 docker-compose 单独拉起（注释掉 `docker-compose.yml` 中的 `sloth-observability` 服务即可）。

```bash
# 拉起基础设施
cd sloth-boot-example/sloth-boot-example-observability
docker-compose up -d tempo otel-collector prometheus loki grafana

# 下载 OTel Agent
curl -L -o opentelemetry-javaagent.jar \
  https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.10.0/opentelemetry-javaagent.jar

# 带 Agent 启动 Demo
java -javaagent:opentelemetry-javaagent.jar \
  -Dotel.service.name=sloth-observability-demo \
  -Dotel.exporter.otlp.endpoint=http://localhost:4317 \
  -Dotel.exporter.otlp.protocol=grpc \
  -jar sloth-boot-example/sloth-boot-example-observability/target/*-exec.jar
```

## 快速观测

1. 启动后调用压测接口批量产生数据：

   ```bash
   curl -X POST "http://localhost:8080/api/demo/load-test?count=100"
   ```

2. 打开 Grafana（`http://localhost:3000`，默认 admin / sloth-grafana），预置仪表盘已自动注入：
   - **Sloth HTTP Metrics**：HTTP 请求量、延迟分布、错误率
   - **Sloth JVM Metrics**：堆内存、GC、线程
   - **Sloth Traces**：调用链 NodeGraph / 服务依赖图
   - **Sloth Logs**：traceId 关联日志

3. 在任意 trace 详情页，点击 span 可跳转到对应时间窗口的 Loki 日志；点击 exemplar 可从 Prometheus 指标跳转到 Tempo trace。

## 数据存储

演示使用 **H2 内存数据库**，启动时自动执行 `schema.sql` + `data.sql`：

- 8 个用户、10 个商品、20 个订单（订单 ID 1001–1020）
- H2 控制台：`http://localhost:8080/h2-console`（JDBC URL `jdbc:h2:mem:sloth_observability`）

## 配置文件说明

- `application.yml`：通用配置（数据源、MyBatis-Plus、Actuator、CORS、文档、日志开关）
- `application-docker.yml`：Docker 环境覆盖（关闭响应包装，便于 OTel 采集原始响应）
- `logback-spring.xml`：含 `traceId/spanId` 的统一日志格式，生产 profile 输出 JSON 风格
- `docker-compose.yml`：基础设施 + Demo 应用一键编排，含 OTel Agent 生产级环境变量
- `Dockerfile`：基于 `eclipse-temurin:21-jre-alpine`，内置 OTel Agent 2.10.0

## 编码规范

本模块遵循与 `sloth-boot-example-service` 一致的规范：

- **整洁架构**：Adapter → Application → Infrastructure 单向依赖，禁止反向
- **Command/Query 分离**：写操作 `*Command.execute()`，读操作 `*Query.execute()`
- **类命名**：`UpperCamelCase`；Controller 字段名与类型名一致
- **Javadoc**：所有公共类和方法标注 `@author sloth-boot` `@since 1.0.0`
- **错误码**：按业务模块拆分（`OrderErrorCode` / `UserErrorCode` / `ProductErrorCode`），不复用其他模块错误码

## 适合怎么使用

- 想了解 OTel 全栈落地的，从这个模块的 `docker-compose.yml` 与 `config/` 开始看
- 想验证自定义指标 / 多跳调用链 / 慢请求观测的，运行 Demo 接口后在 Grafana 观测
- 想参考生产级 Collector 管线配置的，直接看 `otel-collector-config.yml`
- **这是可观测性方案的"截图样板间"**，适合在项目展示时作为端到端能力的演示入口
