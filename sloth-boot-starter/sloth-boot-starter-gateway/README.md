# Sloth Boot Starter Gateway

网关统一增强组件，基于 Spring Cloud Gateway 提供认证鉴权、链路追踪、IP 黑名单、请求日志、动态路由及全局异常处理能力。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-gateway</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `sloth.gateway.white-list` | `Set<String>` | `[]` | 白名单路径列表，匹配前缀跳过认证 |
| `sloth.gateway.black-list` | `Set<String>` | `[]` | IP 黑名单列表 |
| `sloth.gateway.dynamic-route-enabled` | `boolean` | `true` | 是否启用动态路由 |

## 核心组件

| 组件 | 说明 | 过滤器顺序 |
| --- | --- | --- |
| `TraceIdGlobalFilter` | 自动生成或透传 TraceId 到下游服务 | -100 |
| `AuthGlobalFilter` | Token 认证与用户信息透传，白名单路径自动放行 | -1 |
| `BlackListGlobalFilter` | 基于客户端 IP 的黑名单拦截 | -2 |
| `RequestLogGlobalFilter` | 请求方法、路径、耗时日志记录 | 0 |
| `DynamicRouteService` | 支持从配置中心动态加载路由规则 | - |
| `GatewayExceptionHandler` | 统一 JSON 格式异常响应 | - |

## 过滤器链执行顺序

```
请求 -> TraceIdGlobalFilter(-100)
     -> BlackListGlobalFilter(-2)
     -> AuthGlobalFilter(-1)
     -> RequestLogGlobalFilter(0)
     -> 下游服务
```

## 白名单与黑名单配置示例

```yaml
sloth:
  gateway:
    white-list:
      - /auth/login
      - /auth/register
      - /actuator/health
    black-list:
      - 192.168.1.100
      - 10.0.0.50
    dynamic-route-enabled: true
```

## 使用说明

引入依赖后自动生效，无需额外编码。认证过滤器会在非白名单请求中校验 `X-Token` 请求头，缺失则返回 `401`；黑名单 IP 直接返回 `403`。TraceId 会自动注入下游请求头 `X-Trace-Id`。

## FAQ

**Q: 如何自定义某个过滤器？**
A: 每个 Bean 均使用 `@ConditionalOnMissingBean` 注册，自定义同类型 Bean 即可覆盖。

**Q: 白名单匹配规则是什么？**
A: 使用 `String.startsWith` 前缀匹配，例如 `/auth` 会匹配 `/auth/login`、`/auth/register` 等所有以 `/auth` 开头的路径。

**Q: 动态路由如何对接 Nacos？**
A: `DynamicRouteService` 会读取 `spring.cloud.nacos.discovery.server-addr` 等 Nacos 配置，路由规则需按 Spring Cloud Gateway 的 `RouteDefinition` JSON 格式发布。
