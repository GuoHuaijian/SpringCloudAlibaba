# SlothBoot 配置参考手册

本文档列出 SlothBoot 框架所有可配置属性，按模块分组。

---

## sloth.web.*

统一 Web 模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.web.response-wrapper` | `boolean` | `true` | 是否启用统一响应包装 |
| `sloth.web.body-cache-enabled` | `boolean` | `false` | 是否启用请求体缓存（支持多次读取 @RequestBody） |
| `sloth.web.access-log-enabled` | `boolean` | `true` | 是否启用 API 访问日志事件发布 |
| `sloth.web.gzip.enabled` | `boolean` | `false` | 是否启用 Gzip 响应压缩 |
| `sloth.web.gzip.min-size` | `int` | `1024` | 启用压缩的最小响应体大小（字节） |
| `sloth.web.gzip.mime-types` | `String[]` | `text/html,text/xml,text/plain,text/css,application/json,application/javascript` | 启用压缩的 MIME 类型 |

---

## sloth.redis.*

Redis 模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.redis.enabled` | `boolean` | `true` | 是否启用 Redis Starter |
| `sloth.redis.key-prefix` | `String` | `sloth:` | 统一业务 key 前缀 |
| `sloth.redis.lock-wait-time` | `long` | `3` | 分布式锁默认等待时间（秒） |
| `sloth.redis.lock-lease-time` | `long` | `30` | 分布式锁默认租约时间（秒） |
| `sloth.redis.enable-type-info` | `boolean` | `false` | 是否携带类型信息进行 JSON 序列化（默认关闭，开启后仅允许白名单包前缀） |
| `sloth.redis.null-value-expire-seconds` | `long` | `60` | 空值缓存时间（秒） |
| **多级缓存** | | | |
| `sloth.redis.multi-cache.enabled` | `boolean` | `false` | 是否启用多级缓存（Caffeine + Redis） |
| `sloth.redis.multi-cache.l1-max-size` | `int` | `1000` | L1 Caffeine 缓存最大条目数 |
| `sloth.redis.multi-cache.l1-ttl-seconds` | `long` | `300` | L1 Caffeine 缓存过期时间（秒） |
| `sloth.redis.scan-count` | `int` | `500` | SCAN 批量拉取数量 |
| **分布式 ID 生成器** | | | |
| `sloth.redis.id-generator.enabled` | `boolean` | `true` | 是否启用分布式 ID 生成器 |
| `sloth.redis.id-generator.worker-id` | `int` | `0` | 机器号（0-1023） |
| `sloth.redis.id-generator.prefix` | `String` | `sloth` | ID 前缀 |
| **布隆过滤器** | | | |
| `sloth.redis.bloom-filter.enabled` | `boolean` | `false` | 是否启用布隆过滤器 |
| `sloth.redis.bloom-filter.name` | `String` | `sloth:bloom:default` | 布隆过滤器 Redis Key |
| `sloth.redis.bloom-filter.expected-insertions` | `long` | `1000000` | 预期插入元素数量 |
| `sloth.redis.bloom-filter.false-positive-probability` | `double` | `0.01` | 误判概率 |
| **Pub/Sub** | | | |
| `sloth.redis.pub-sub.enabled` | `boolean` | `false` | 是否启用 Pub/Sub 模板 |

---

## sloth.mybatis.*

