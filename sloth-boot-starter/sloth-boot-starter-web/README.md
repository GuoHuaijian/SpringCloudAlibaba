# Sloth Boot Starter Web

## 简介

`sloth-boot-starter-web` 提供 Web 层通用能力，包括统一响应包装、全局异常处理、参数校验（手机/身份证/枚举）、XSS 防护和 CORS 跨域配置，开箱即用。

## Maven 依赖

```xml
<dependency>
    <groupId>com.sloth.boot</groupId>
    <artifactId>sloth-boot-starter-web</artifactId>
</dependency>
```

## 配置项

| 配置键 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sloth.web.response-wrapper` | boolean | `true` | 是否启用统一响应包装 |
| `sloth.xss.enabled` | boolean | `true` | 是否启用 XSS 过滤（common-security） |
| `sloth.xss.exclude-urls` | Set | `[]` | XSS 过滤排除的 URL 列表（common-security） |
| `sloth.web.cors.allowed-origins` | Set | `[]` | CORS 允许的来源域名 |
| `sloth.web.cors.allowed-methods` | List | `GET,POST,PUT,DELETE,OPTIONS` | CORS 允许的 HTTP 方法 |
| `sloth.web.cors.allowed-headers` | List | `*` | CORS 允许的请求头 |
| `sloth.web.cors.allow-credentials` | boolean | `true` | 是否允许携带 Cookie |
| `sloth.web.cors.max-age` | long | `3600` | 预检请求缓存时间（秒） |

## 核心组件

| 组件 | 说明 |
|------|------|
| `GlobalExceptionHandler` | 全局异常处理器，统一处理业务异常、参数校验异常、系统异常等 |
| `GlobalResponseAdvice` | 统一响应包装，自动将返回值包装为 `R<T>` 格式 |
| `@SkipResponseWrapper` | 标注在类或方法上，跳过统一响应包装 |
| `@Phone` | 手机号校验注解 |
| `@IdCard` | 身份证号校验注解 |
| `@EnumValue` | 枚举值校验注解 |
| `CorsConfiguration` | CORS 跨域配置 |
| `UserContextInterceptor` | 用户上下文拦截器 |

## 使用示例

### @SkipResponseWrapper 跳过响应包装

```java
// 方法级别
@RestController
public class FileController {

    @SkipResponseWrapper
    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        // 直接写入流，不进行 R<> 包装
    }
}

// 类级别
@SkipResponseWrapper
@RestController
public class ThirdPartyCallbackController {
    // 该控制器所有接口均跳过包装
}
```

### 参数校验

```java
@Data
public class UserForm {

    @NotBlank(message = "手机号不能为空")
    @Phone
    private String phone;

    @NotBlank(message = "身份证号不能为空")
    @IdCard
    private String idCard;
}
```

### 统一异常使用

```java
// 业务异常 - 返回 400
throw new BizException("用户不存在");

// 系统异常 - 返回 500
throw new SystemException("数据库连接失败");
```

### CORS 配置示例

```yaml
sloth:
  web:
    cors:
      allowed-origins:
        - https://www.example.com
      allowed-methods:
        - GET
        - POST
      allow-credentials: true
      max-age: 3600
```

## FAQ

**Q: 如何让某个接口返回原始数据而不包装成 `R<T>`？**
A: 在方法或类上添加 `@SkipResponseWrapper` 注解即可。Swagger/Knife4j 文档路径已内置排除。

**Q: XSS 过滤器如何排除特定接口？**
A: 配置 `sloth.xss.exclude-urls`，支持 Ant 风格路径匹配，如 `/api/file/**`。

**Q: 全局异常处理器与自定义 `@ExceptionHandler` 会冲突吗？**
A: 不会。Spring 优先使用 Controller 级别的 `@ExceptionHandler`，`GlobalExceptionHandler` 仅处理未被局部捕获的异常。

**Q: CORS 配置不生效？**
A: 确保 `allowed-origins` 中填写了具体的域名（而非 `*`），因为 `allow-credentials=true` 时浏览器要求来源不能为通配符。
