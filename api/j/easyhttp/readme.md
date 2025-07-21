# EasyHttp 项目测试

## 项目简介

EasyHttp 是一个基于 Spring Boot 的 HTTP 客户端框架，提供了简洁易用的 HTTP 调用方式，支持声明式接口调用。

下面我写一个例子来实际集成和测试一下。

## 运行条件

- **Java 17** 或更高版本
- **Spring Boot 3.5.3**
- **Maven 3.6+**

## 项目架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        EasyHttp测试项目架构图                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────┐                ┌─────────────────┐         │
│  │  easyhttp-app   │                │ easyhttp-appstart│         │
│  │   (原生方式)     │                │(Bootstart方式)   │         │
│  │   Port: 8081    │                │   Port: 8083    │         │
│  └─────────┬───────┘                └─────────┬───────┘         │
│            │                                  │                 │
│            │ EasyHTTP调用                     │ EasyHTTP调用     │
│            │                                  │                 │
│            └──────────┬───────────────────────┘                 │
│                       │                                         │
│                       ▼                                         │
│            ┌─────────────────────────────┐                     │
│            │      easyhttp-auth          │                     │
│            │      (认证服务)              │                     │
│            │      Port: 8082             │                     │
│            └─────────────────────────────┘                     │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                   核心组件层次                           │   │
│  │                                                         │   │
│  │  ┌─────────────────────────────┐                       │   │
│  │  │  easy-http-boot-starter     │                       │   │
│  │  │    (SpringBoot自动配置)      │                       │   │
│  │  └─────────┬───────────────────┘                       │   │
│  │            │                                           │   │
│  │            ▼                                           │   │
│  │  ┌─────────────────────────────┐                       │   │
│  │  │       easy-http             │                       │   │
│  │  │      (核心库)                │                       │   │
│  │  └─────────────────────────────┘                       │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## 测试步骤

### 方式一：测试easyhttp-app (原生方式)

1. **编译项目**
   ```bash
   # 使用提供的批处理文件编译
   .\compile_all.bat
   ```

2. **启动认证服务**
   ```bash
   cd easyhttp-auth
   mvn spring-boot:run
   ```
   服务将在 `http://localhost:8082` 启动

3. **启动测试应用**
   ```bash
   cd easyhttp-app
   mvn spring-boot:run
   ```
   应用将在 `http://localhost:8081` 启动

4. **测试接口**
   - 访问：`http://localhost:8081/testGet?id=1000`
   - 查看控制台日志，验证 HTTP 调用是否成功

### 方式二：测试easyhttp-appstart (SpringBoot Starter方式)

1. **编译并安装依赖**
   ```bash
   # 安装 easy-http-boot-starter 到本地仓库
   .\install_easyhttp.bat
   ```

2. **启动认证服务**
   ```bash
   cd easyhttp-auth
   mvn spring-boot:run
   ```
   服务将在 `http://localhost:8082` 启动

3. **启动测试应用**
   ```bash
   # 使用提供的批处理文件启动
   .\run_appstart.bat
   ```
   应用将在 `http://localhost:8083` 启动

4. **测试接口**
   - 基础测试：`http://localhost:8083/api/testGet?id=123`
   - 获取图书：`http://localhost:8083/api/books/123`
   - 根据作者查询：`http://localhost:8083/api/books/author/张三`
   - 查询图书列表：`http://localhost:8083/api/books?author=李四&publisher=人民出版社`
   - 异步获取图书：`http://localhost:8083/api/books/123/async`
   - 健康检查：`http://localhost:8083/api/health`
   - 查看控制台日志，验证 HTTP 调用是否成功

## EasyHttp 使用示例 (SpringBoot Starter方式)

### 1. 添加依赖

在 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>com.github.vizaizai</groupId>
    <artifactId>easy-http-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置文件

在 `application.properties` 中配置：

```properties
# 应用配置
spring.application.name=easyhttp-app
server.port=8083

# EasyHttp 配置
easy-http.base-endpoints.auth=http://localhost:8082/
easy-http.retry.enable=true
easy-http.retry.max-attempts=1
easy-http.retry.interval-time=0
easy-http.request-log=true
```

### 3. 启用 EasyHttp

在主类上添加 `@EnableEasyHttp` 注解：

```java
@SpringBootApplication
@EnableEasyHttp
public class EasyhttpAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyhttpAppApplication.class, args);
    }
}
```

### 4. 定义 HTTP 客户端接口