MyBatis Plus 模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.mybatis.tenant-enabled` | `boolean` | `false` | 是否启用租户插件 |
| `sloth.mybatis.tenant-column` | `String` | `tenant_id` | 租户字段名 |
| `sloth.mybatis.tenant-ignore-tables` | `Set<String>` | `[]` | 忽略租户过滤的表 |
| `sloth.mybatis.slow-sql-threshold` | `long` | `1000` | 慢 SQL 阈值（毫秒） |
| `sloth.mybatis.encrypt-key` | `String` | `null` | 字段加密密钥（EncryptTypeHandler 使用，建议经环境变量注入） |
| `sloth.mybatis.tenant-auto-fill` | `boolean` | `true` | INSERT 时是否自动填充 tenantId 字段 |

---

## sloth.auth.*

Sa-Token 认证授权模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.auth.enabled` | `boolean` | `true` | 是否启用认证 |
| `sloth.auth.token-name` | `String` | `Authorization` | Token 名称（请求头中的 key） |
| `sloth.auth.token-timeout` | `long` | `7200` | Token 有效期（秒），默认 2 小时 |
| `sloth.auth.active-timeout` | `long` | `-1` | Token 最低活跃频率（秒），-1 表示不限 |
| `sloth.auth.is-concurrent` | `boolean` | `true` | 是否允许同一账号并发登录 |
| `sloth.auth.is-share` | `boolean` | `true` | 多人登录同一账号时是否共用同一个 Token |
| `sloth.auth.token-prefix` | `String` | `Bearer` | Token 前缀 |
| `sloth.auth.is-read-cookie` | `boolean` | `false` | 是否从 Cookie 中读取 Token |
| `sloth.auth.is-read-body` | `boolean` | `false` | 是否从 Body 中读取 Token |
| `sloth.auth.white-list` | `List<String>` | `[]` | 白名单路径（不需要认证） |
| `sloth.auth.black-list` | `List<String>` | `[]` | 黑名单路径（禁止访问） |
| `sloth.auth.device-strategy` | `String` | `ALLOW_MULTI` | 多设备登录策略：ALLOW_MULTI / REPLACED / PROHIBIT |

---

## sloth.ai.*

AI Starter 模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.ai.enabled` | `boolean` | `true` | 是否启用 AI Starter |
| `sloth.ai.model` | `String` | `gpt-4o-mini` | 默认模型名称 |
| `sloth.ai.temperature` | `Double` | `0.7` | 默认温度参数 |
| `sloth.ai.top-p` | `Double` | `1.0` | 默认 topP 参数 |
| `sloth.ai.max-tokens` | `Integer` | `2048` | 默认最大输出 Token 数 |
| `sloth.ai.default-system-prompt` | `String` | `null` | 默认系统提示词 |
| **对话记忆** | | | |
| `sloth.ai.memory.enabled` | `boolean` | `false` | 是否启用对话记忆 |
| `sloth.ai.memory.max-messages` | `int` | `20` | 滑动窗口最大消息数 |
| **向量嵌入** | | | |
| `sloth.ai.embedding.enabled` | `boolean` | `true` | 是否启用向量嵌入客户端 |
| **图像生成** | | | |
| `sloth.ai.image.enabled` | `boolean` | `true` | 是否启用图像生成客户端 |
| **可观测性** | | | |
| `sloth.ai.observability.enabled` | `boolean` | `true` | 是否启用 AI 可观测性装饰器 |
| `sloth.ai.observability.slow-threshold-ms` | `long` | `3000` | 慢调用阈值（毫秒） |

---

## sloth.monitor.*

监控模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.monitor.enabled` | `boolean` | `true` | 是否启用监控 Starter |
| `sloth.monitor.expose-endpoints` | `boolean` | `false` | 是否自动将监控相关 Actuator 端点合并进 web 暴露范围 |
| `sloth.monitor.slow-api-enabled` | `boolean` | `true` | 是否启用慢接口监控 |
| `sloth.monitor.slow-api-threshold` | `long` | `3000` | 慢接口阈值（毫秒） |
| **告警配置** | | | |
| `sloth.monitor.alarm.enabled` | `boolean` | `false` | 是否启用告警 |
| `sloth.monitor.alarm.type` | `String` | `dingtalk` | 告警类型（dingtalk / wechat） |
| `sloth.monitor.alarm.webhook` | `String` | `null` | Webhook 地址 |
| `sloth.monitor.alarm.secret` | `String` | `null` | Webhook 签名密钥 |
| `sloth.monitor.alarm.cpu-threshold` | `double` | `80.0` | CPU 使用率告警阈值（%） |
| `sloth.monitor.alarm.memory-threshold` | `double` | `80.0` | 内存使用率告警阈值（%） |
| `sloth.monitor.alarm.disk-threshold` | `double` | `90.0` | 磁盘使用率告警阈值（%） |
| **链路追踪** | | | |
| `sloth.monitor.tracing.enabled` | `boolean` | `true` | 是否启用链路追踪 |
| `sloth.monitor.tracing.sampler-rate` | `double` | `1.0` | 链路采样率（0.0 ~ 1.0） |
| **系统采集与健康检查** | | | |
| `sloth.monitor.collect-interval` | `long` | `60000` | 系统资源采集间隔（毫秒） |
| `sloth.monitor.health.connect-timeout` | `int` | `3000` | 健康检查连接超时（毫秒） |
| `sloth.monitor.health.read-timeout` | `int` | `3000` | 健康检查读取超时（毫秒） |

