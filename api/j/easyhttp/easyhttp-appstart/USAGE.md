# EasyHttp SpringBoot Starter 使用示例

本项目演示了如何使用 EasyHttp SpringBoot Starter 进行 HTTP 客户端调用。

## 项目结构

```
src/main/java/com/joysky/ice/easyhttp/app/
├── EasyhttpAppApplication.java     # 主启动类
├── client/
│   └── BookApiClient.java          # HTTP客户端接口
├── model/
│   ├── ApiResult.java              # 统一响应结果封装
│   └── Book.java                   # 图书实体类
└── web/
    └── ReqOutController.java       # 控制器示例
```

## 核心组件说明

### 1. 主启动类 (EasyhttpAppApplication.java)

```java
@SpringBootApplication
@EnableEasyHttp  // 启用EasyHttp功能
public class EasyhttpAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyhttpAppApplication.class, args);
    }
}
```

### 2. HTTP客户端接口 (BookApiClient.java)

使用 `@EasyHttpClient` 注解定义HTTP客户端：

```java
@EasyHttpClient(value = "auth")  // 对应配置中的 easy-http.base-endpoints.auth
public interface BookApiClient {
    
    @Get("/books")
    ApiResult<Book> getBookById(@Param("id") String id);
    
    @Post("/books")
    ApiResult<String> createBook(@Body Book book);
    
    // 更多方法...
}
```

### 3. 数据模型

- **Book.java**: 图书实体类，包含id、name、author等字段
- **ApiResult.java**: 统一的API响应结果封装类

### 4. 控制器示例 (ReqOutController.java)

提供了完整的REST API示例，展示如何使用HTTP客户端。

## 配置说明

### application.properties

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

## API接口说明

### 基础接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/testGet?id={id}` | 原有测试接口 |
| GET | `/api/health` | 健康检查 |

### 图书管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/books/{id}` | 根据ID获取图书 |
| GET | `/api/books/author/{author}` | 根据作者查询图书 |
| GET | `/api/books?author={author}&publisher={publisher}` | 根据参数查询图书列表 |
| POST | `/api/books` | 创建新图书 |
| PUT | `/api/books/{id}` | 更新图书信息 |
| DELETE | `/api/books/{id}` | 删除图书 |

### 异步接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/books/{id}/async` | 异步获取图书信息 |
| GET | `/api/books/async` | 异步获取所有图书 |

### 示例接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/books/example` | 创建示例图书数据 |
| POST | `/api/books/with-headers` | 带自定义请求头创建图书 |

## 使用示例

### 1. 启动应用

```bash
# 确保认证服务已启动在8082端口
# 启动本应用
.\run_appstart.bat
```

### 2. 测试基础功能

```bash
# 健康检查
curl http://localhost:8083/api/health

# 测试获取图书
curl "http://localhost:8083/api/testGet?id=123"
```

### 3. 测试图书管理

```bash
# 创建示例图书
curl -X POST http://localhost:8083/api/books/example

# 获取图书信息
curl http://localhost:8083/api/books/example-001

# 根据作者查询
curl http://localhost:8083/api/books/author/EasyHttp%20Team
```

### 4. 测试异步调用

```bash
# 异步获取图书
curl http://localhost:8083/api/books/123/async

# 异步获取所有图书
curl http://localhost:8083/api/books/async
```

## 注解说明

### HTTP方法注解

- `@Get`: GET请求
- `@Post`: POST请求
- `@Put`: PUT请求
- `@Delete`: DELETE请求

### 参数注解

- `@Param`: 查询参数或表单参数
- `@Var`: 路径变量
- `@Body`: 请求体
- `@Headers`: 请求头

### 配置注解

- `@EasyHttpClient`: 标记HTTP客户端接口
- `@EnableEasyHttp`: 启用EasyHttp自动配置

## 错误处理

所有接口都包含了异常处理，返回统一的错误格式：

```json
{
  "code": 1,
  "message": "错误信息",
  "data": null
}
```

## 扩展功能

### 1. 自定义请求头

```java
@Headers({"Content-Type: application/json", "Client: EasyHttp"})
@Post("/books")
ApiResult<String> createBookWithHeaders(@Body Book book, @Headers Map<String, String> headers);
```

### 2. 异步调用

```java
@Get("/books")
CompletableFuture<ApiResult<Book>> getBookByIdAsync(@Param("id") String id);
```

### 3. 表单提交

```java
@Post("/books/form")
ApiResult<String> createBookByForm(@Param Book book);
```

## 注意事项

1. 确保目标服务（认证服务）已启动并可访问
2. 检查配置文件中的服务端点地址是否正确
3. 注意异常处理和错误日志
4. 异步调用需要正确处理CompletableFuture
5. 自定义请求头要注意格式和内容

## 故障排除

1. **连接被拒绝**: 检查目标服务是否启动
2. **Bean创建失败**: 检查@EnableEasyHttp注解是否添加
3. **配置不生效**: 检查application.properties配置格式
4. **接口调用失败**: 查看控制台日志和错误信息