```java
@EasyHttpClient(value = "auth")
public interface BookApiClient {
    
    /**
     * 根据ID获取图书信息
     */
    @Get("/books")
    ApiResult<Book> getBookById(@Param("id") String id);
    
    /**
     * 根据作者查询图书
     */
    @Get("/books?author={author}")
    ApiResult<Book> getBookByAuthor(@Var("author") String author);
    
    /**
     * 根据参数查询图书列表
     */
    @Get("/books")
    ApiResult<List<Book>> getBooksByParams(@Param Map<String, Object> params);
    
    /**
     * 创建图书
     */
    @Post("/books")
    ApiResult<String> createBook(@Body Book book);
    
    /**
     * 更新图书
     */
    @Put("/books/{id}")
    ApiResult<String> updateBook(@Var("id") String id, @Body Book book);
    
    /**
     * 删除图书
     */
    @Delete("/books/{id}")
    ApiResult<String> deleteBook(@Var("id") String id);
    
    /**
     * 异步获取图书
     */
    @Get("/books")
    CompletableFuture<ApiResult<Book>> getBookByIdAsync(@Param("id") String id);
    
    /**
     * 带自定义请求头创建图书
     */
    @Headers({"Content-Type: application/json", "Client: EasyHttp"})
    @Post("/books")
    ApiResult<String> createBookWithHeaders(@Body Book book, @Headers Map<String, String> headers);
    
    /**
     * 表单方式创建图书
     */
    @Post("/books/form")
    ApiResult<String> createBookByForm(@Param Book book);
}
```

### 5. 使用 HTTP 客户端

```java
@RestController
@RequestMapping("/api")
public class ReqOutController {
    
    private final BookHttpService bookHttpService;
    private final BookApiClient bookApiClient;
    
    public ReqOutController(BookHttpService bookHttpService, BookApiClient bookApiClient) {
        this.bookHttpService = bookHttpService;
        this.bookApiClient = bookApiClient;
    }
    
    /**
     * 基础测试接口
     */
    @GetMapping("/testGet")
    public Object testGet(@RequestParam String id) {
        try {
            return bookHttpService.getBookById(id);
        } catch (Exception e) {
            return ApiResult.error("调用失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取图书信息
     */
    @GetMapping("/books/{id}")
    public ApiResult<Book> getBook(@PathVariable String id) {
        try {
            return bookApiClient.getBookById(id);
        } catch (Exception e) {
            return ApiResult.error("获取图书失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建图书
     */
    @PostMapping("/books")
    public ApiResult<String> createBook(@RequestBody Book book) {
        try {
            return bookApiClient.createBook(book);
        } catch (Exception e) {
            return ApiResult.error("创建图书失败: " + e.getMessage());
        }
    }
    
    /**
     * 异步获取图书
     */
    @GetMapping("/books/{id}/async")
    public CompletableFuture<ApiResult<Book>> getBookAsync(@PathVariable String id) {
        return bookApiClient.getBookByIdAsync(id)
            .exceptionally(ex -> ApiResult.error("异步调用失败: " + ex.getMessage()));
    }
}
```

### 6. 数据模型

```java
/**
 * 图书实体类
 */
@Data
public class Book {
    private String id;
    private String name;
    private String author;
    private String isbn;
    private String publisher;
    private String publishDate;
    private BigDecimal price;

}

/**
 * 通用API响应结果封装类
 */
@Data
public class ApiResult<T> {
    private int code;
    private String message;
    private T data;
    
    public ApiResult() {}
    
    public ApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    // 成功响应
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "success", data);
    }
    
    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(200, message, data);
    }
    
    // 失败响应
    public static <T> ApiResult<T> error(String message) {
        return new ApiResult<>(500, message, null);
    }
    
    public static <T> ApiResult<T> error(int code, String message) {
        return new ApiResult<>(code, message, null);
    }
    
    // getter/setter 方法
}
```

## 注解说明

### HTTP方法注解
- `@Get` - GET请求
- `@Post` - POST请求
- `@Put` - PUT请求
- `@Delete` - DELETE请求
- `@Patch` - PATCH请求

### 参数注解
- `@Param` - 查询参数，会拼接到URL后面
- `@Var` - 路径变量，替换URL中的占位符
- `@Body` - 请求体，用于POST/PUT等请求
- `@Headers` - 自定义请求头

### 客户端注解
- `@EasyHttpClient` - 标记HTTP客户端接口
- `@EnableEasyHttp` - 启用EasyHttp自动配置

### 使用示例
```java
// 查询参数：GET /books?id=123&author=张三
@Get("/books")
ApiResult<Book> getBook(@Param("id") String id, @Param("author") String author);

// 路径变量：GET /books/123
@Get("/books/{id}")
ApiResult<Book> getBookById(@Var("id") String id);

// 请求体：POST /books
@Post("/books")
ApiResult<String> createBook(@Body Book book);

// 自定义请求头
@Headers({"Content-Type: application/json"})
@Post("/books")
ApiResult<String> createBookWithHeaders(@Body Book book);
```

## 特性说明

- **声明式接口**：通过注解定义 HTTP 接口，无需手动编写 HTTP 调用代码
- **自动配置**：SpringBoot Starter 提供自动配置，开箱即用
- **多种参数支持**：支持 @Param、@Var、@Body、@Headers 等注解
- **异步调用**：支持 CompletableFuture 异步调用
- **重试机制**：内置重试机制，提高调用成功率
- **请求日志**：可配置请求日志，便于调试
- **灵活配置**：支持多个服务端点配置