---

## sloth.gateway.*

Gateway 网关模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.gateway.enabled` | `boolean` | `true` | 是否启用 Gateway Starter |
| `sloth.gateway.white-list` | `Set<String>` | `[]` | 白名单路径 |
| `sloth.gateway.dynamic-route-enabled` | `boolean` | `true` | 是否启用动态路由 |
| `sloth.gateway.black-list` | `Set<String>` | `[]` | IP 黑名单 |
| **重试配置** | | | |
| `sloth.gateway.retry.enabled` | `boolean` | `false` | 是否启用全局重试 |
| `sloth.gateway.retry.max-attempts` | `int` | `3` | 最大重试次数 |
| `sloth.gateway.retry.backoff-ms` | `long` | `100` | 重试退避间隔（毫秒） |

---

## sloth.thread-pool.*

线程池模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.thread-pool.enabled` | `boolean` | `true` | 是否启用线程池 Starter |
| `sloth.thread-pool.virtual-enabled` | `boolean` | `false` | 是否启用 Java 21 虚拟线程 |
| **线程池配置（按名称）** | | | |
| `sloth.thread-pool.pools.<name>.core-size` | `int` | `8` | 核心线程数 |
| `sloth.thread-pool.pools.<name>.max-size` | `int` | `32` | 最大线程数 |
| `sloth.thread-pool.pools.<name>.queue-capacity` | `int` | `1024` | 队列容量 |
| `sloth.thread-pool.pools.<name>.keep-alive-time` | `int` | `60` | 空闲线程存活时间（秒） |
| `sloth.thread-pool.pools.<name>.thread-name-prefix` | `String` | `sloth-async-` | 线程名前缀 |
| `sloth.thread-pool.pools.<name>.rejected-policy` | `String` | `CALLER_RUNS` | 拒绝策略 |

> 默认内置 `default` 和 `scheduled` 两个线程池。`default` 池 coreSize=8, maxSize=32；`scheduled` 池 coreSize=4, maxSize=4。

---

## sloth.mq.*

MQ 消息队列模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.mq.enabled` | `boolean` | `true` | 是否启用 MQ Starter |
| `sloth.mq.idempotent-enabled` | `boolean` | `true` | 是否启用消费幂等 |
| `sloth.mq.max-retry` | `int` | `3` | 最大重试次数 |
| `sloth.mq.transaction-producer-group` | `String` | `sloth-tx-producer-group` | 默认事务生产者组 |
| `sloth.mq.consume-idempotent-key-prefix` | `String` | `sloth:mq:consume:` | 消费幂等键前缀 |

---

## sloth.oss.*

OSS 对象存储模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.oss.type` | `String` | `minio` | OSS 类型 |
| `sloth.oss.endpoint` | `String` | `null` | 服务端点 |
| `sloth.oss.access-key` | `String` | `null` | AccessKey |
| `sloth.oss.secret-key` | `String` | `null` | SecretKey |
| `sloth.oss.bucket-name` | `String` | `null` | Bucket 名称 |
| `sloth.oss.region` | `String` | `null` | 区域 |
| `sloth.oss.domain` | `String` | `null` | 访问域名 |

---

## sloth.sms.*

