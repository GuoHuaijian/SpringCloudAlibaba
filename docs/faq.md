# 常见问题 (FAQ)

## 通用问题

### Q: SlothBoot 和 Spring Boot 是什么关系？

SlothBoot 是基于 Spring Boot 4.x + Spring Cloud Alibaba 的企业级开发脚手架，不是 Spring Boot 的替代品。它在 Spring Boot 基础上提供了开箱即用的企业级组件（认证、日志、监控、分布式锁等），减少重复配置。

### Q: 支持哪些 Java 版本？

SlothBoot 要求 Java 21+。基于 Spring Boot 4.0.x 的最低要求，并利用了 Java 21 的虚拟线程等新特性。

### Q: 如何只使用部分模块？

SlothBoot 采用模块化设计，按需引入即可。例如只需要 Web + Redis：

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-redis</artifactId>
</dependency>
```

### Q: 配置前缀是什么？

所有配置统一使用 `sloth.*` 前缀，例如 `sloth.web.*`、`sloth.redis.*`、`sloth.auth.*`。

## 认证授权

### Q: 如何自定义权限校验逻辑？

实现 `PermissionService` 接口并注册为 Spring Bean，会自动覆盖默认实现：

```java
@Component
public class CustomPermissionService implements PermissionService {
    @Override
    public boolean hasPermission(String permission) {
        // 从数据库/缓存查询权限
        return true;
    }
}
```

### Q: Sa-Token 的 token 从哪里获取？

默认从 HTTP Header `Authorization` 或 Query 参数 `satoken` 获取。可在 Sa-Token 官方文档中配置其他方式。

## 数据库

### Q: 使用加密字段处理器报错怎么办？

`sloth.mybatis.encrypt-key` 是必填配置。如果启用了 `@TableField(typeHandler = EncryptTypeHandler.class)`，必须在配置文件中设置加密密钥：

```yaml
sloth:
  mybatis:
    encrypt-key: your-secret-key-here
```

### Q: 多数据源如何配置？

引入 `dynamic-datasource-spring-boot3-starter` 并使用 `@DS` 注解切换数据源。SlothBoot 的 MyBatis 模块已兼容多数据源场景。

## Redis

### Q: 分布式锁支持哪些特性？

基于 Redisson 实现，支持：
- 可重入锁 (`tryLock` / `unlock`)
- 读写锁 (`DistributedReadWriteLock`)
- 注解式锁 (`@DistributedLock`)
- 自动续期（看门狗机制）

### Q: 布隆过滤器如何自定义名称？

```yaml
sloth:
  redis:
    bloom-filter:
      enabled: true
      name: "my:bloom:filter"
      expected-insertions: 1000000
      false-positive-probability: 0.01
```

## 线程池

### Q: 虚拟线程和传统线程池如何选择？

- **I/O 密集型任务**（HTTP 调用、数据库查询）：使用虚拟线程 `sloth.thread-pool.virtual-enabled=true`
- **CPU 密集型任务**（计算、加密）：使用传统线程池
- 两者可同时启用，通过 Bean 名称区分使用

### Q: 如何动态调整线程池参数？

通过 Actuator 端点 `/actuator/threadPools`：

```bash
# 查看所有线程池
curl http://localhost:8080/actuator/threadPools

# 调整参数
curl -X POST http://localhost:8080/actuator/threadPools/default \
  -d '{"coreSize": 16, "maxSize": 64}'
```
