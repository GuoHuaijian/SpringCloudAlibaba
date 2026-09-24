<div align="center">

# 🦥 Sloth Boot

> **慢工出细活。** 一个面向个人开源与中小团队的 Spring Boot 多模块基础脚手架。  
> 不造平台，只沉淀高频、可复用的工程化基础能力。

<br/>

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.1-0A6CFF?style=flat-square&logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.1.6-FF6F00?style=flat-square&logo=spring&logoColor=white)](https://spring.io/projects/spring-ai)
[![Maven](https://img.shields.io/badge/Maven-3.8.1+-C71A36?style=flat-square&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue?style=flat-square&logo=apache&logoColor=white)](./LICENSE)
[![Build](https://img.shields.io/github/actions/workflow/status/GuoHuaijian/SlothBoot/ci.yml?style=flat-square&logo=githubactions&logoColor=white&label=CI)](./.github/workflows/ci.yml)
[![Stars](https://img.shields.io/github/stars/GuoHuaijian/SlothBoot?style=flat-square&logo=github&logoColor=white)](https://github.com/GuoHuaijian/SlothBoot/stargazers)
[![Issues](https://img.shields.io/github/issues/GuoHuaijian/SlothBoot?style=flat-square&logo=github)](https://github.com/GuoHuaijian/SlothBoot/issues)
[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-brightgreen?style=flat-square&logo=git&logoColor=white)](./docs/CONTRIBUTING.md)
[![Status](https://img.shields.io/badge/Status-Active-success?style=flat-square)](./CHANGELOG.md)
[![OpenTelemetry](https://img.shields.io/badge/OpenTelemetry-CNCF-blue?style=flat-square&logo=opentelemetry&logoColor=white)](./sloth-boot-example/sloth-boot-example-observability)

<br/>

[📖 快速开始](#-快速开始) · [🧩 模块导航](#-模块导航) · [🏗 架构文档](./docs/architecture.md) · [🗺 Roadmap](#-roadmap) · [🤝 参与贡献](./docs/CONTRIBUTING.md) · [📋 更新日志](./CHANGELOG.md)

</div>

---

## 🦥 为什么叫 Sloth Boot？

树懒不慌不忙，但每一步都踩得很稳。  
这个项目的理念相同 —— **不追求大而全，只做扎实可用的基础层**：

- ✅ 结构清晰，可以按模块裁剪引入
- ✅ 配置收敛，统一 `sloth.*` 前缀，一目了然
- ✅ 自动装配，业务侧接入几乎零配置
- ✅ 适合长期维护，而不是一次性造轮子
- ✅ 代码可读，注释友好，适合学习和二次定制

---

## ✨ 核心特性

<table>
<tr>
<td width="50%">

**🏗 工程化结构**
- Maven 多模块，按需引入
- `common` 基础层 + `starter` 能力层分离
- `example` 示例工程，可直接运行对照

</td>
<td width="50%">

**⚙️ 统一配置治理**
- 所有自定义配置统一 `sloth.*` 前缀
- `@ConditionalOnMissingBean` 支持业务侧覆盖
- 通过 `AutoConfiguration.imports` 自动装配

</td>
</tr>
<tr>
<td width="50%">

**🔧 覆盖高频场景**
- Web / Redis / MQ / MyBatis / 线程池
- 网关 / Feign / Sentinel / Seata
- OSS / Excel / 短信 / 任务调度 / ES / AI 集成

</td>
<td width="50%">

**🧪 测试友好**
- 提供 Spring Boot / MockMvc / Mapper 测试基类
- 集成 CI/CD，自动构建验证
- 示例工程配套 Knife4j 文档

</td>
</tr>
</table>

---

## 🏛 技术栈

| 层次 | 技术选型 |
|------|---------|
| **语言 & 运行时** | Java 21 · Maven 3.8.1+ |
| **核心框架** | Spring Boot 4.0.6 · Spring Cloud 2025.1.1 |
| **微服务生态** | Spring Cloud Alibaba 2025.1.0.0 · Nacos · Sentinel · Seata |
| **AI 能力** | Spring AI 1.1.6 · OpenAI · 通义千问 · DeepSeek · Ollama |
| **数据层** | MyBatis-Plus · MySQL · Elasticsearch |
| **缓存 & 消息** | Redis · RocketMQ |
| **任务 & 文件** | XXL-Job · MinIO · 阿里云 OSS |
| **网关 & RPC** | Spring Cloud Gateway · OpenFeign |
| **文档 & 监控** | Knife4j · Spring Actuator · Prometheus |

---

## 🗂 模块结构

```text
sloth-boot/
│
├── 📦 sloth-boot-common/            # 基础公共层
│   ├── sloth-boot-common-core       # 常量 / 异常 / 返回体 / 上下文 / 工具类 / 注解
│   ├── sloth-boot-common-log        # Trace 过滤 / 请求日志 / 操作日志切面
│   ├── sloth-boot-common-security   # 加解密 / 签名 / 脱敏 / XSS 处理
│   ├── sloth-boot-common-doc        # OpenAPI / Knife4j 文档自动配置
│   └── sloth-boot-common-test       # 测试基类
│
├── 🚀 sloth-boot-starter/           # 能力 Starter 层
│   ├── sloth-boot-starter-web       # Web 基础 / 统一异常 / 统一返回 / 参数校验
│   ├── sloth-boot-starter-ai        # Spring AI 集成 / ChatClient 封装 / OpenAI 模型接入
│   ├── sloth-boot-starter-redis     # RedisTemplate / 缓存工具 / 分布式锁 / 限流
│   ├── sloth-boot-starter-rocketmq  # RocketMQ 生产消费封装
│   ├── sloth-boot-starter-mybatis   # MyBatis-Plus 插件 / 自动填充 / TypeHandler
│   ├── sloth-boot-starter-thread-pool  # 线程池 / 上下文透传 / 监控端点
│   ├── sloth-boot-starter-sentinel  # Sentinel 限流降级 / Nacos 规则源
│   ├── sloth-boot-starter-monitor   # 健康检查 / 告警 / Actuator 增强
│   ├── sloth-boot-starter-feign     # Feign 拦截器 / 解码器 / Fallback 模板
│   ├── sloth-boot-starter-gateway   # Gateway 过滤器 / 异常处理 / 动态路由
│   ├── sloth-boot-starter-oss       # 本地 / MinIO / 阿里云 OSS 统一封装
│   ├── sloth-boot-starter-excel     # Excel 导入导出与响应封装
│   ├── sloth-boot-starter-job       # XXL-Job 自动配置与任务基类
│   ├── sloth-boot-starter-seata     # Seata AT 模式自动配置
│   ├── sloth-boot-starter-es        # Elasticsearch 常用操作封装
│   ├── sloth-boot-starter-sms       # 阿里云 / 腾讯云短信门面
│   └── sloth-boot-starter-idempotent  # 幂等增强 / Token 模式
│
├── 🧪 sloth-boot-example/           # 示例工程
│   └── sloth-boot-example-service   # 单体服务示例，本地可直接运行
│
├── 📄 README.md
├── 📄 CHANGELOG.md
├── 📄 LICENSE
└── 📄 .gitignore
```

---

## 🧩 模块导航

### Common 基础层

| 模块 | 核心能力 | 推荐先看 |
|------|---------|:-------:|
| `common-core` | 基础常量、统一返回体 `R<T>`、业务异常体系、请求上下文、工具类、通用注解 | ✅ |
| `common-log` | TraceId 过滤器、HTTP 请求日志、`@OperateLog` 操作日志注解与切面、慢操作事件 | ✅ |
| `common-security` | AES/RSA 加解密、HMAC 签名验签、数据脱敏、XSS 过滤 | — |
| `common-doc` | Knife4j / OpenAPI 3 自动配置，开箱即用文档 | — |
| `common-test` | `BaseSpringBootTest`、`BaseMockMvcTest` 测试基类、`@MockUser` | — |

### Starter 能力层

每个 starter 均有独立 README，点击模块名查看详细文档（配置项、使用示例、FAQ）。

| 模块 | 核心能力 | 文档 |
|------|---------|:----:|
| [`starter-ai`](./sloth-boot-starter/sloth-boot-starter-ai/README.md) | Spring AI 多模型接入、流式响应（SSE）、Function Calling | ✅ 新增 |
| [`starter-web`](./sloth-boot-starter/sloth-boot-starter-web/README.md) | 全局异常处理、统一返回包装、参数校验、XSS 过滤 | ✅ |
| [`starter-redis`](./sloth-boot-starter/sloth-boot-starter-redis/README.md) | 缓存工具、分布式锁、滑动窗口限流、幂等、延迟队列 | ✅ |
| [`starter-mybatis`](./sloth-boot-starter/sloth-boot-starter-mybatis/README.md) | MyBatis-Plus 增强、自动填充、数据权限、慢 SQL、`BaseMapperTest` 测试基类 | ✅ |
| [`starter-thread-pool`](./sloth-boot-starter/sloth-boot-starter-thread-pool/README.md) | 动态线程池、TTL 上下文透传、虚拟线程、Actuator 监控 | ✅ |
| [`starter-auth`](./sloth-boot-starter/sloth-boot-starter-auth/README.md) | Sa-Token 认证授权、登录/登出、权限注解、UserContext 集成 | ✅ 新增 |
| [`starter-gateway`](./sloth-boot-starter/sloth-boot-starter-gateway/README.md) | 全局过滤器、鉴权、动态路由、WebFlux 异常处理 | ✅ |
| [`starter-monitor`](./sloth-boot-starter/sloth-boot-starter-monitor/README.md) | 健康检查、钉钉/微信告警、JVM 指标、Micrometer Tracing | ✅ |
| [`starter-feign`](./sloth-boot-starter/sloth-boot-starter-feign/README.md) | 请求头透传、统一错误解码、Fallback 模板 | ✅ |
| [`starter-rocketmq`](./sloth-boot-starter/sloth-boot-starter-rocketmq/README.md) | RocketMQ 生产消费封装、消息重试、幂等消费 | ✅ |
| [`starter-oss`](./sloth-boot-starter/sloth-boot-starter-oss/README.md) | 统一 OSS 门面（本地 / MinIO / 阿里云） | ✅ |
| [`starter-excel`](./sloth-boot-starter/sloth-boot-starter-excel/README.md) | EasyExcel 封装、导入校验、流式导出 | ✅ |
| [`starter-job`](./sloth-boot-starter/sloth-boot-starter-job/README.md) | XXL-Job 自动注册、任务基类、分片处理 | ✅ |
| [`starter-sentinel`](./sloth-boot-starter/sloth-boot-starter-sentinel/README.md) | Sentinel 限流降级、Nacos 动态规则源 | ✅ |
| [`starter-seata`](./sloth-boot-starter/sloth-boot-starter-seata/README.md) | Seata AT 模式分布式事务 | ✅ |
| [`starter-es`](./sloth-boot-starter/sloth-boot-starter-es/README.md) | Elasticsearch 索引管理、CRUD、聚合查询 | ✅ |
| [`starter-sms`](./sloth-boot-starter/sloth-boot-starter-sms/README.md) | 统一短信门面（阿里云 / 腾讯云） | ✅ |
| [`starter-idempotent`](./sloth-boot-starter/sloth-boot-starter-idempotent/README.md) | `@Idempotent` 注解驱动，Token 模式 + Redis 去重 | ✅ |
| [`generator`](./sloth-boot-generator/README.md) | MyBatis-Plus 代码生成器，一键生成 CRUD 全套代码 | ✅ 新增 |

---

## 🏗 架构概览

```text
                           ┌─────────────────────────────────────┐
                           │           sloth-boot-example        │
                           │         (可运行示例工程)             │
                           └──────────────────┬──────────────────┘
                                              │
              ┌───────────────────────────────┼───────────────────────────────┐
              │                               │                               │
    ┌─────────▼─────────┐         ┌───────────▼──────────┐        ┌──────────▼──────────┐
    │   Starter 能力层   │         │   Common 基础层       │        │   Generator 工具层   │
    │                    │         │                      │        │                     │
    │  auth (Sa-Token)   │         │  core (R/异常/上下文) │        │  代码生成器          │
    │  web (异常/返回)    │         │  log (日志/追踪)      │        │  (MyBatis-Plus)      │
    │  redis (缓存/锁)    │         │  security (加密/XSS)  │        │                     │
    │  mybatis (ORM)     │         │  doc (Knife4j)       │        └─────────────────────┘
    │  ai (Spring AI)    │         │  test (测试基类)      │
    │  mq / excel / ...  │         └──────────────────────┘
    │  monitor (监控)     │
    │  gateway (网关)     │
    └─────────────────────┘

    自动装配: @AutoConfiguration + AutoConfiguration.imports + @ConditionalOnMissingBean
    配置治理: 统一 sloth.* 命名空间 + 配置元数据 (IDE 自动补全)
```

---

## 🚀 快速开始

### 环境要求

| 工具 | 版本要求 |
|------|---------|
| JDK | 21+ |
| Maven | 3.8.1+ |
| MySQL | 8.0+（示例工程需要） |
| Redis | 6.0+（示例工程需要） |

### 1. 克隆仓库

```bash
git clone https://github.com/GuoHuaijian/SlothBoot.git
cd sloth-boot
```

### 2. 构建全量模块

```bash
mvn clean verify
```

### 3. 启动示例工程

```bash
# 修改 example 配置文件中的 MySQL / Redis 地址
vim sloth-boot-example/sloth-boot-example-service/src/main/resources/application.yml

# 启动
mvn -pl sloth-boot-example/sloth-boot-example-service spring-boot:run
```

### 4. 验证运行

```bash
# 健康检查
curl http://localhost:8080/actuator/health

# API 文档
open http://localhost:8080/doc.html
```

### 5. Docker 一键启动（推荐）

```bash
# 一键启动 MySQL + Redis + 示例服务（示例服务默认使用内存 H2 数据库）
docker-compose up -d

# 查看日志
docker-compose logs -f sloth-example
```

启动后访问：
- 健康检查: http://localhost:8080/actuator/health
- API 文档: http://localhost:8080/doc.html
- AI 对话: `POST http://localhost:8080/api/ai/chat?prompt=你好`（需配置 `OPENAI_API_KEY` 且 `sloth.ai.enabled: true`）

> 💡 **推荐阅读顺序：**  
> `common-core` → `starter-web` → `starter-redis` → `starter-mybatis` → `example-service`

---

## ⚙️ 配置约定

```yaml
sloth:
  web:
    response-wrapper: true        # 统一响应包装
    access-log-enabled: true      # 访问日志

  xss:
    enabled: true                 # XSS 过滤

  ai:
    enabled: false
    model: gpt-4o-mini
    default-system-prompt: "你是 Sloth Boot 内置 AI 助手"

  redis:
    key-prefix: "sloth:"           # 业务 Key 统一前缀
    lock-wait-time: 3             # 分布式锁等待时间（秒）
    lock-lease-time: 30           # 分布式锁持有时长（秒）

  thread-pool:
    enabled: true
    virtual-enabled: false        # Java 21 虚拟线程
    pools:
      default:
        core-size: 8
        max-size: 32
        queue-capacity: 1024

  idempotent:
    enabled: true
    timeout: 10                   # 幂等 Token 有效期（秒）

spring:
  ai:
    model:
      chat: openai
    openai:
      base-url: https://api.openai.com
      api-key: ${OPENAI_API_KEY:}
```

> 所有模块统一使用 `sloth.*` 前缀，避免配置散落在多个命名空间。  
> 每个 starter 均支持通过 `@ConditionalOnMissingBean` 覆盖默认 Bean。

---

## 🗺 Roadmap

```text
Phase 1 — 骨架搭建               ████████████████████  100% ✅
Phase 2 — 代码质量与测试          ████████████████████  100% ✅
Phase 3 — 核心能力建设            ████████████████████  100% ✅
Phase 4 — 现代化特性              ████████████████████  100% ✅
Phase 5 — 开源运营与发布          ██████████████░░░░░░   70% 📋
```

- [x] 完成 31 个模块骨架搭建与编译修复
- [x] 53 个单元测试覆盖核心模块
- [x] 新增 starter-auth（Sa-Token 认证授权）
- [x] 新增 sloth-boot-generator（代码生成器）
- [x] AI 流式响应、虚拟线程、GraalVM Native Image 支持
- [x] i18n 国际化 + 配置元数据（IDE 自动补全）
- [x] Docker 支持（Dockerfile + docker-compose.yml）
- [x] CHANGELOG + 架构图 + 竞品对比
- [ ] Maven Central 发布
- [ ] 在线文档站（VitePress）

---

## 🤝 参与贡献

欢迎任何形式的贡献，无论是 Issue、PR 还是文档改进。

```bash
# Fork 本仓库后
git checkout -b feat/your-feature
git commit -m "feat: add your feature"
git push origin feat/your-feature
# 提交 Pull Request
```

请在贡献前阅读：

- [🤝 CONTRIBUTING.md](./docs/CONTRIBUTING.md) — 贡献指南
- [📜 CODE_OF_CONDUCT.md](./docs/CODE_OF_CONDUCT.md) — 行为准则
- [🔒 SECURITY.md](./docs/SECURITY.md) — 安全漏洞报告
- [💬 SUPPORT.md](./docs/SUPPORT.md) — 获取帮助

---

## 📄 License

```
Copyright 2025 Sloth Boot Contributors

Licensed under the Apache License, Version 2.0
http://www.apache.org/licenses/LICENSE-2.0
```

---

<div align="center">

如果这个项目对你有帮助，欢迎点一个 ⭐ Star，这是对维护者最大的鼓励。

</div>
