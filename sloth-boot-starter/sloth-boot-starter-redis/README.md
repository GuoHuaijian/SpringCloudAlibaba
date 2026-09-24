# Sloth Boot Starter Redis

## 简介

`sloth-boot-starter-redis` 提供 Redis 增强能力，包括分布式锁（`@DistributedLock`）、限流（`@RateLimit`）、幂等控制（`@Idempotent`）、延迟队列和 `RedisCacheUtil` 工具类。基于 Redisson 实现分布式锁和延迟队列，基于 Lua 脚本实现滑动窗口限流。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-redis</artifactId>
</dependency>

<!-- 分布式锁和延迟队列需要 Redisson -->
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
</dependency>
```

## 配置项

| 配置键 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.redis.enabled` | boolean | `true` | 是否启用 Redis Starter |
| `sloth.redis.key-prefix` | String | `sloth:` | 统一业务 key 前缀 |
| `sloth.redis.lock-wait-time` | long | `3` | 分布式锁默认等待时间（秒） |
| `sloth.redis.lock-lease-time` | long | `30` | 分布式锁默认租约时间（秒） |
| `sloth.redis.enable-type-info` | boolean | `false` | JSON 序列化时是否携带类型信息（默认关闭，开启后仅允许白名单包前缀） |
| `sloth.redis.null-value-expire-seconds` | long | `60` | 空值缓存过期时间（秒） |

## 核心组件

| 组件 | 说明 |
|------|------|
| `RedisCacheUtil` | Redis 缓存工具类，封装 String/Hash/List/Set/ZSet 操作，支持缓存穿透保护和逻辑过期 |
| `@DistributedLock` | 分布式锁注解，支持 SpEL 表达式动态 key |
| `@RateLimit` | 限流注解，基于 Redis Lua 脚本的滑动窗口限流 |
| `@Idempotent` | 幂等注解，防止重复提交 |
| `RedisDelayQueue` | Redis 延迟队列，基于 Redisson 的 `RBlockingQueue` |
| `DistributedLockAspect` | 分布式锁切面 |
| `RateLimiterAspect` | 限流切面 |
| `IdempotentAspect` | 幂等切面 |

## 使用示例

### @DistributedLock 分布式锁

```java
@Service
public class OrderService {

    @DistributedLock(key = "'order:' + #orderId", waitTime = 5, leaseTime = 30)
    public void processOrder(Long orderId) {
        // 同一 orderId 同时只有一个线程执行
    }
}
```

### @RateLimit 限流

```java
@RestController
public class ApiController {

    // 每个 IP 60 秒内最多 100 次请求
    @RateLimit(count = 100, period = 60, type = LimitType.IP)
    @GetMapping("/api/data")
    public R<List<Data>> getData() {
        return R.ok(dataService.list());
    }

    // 每个用户 60 秒内最多 10 次请求，SpEL 表达式 key
    @RateLimit(count = 10, period = 60, type = LimitType.USER,
               message = "操作过于频繁，请稍后再试")
    @PostMapping("/api/submit")
    public R<Void> submit() {
        return R.ok();
    }
}
```

### @Idempotent 幂等控制

```java
@Service
public class PayService {

    // 10 秒内相同请求参数不允许重复提交
    @Idempotent(timeout = 10, key = "#payOrder.orderNo", message = "请勿重复支付")
    public void pay(PayOrder payOrder) {
        // 支付逻辑
    }
}
```

### RedisCacheUtil 使用

```java
@Service
public class UserService {

    private final RedisCacheUtil redisCacheUtil;

    // 基础操作
    public void basicOps() {
        redisCacheUtil.set("user:1", userObj, Duration.ofMinutes(30));
        User user = redisCacheUtil.get("user:1", User.class);
        redisCacheUtil.delete("user:1");
    }

    // Hash 操作
    public void hashOps() {
        redisCacheUtil.hSet("user:info", "name", "张三");
        String name = redisCacheUtil.hGet("user:info", "name", String.class);
    }

    // 缓存穿透保护：未命中时加载并回填，空值也会缓存
    public User getUserWithCache(Long userId) {
        return redisCacheUtil.getOrLoad(
            "user:" + userId, User.class,
            () -> userMapper.selectById(userId),
            Duration.ofMinutes(30)
        );
    }

    // 逻辑过期：缓存不阻塞读请求，过期后异步重建
    public User getUserWithLogicalExpire(Long userId) {
        return redisCacheUtil.getWithLogicalExpire(
            "user:" + userId, User.class,
            () -> userMapper.selectById(userId),
            Duration.ofHours(2)
        );
    }
}
```

## FAQ

**Q: 分布式锁 `@DistributedLock` 不生效？**
A: 项目中需要引入 `redisson-spring-boot-starter` 依赖，分布式锁基于 Redisson 实现。

**Q: 限流支持哪些维度？**
A: `LimitType.IP` 按客户端 IP 限流，`LimitType.USER` 按登录用户限流，`LimitType.DEFAULT` 按方法签名限流。也支持通过 SpEL 自定义 key。

**Q: `RedisCacheUtil.getOrLoad` 如何防止缓存穿透？**
A: 当数据库查询结果为 null 时，会缓存一个空值标记（默认 60 秒），避免相同 key 反复穿透到数据库。可通过 `sloth.redis.null-value-expire-seconds` 调整。
**Q: `key-prefix` 前缀如何生效？**
A: `RedisCacheUtil` 所有操作自动拼接 `sloth.redis.key-prefix`（默认 `sloth:`），传入的 key 无需手动添加前缀。
