# 用户注销接口使用指南

## 接口概述

用户注销接口提供了安全、高效的用户登出功能，支持清理用户会话缓存和记录注销日志。

## 接口详情

### 请求信息
- **接口地址**: `POST /api/users/logout`
- **请求格式**: `application/json`
- **响应格式**: `application/json`

### 请求参数

```json
{
    "username": "用户名/手机号/邮箱"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户标识符，支持用户名、手机号或邮箱 |

### 响应格式

#### 成功响应
```json
{
    "code": 200,
    "message": "操作成功",
    "data": "注销成功"
}
```

#### 失败响应
```json
{
    "code": 500,
    "message": "注销失败，用户不存在",
    "data": null
}
```

## 使用示例

### cURL 示例

```bash
curl -X POST http://localhost:8080/api/users/logout \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser@example.com"
  }'
```

### JavaScript 示例

```javascript
fetch('/api/users/logout', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({
        username: 'testuser@example.com'
    })
})
.then(response => response.json())
.then(data => {
    if (data.code === 200) {
        console.log('注销成功');
        // 跳转到登录页面
        window.location.href = '/login.html';
    } else {
        console.error('注销失败:', data.message);
    }
})
.catch(error => {
    console.error('请求失败:', error);
});
```

### Java 客户端示例

```java
// 使用 RestTemplate
RestTemplate restTemplate = new RestTemplate();
UserLogoutRequest request = new UserLogoutRequest();
request.setUsername("testuser@example.com");

R<String> response = restTemplate.postForObject(
    "http://localhost:8080/api/users/logout", 
    request, 
    R.class
);

if (response.getCode() == 200) {
    System.out.println("注销成功");
} else {
    System.out.println("注销失败: " + response.getMessage());
}
```

## 功能特性

### 🔒 安全特性
- **缓存清理**: 自动清理用户登录相关的缓存数据
- **日志记录**: 异步记录用户注销操作日志，包含IP地址等信息
- **异常处理**: 完善的异常处理机制，确保系统稳定性

### ⚡ 性能特性
- **智能路由**: 根据用户名格式自动选择最优查询策略
- **异步处理**: 注销日志记录采用异步处理，不影响主流程性能
- **性能监控**: 集成性能监控，记录注销请求数量和响应时间

### 📊 监控统计
- **请求统计**: 记录注销请求总数
- **响应时间**: 监控注销接口平均响应时间
- **缓存命中**: 统计缓存清理操作的执行情况

## 错误码说明

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 200 | 注销成功 | - |
| 500 | 注销失败，用户不存在 | 检查用户名是否正确 |
| 500 | 注销失败，请稍后重试 | 系统异常，稍后重试或联系管理员 |

## 最佳实践

### 前端集成建议
1. **用户体验**: 注销成功后自动跳转到登录页面
2. **错误处理**: 提供友好的错误提示信息
3. **加载状态**: 显示注销处理中的加载状态

### 安全建议
1. **HTTPS**: 生产环境建议使用HTTPS协议
2. **防重复提交**: 前端可添加防重复提交机制
3. **会话管理**: 注销后清理前端存储的用户信息

## 性能监控

可通过以下接口查看注销功能的性能统计：

```bash
# 获取性能统计报告
curl http://localhost:8080/api/performance/stats
```

统计信息包括：
- 注销请求总数
- 注销平均响应时间
- 缓存命中率等指标

## 技术实现

### 核心组件
- **UserController**: 注销接口控制器
- **UserService**: 用户业务逻辑服务
- **UserCacheService**: 用户缓存管理服务
- **AsyncUserService**: 异步任务处理服务
- **PerformanceMonitorService**: 性能监控服务

### 处理流程
1. 接收注销请求并验证参数
2. 清理用户登录相关缓存
3. 查找用户信息
4. 异步记录注销日志
5. 返回注销结果
6. 记录性能统计数据

## 扩展功能

### 未来可扩展的功能
1. **批量注销**: 支持管理员批量注销用户
2. **强制注销**: 支持强制注销指定用户的所有会话
3. **注销通知**: 注销时发送邮件或短信通知
4. **会话管理**: 集成分布式会话管理（如Redis）