短信模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.sms.enabled` | `boolean` | `true` | 是否启用 |
| `sloth.sms.type` | `String` | `aliyun` | 短信供应商类型 |
| `sloth.sms.access-key-id` | `String` | `null` | 访问 Key Id |
| `sloth.sms.access-key-secret` | `String` | `null` | 访问 Key Secret |
| `sloth.sms.sign-name` | `String` | `null` | 短信签名 |
| `sloth.sms.region-id` | `String` | `cn-hangzhou` | 区域 ID |

---

## sloth.sentinel.*

Sentinel 限流模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.sentinel.enabled` | `boolean` | `true` | 是否启用 Sentinel Starter |
| `sloth.sentinel.datasource` | `String` | `nacos` | 数据源类型 |
| `sloth.sentinel.nacos-group-id` | `String` | `SENTINEL_GROUP` | Nacos Group ID |
| `sloth.sentinel.nacos-data-id` | `String` | `null` | Nacos Data ID（限流规则配置文件名） |
| `sloth.sentinel.nacos-namespace` | `String` | `null` | Nacos 命名空间 |

---

## sloth.seata.*

Seata 分布式事务模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.seata.enabled` | `boolean` | `false` | 是否启用 |
| `sloth.seata.tx-service-group` | `String` | `${spring.application.name}-tx-group` | 事务分组 |
| `sloth.seata.mode` | `String` | `AT` | 事务模式（AT / TCC / SAGA / XA） |

---

## sloth.doc.*

接口文档（Swagger/Knife4j）模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.doc.enabled` | `boolean` | `true` | 是否启用接口文档 |
| `sloth.doc.title` | `String` | `Sloth Boot API` | 接口文档标题 |
| `sloth.doc.description` | `String` | `Sloth Boot 接口文档` | 接口文档描述 |
| `sloth.doc.version` | `String` | `1.0.0` | 接口文档版本 |
| `sloth.doc.contact-name` | `String` | `sloth-boot` | 联系人名称 |
| `sloth.doc.contact-email` | `String` | `sloth-boot@example.com` | 联系人邮箱 |
| `sloth.doc.contact-url` | `String` | `https://github.com/GuoHuaijian/SlothBoot` | 联系人主页或仓库地址 |
| `sloth.doc.license` | `String` | `Apache 2.0` | 开源许可证名称 |
| `sloth.doc.base-packages` | `List<String>` | `[com.sloth.boot]` | 扫描的基础包 |
| `sloth.doc.security-scheme-enabled` | `boolean` | `true` | 是否启用 Bearer Token 安全方案 |
| `sloth.doc.security-scheme-name` | `String` | `Bearer` | 安全方案名称 |
| `sloth.doc.security-bearer-format` | `String` | `JWT` | Bearer Token 格式 |
| `sloth.doc.security-description` | `String` | `请输入 Token（无需 Bearer 前缀）` | 安全方案描述 |
| `sloth.doc.server-url` | `String` | `null` | 服务器地址（不配置则自动检测） |
| `sloth.doc.server-description` | `String` | `默认服务器` | 服务器描述 |

---

## sloth.log.*

日志模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.log.enabled` | `boolean` | `true` | 是否启用 |
| `sloth.log.print-request-log` | `boolean` | `true` | 是否打印请求日志 |
| `sloth.log.print-response-log` | `boolean` | `false` | 是否打印响应日志 |
| `sloth.log.exclude-urls` | `Set<String>` | `[]` | 排除的 URL |
| `sloth.log.max-body-length` | `int` | `2048` | 请求/响应体最大打印长度 |

---

## sloth.job.*

XXL-Job 定时任务模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.job.enabled` | `boolean` | `true` | 是否启用 Job Starter |
| `sloth.job.admin-addresses` | `String` | `null` | XXL-Job Admin 地址 |
| `sloth.job.access-token` | `String` | `null` | 访问令牌 |
| `sloth.job.appname` | `String` | `null` | 执行器AppName |
| `sloth.job.address` | `String` | `null` | 执行器地址 |
| `sloth.job.ip` | `String` | `null` | 执行器IP |
| `sloth.job.port` | `int` | `9999` | 执行器端口 |
| `sloth.job.log-path` | `String` | `./logs/xxl-job` | 日志路径 |
| `sloth.job.log-retention-days` | `int` | `30` | 日志保留天数 |