## 注意事项

1. 确保目标服务已启动并可访问
2. 检查网络连接和防火墙设置
3. 配置正确的服务端点地址
4. 注意 Java 版本兼容性（需要 Java 17+）
5. 确保 Spring Boot 版本为 3.5.3

## 重试功能详解

### 重试机制配置

EasyHttp 提供了强大的重试机制，可以在网络不稳定或服务暂时不可用时自动重试请求。

#### 全局配置
在 `application.properties` 中配置全局重试参数：

```properties
# 启用重试机制
easy-http.retry.enable=true
# 最大重试次数
easy-http.retry.max-attempts=3
# 重试间隔时间（毫秒）
easy-http.retry.interval-time=1000
```

#### 方法级别配置
使用 HTTP 方法注解的 `retries` 和 `interval` 参数为特定方法配置重试：

```java
/**
 * 重试示例：获取图书信息（带重试机制）
 * 当请求失败时会自动重试，最多重试3次，每次间隔1秒
 */
@Get(value = "/books/{id}", retries = 3, interval = 1000)
ApiResult<Book> getBookByIdWithRetry(@Var("id") String id);

/**
 * 重试示例：模拟不稳定的接口调用
 * 最多重试5次，每次间隔2秒
 */
@Get(value = "/books/{id}/unstable", retries = 5, interval = 2000)
ApiResult<Book> getBookFromUnstableService(@Var("id") String id);
```

### 重试功能测试

#### 测试接口

1. **基础重试测试**
   ```
   GET http://localhost:8083/api/books/300/retry
   ```
   测试正常接口的重试功能（通常第一次就成功）

2. **不稳定服务重试测试**
   ```
   POST http://localhost:8083/api/books/300/unstable
   ```
   测试不稳定服务的重试功能（70%概率失败，展示重试效果）

3. **使用测试脚本**
   ```
   ./retryTest.sh http://localhost:8083 300 5
   ```
   使用提供的测试脚本进行批量重试测试

#### 测试步骤

1. 启动 easyhttp-auth 服务（端口8082）
2. 启动 easyhttp-appstart 服务（端口8083）
3. 测试GET方法重试：访问 `http://localhost:8083/api/books/300/retry`
4. 测试POST方法重试：发送POST请求到 `http://localhost:8083/api/books/300/unstable`
5. 使用测试脚本：运行 `./retryTest.sh http://localhost:8083 300 5`
6. 观察控制台日志，查看重试过程

#### 预期结果

- **GET方法重试**：正常返回图书信息，无重试行为（因为接口稳定）
- **POST方法重试**：由于70%失败率，会触发重试机制，最多重试5次，间隔50ms
- **测试脚本结果**：显示每次请求的成功/失败状态，统计成功率
- **日志输出**：控制台会显示每次重试的详细信息，包括重试次数和间隔时间
- **最终结果**：重试次数用尽后，返回最后一次的响应结果

### 重试机制实现原理

#### 核心组件

1. **重试拦截器**：在HTTP请求执行前后进行拦截
2. **重试策略**：根据配置决定是否需要重试
3. **异常处理**：识别可重试的异常类型
4. **延迟机制**：在重试之间添加延迟

#### 工作流程

```
1. 发起HTTP请求
   ↓
2. 请求执行
   ↓
3. 检查响应结果
   ↓
4. 如果失败且未达到最大重试次数
   ↓
5. 等待指定间隔时间
   ↓
6. 重新发起请求（回到步骤2）
   ↓
7. 返回最终结果
```

#### 重试触发条件

- 网络连接异常
- 服务器返回5xx错误
- 连接超时
- 读取超时
- 其他可重试的异常

#### 重试策略

- **固定间隔**：每次重试间隔时间固定
- **指数退避**：重试间隔时间逐渐增加（可扩展）
- **最大重试次数**：防止无限重试

#### 配置优先级

1. 方法级别的 HTTP 注解参数（最高优先级）
2. 全局配置文件设置
3. 框架默认值

### 最佳实践

1. **合理设置重试次数**：避免过多重试导致系统负载过高
2. **适当的重试间隔**：给服务恢复留出时间，POST请求建议间隔更短
3. **区分HTTP方法**：GET请求可以安全重试，POST请求需要确保幂等性
4. **区分异常类型**：只对可重试的异常进行重试
5. **监控重试指标**：记录重试次数和成功率
6. **熔断机制**：结合熔断器避免雪崩效应
7. **幂等性设计**：确保POST/PUT请求可以安全重试

## 故障排除

- **连接被拒绝**：检查目标服务是否启动，端口是否正确
- **Bean 创建失败**：检查自动配置是否正确加载
- **配置不生效**：检查配置文件格式和属性名称
- **编译错误**：确保使用 Java 17 和正确的 Maven 配置