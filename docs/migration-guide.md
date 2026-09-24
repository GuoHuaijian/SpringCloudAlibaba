# 迁移指南

## 从 Spring Boot 原生项目迁移到 SlothBoot

### 1. 修改 parent POM

```xml
<!-- 方式一：继承 sloth-boot-parent -->
<parent>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-parent</artifactId>
    <version>${sloth.version}</version>
</parent>

<!-- 方式二：引入 BOM -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.sloth.boot</groupId>
            <artifactId>sloth-boot-dependencies</artifactId>
            <version>${sloth.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2. 替换通用配置

SlothBoot 使用 `sloth.*` 配置前缀，部分 Spring Boot 原生配置可简化：

| 原生配置 | SlothBoot 配置 |
|----------|---------------|
| `spring.jackson.*` | 由 `sloth-boot-starter-web` 自动配置 |
| 手写全局异常处理 | 由 `GlobalExceptionHandler` 自动处理 |
| 手写响应包装 | 由 `GlobalResponseAdvice` 自动包装 |
| 手写 CORS 配置 | `sloth.web.cors.*` |
| 手写 Swagger 配置 | `sloth.doc.*` |

### 3. 替换工具类

| 原有工具 | SlothBoot 替代 |
|----------|---------------|
| 自定义 Result/Response 类 | `R<T>` |
| 自定义 BaseEntity | `com.sloth.boot.common.base.BaseEntity` |
| 自定义分页参数 | `BaseQuery` + `PageResult` |
| 手写 Redis 工具类 | `RedisCacheUtil` |
| 手写分布式锁 | `@DistributedLock` 注解 |

### 4. 注意事项

- SlothBoot 基于 Java 21，确保项目 JDK 版本 >= 21
- 使用 Jakarta 命名空间（`jakarta.servlet`、`jakarta.validation`），不兼容 `javax.*`
- Sa-Token 替代 Spring Security 进行认证授权
- 默认关闭 Swagger 文档，生产环境建议保持关闭

## 版本升级

### 升级到 1.0.0-RC1

1. `sloth.mybatis.encrypt-key` 变为必填配置（如果使用了加密字段处理器）
2. `AuthAutoConfiguration` 不再使用 `@ComponentScan`，`DefaultStpInterface` 改为显式 `@Bean` 注册
3. `RedisBloomFilter` 构造函数新增 `name` 参数，可通过 `sloth.redis.bloom-filter.name` 配置