---

## sloth.es.*

Elasticsearch 模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.es.enabled` | `boolean` | `true` | 是否启用 |
| `sloth.es.default-index` | `String` | `null` | 默认索引 |
| `sloth.es.timeout` | `long` | `5` | 查询超时时间（秒） |

---

## sloth.excel.*

Excel 导入导出模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.excel.enabled` | `boolean` | `true` | 是否启用 Excel Starter |
| **导入配置** | | | |
| `sloth.excel.import-config.batch-size` | `int` | `1000` | 导入批处理大小 |
| `sloth.excel.import-config.ignore-errors` | `boolean` | `false` | 是否忽略行级错误 |
| `sloth.excel.import-config.validate` | `boolean` | `false` | 是否启用数据校验 |
| `sloth.excel.import-config.head-row-number` | `int` | `1` | 表头所在行号 |
| **导出配置** | | | |
| `sloth.excel.export-config.default-sheet-name` | `String` | `Sheet1` | 默认 Sheet 名称 |
| `sloth.excel.export-config.auto-size-column` | `boolean` | `true` | 是否自动调整列宽 |
| `sloth.excel.export-config.style.header-background-color` | `String` | `#F2F2F2` | 表头背景色 |
| `sloth.excel.export-config.style.header-font-color` | `String` | `#000000` | 表头字体颜色 |
| `sloth.excel.export-config.style.header-font-bold` | `boolean` | `true` | 表头是否加粗 |
| `sloth.excel.export-config.style.header-font-size` | `short` | `11` | 表头字号 |

---

## sloth.idempotent.*

接口幂等模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.idempotent.enabled` | `boolean` | `true` | 是否启用 |
| `sloth.idempotent.timeout` | `int` | `10` | 超时时间（秒） |
| `sloth.idempotent.key-prefix` | `String` | `idempotent:` | Key 前缀 |

---

## sloth.feign.*

Feign 远程调用模块配置。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.feign.enabled` | `boolean` | `true` | 是否启用 Feign Starter |
| `sloth.feign.connect-timeout` | `long` | `5` | 连接超时时间（秒） |
| `sloth.feign.read-timeout` | `long` | `10` | 读取超时时间（秒） |
| `sloth.feign.write-timeout` | `long` | `10` | 写入超时时间（秒） |
| `sloth.feign.max-idle-connections` | `int` | `200` | 连接池最大空闲连接数 |
| `sloth.feign.keep-alive-minutes` | `long` | `5` | 连接池空闲连接存活时间（分钟） |
| `sloth.feign.sentinel-enabled` | `boolean` | `false` | 是否启用 Sentinel 集成 |

---

## sloth.xss.*

XSS 防护配置（属于 common-security 模块）。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.xss.enabled` | `boolean` | `true` | 是否启用 XSS 过滤 |
| `sloth.xss.exclude-urls` | `Set<String>` | `[]` | 排除的 URL |
| `sloth.xss.clean-html` | `boolean` | `true` | 是否清理 HTML 标签 |
| `sloth.xss.clean-java-script` | `boolean` | `true` | 是否清理 JavaScript |
| `sloth.xss.clean-css` | `boolean` | `true` | 是否清理 CSS |
| `sloth.xss.clean-event-attributes` | `boolean` | `true` | 是否清理事件属性 |

---

## sloth.sign.*

请求签名验证配置（属于 common-security 模块）。

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.sign.enabled` | `boolean` | `false` | 是否启用签名验证 |
| `sloth.sign.secret-key` | `String` | `null` | 密钥 |
| `sloth.sign.valid-time` | `int` | `300` | 有效时间（秒） |
| `sloth.sign.exclude-paths` | `Set<String>` | `[]` | 排除的路径 |
