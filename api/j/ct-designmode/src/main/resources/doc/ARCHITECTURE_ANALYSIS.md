# 工程架构分析报告

## 1. 整体架构概述

### 1.1 项目结构
```
ct-designmode/
├── src/main/java/com/joysky/ms/ct/
│   ├── CtDesignmodeApplication.java     # Spring Boot 启动类
│   ├── aitest/                          # AI测试模块
│   ├── designmode/                      # 设计模式示例
│   │   └── strategy1/                   # 策略模式实现
│   ├── login/                           # 登录注册核心模块
│   │   ├── common/                      # 公共组件
│   │   ├── config/                      # 配置类
│   │   ├── controller/                  # 控制器层
│   │   ├── dto/                         # 数据传输对象
│   │   ├── entity/                      # 实体类
│   │   ├── repository/                  # 数据访问层
│   │   ├── service/                     # 业务逻辑层
│   │   └── util/                        # 工具类
│   ├── od/                              # 其他模块
│   └── sf/                              # 排序算法模块
└── src/main/resources/
    ├── application.properties           # 应用配置
    ├── db/schema.sql                   # 数据库表结构
    ├── keys/                           # RSA密钥文件
    └── static/                         # 静态资源
```

### 1.2 技术栈
- **框架**: Spring Boot 3.5.0
- **数据库**: MySQL 8.x + HikariCP连接池
- **ORM**: Spring Data JPA + Hibernate
- **缓存**: Guava Cache (本地缓存)
- **安全**: RSA加密 + MD5密码哈希
- **异步**: Spring @Async
- **监控**: 自定义性能监控服务
- **前端**: 原生HTML/JavaScript

### 1.3 架构模式
采用经典的**分层架构模式**：
- **表现层 (Presentation Layer)**: Controller
- **业务逻辑层 (Business Layer)**: Service
- **数据访问层 (Data Access Layer)**: Repository
- **数据层 (Data Layer)**: MySQL数据库

## 2. 核心模块分析

### 2.1 登录注册模块 (login)

#### 2.1.1 模块职责
- 用户注册、登录认证
- 用户信息加密存储
- 缓存管理
- 性能监控
- 异步任务处理

#### 2.1.2 关键组件

**控制器层 (Controller)**
- `UserController`: 用户注册/登录接口
- `UserAdminController`: 用户管理接口
- `PerformanceController`: 性能监控接口

**业务逻辑层 (Service)**
- `UserService/UserServiceImpl`: 核心业务逻辑
- `UserCacheService`: 缓存管理
- `AsyncUserService`: 异步任务处理
- `PerformanceMonitorService`: 性能监控
- `ImageCodeService`: 图片验证码

**数据访问层 (Repository)**
- `UserRepository`: 用户数据访问，继承JpaRepository

**实体层 (Entity)**
- `User`: 用户实体，包含加密字段和索引配置

#### 2.1.3 数据流向
```
前端请求 → Controller → Service → Repository → Database
                ↓
            Cache ← Service ← Repository
                ↓
            异步任务 (日志/统计)
```

### 2.2 设计模式模块 (designmode)

#### 2.2.1 策略模式实现 (strategy1)
- `PricingStrategy`: 价格策略接口
- 多种具体策略实现
- 策略上下文管理

### 2.3 工具模块

#### 2.3.1 加密工具 (util)
- `EncryptionUtil`: RSA加密/解密
- `MaskUtil`: 数据脱敏
- `RSAKeyGenerator`: 密钥生成

#### 2.3.2 算法模块 (sf)
- 排序算法实现和测试

## 3. 数据库设计分析

### 3.1 表结构

**t_user表**
```sql
CREATE TABLE t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(11) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone_encrypted VARCHAR(500) NOT NULL,
    email_encrypted VARCHAR(500) NOT NULL,
    phone_masked VARCHAR(20) NOT NULL,
    email_masked VARCHAR(100) NOT NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    UNIQUE KEY uk_phone (phone),
    UNIQUE KEY uk_email (email),
    UNIQUE KEY uk_username (username)
);
```

### 3.2 索引策略
- **主键索引**: id (自动)
- **唯一索引**: username, phone, email
- **普通索引**: create_time (用于时间范围查询)

### 3.3 数据安全设计
- **多重存储**: 明文(校验) + 加密(安全) + 脱敏(展示)
- **RSA加密**: 敏感信息加密存储
- **MD5哈希**: 密码不可逆加密

## 4. 性能优化策略

### 4.1 数据库层优化
- **连接池**: HikariCP，最大20连接，最小5空闲
- **批处理**: Hibernate批量操作，batch_size=20
- **索引优化**: 关键字段建立索引
- **联合查询**: 减少多次数据库访问

### 4.2 缓存策略
- **本地缓存**: Guava Cache
  - 用户信息缓存: 5分钟过期，最大10000条
  - 存在性缓存: 1分钟过期，最大50000条
- **缓存监控**: 命中率统计

### 4.3 异步处理
- **线程池配置**: 核心4线程，最大8线程，队列200
- **异步任务**: 登录日志、统计更新、通知发送

### 4.4 HTTP优化
- **Gzip压缩**: 启用响应压缩，最小1KB
- **连接池**: Tomcat最大2000连接，200线程

## 5. 监控和可观测性

### 5.1 性能监控指标
- 登录/注册请求计数
- 缓存命中率统计
- 响应时间监控
- 系统资源使用情况

### 5.2 监控接口
- `GET /api/performance/stats`: 完整性能报告
- `GET /api/performance/login-count`: 登录请求统计
- `GET /api/performance/cache-hit-rate`: 缓存命中率
- `POST /api/performance/reset`: 重置统计数据

## 6. 安全机制

### 6.1 数据加密
- **传输加密**: HTTPS (生产环境)
- **存储加密**: RSA非对称加密
- **密码安全**: MD5哈希 + 盐值 (建议升级为BCrypt)

### 6.2 数据脱敏
- 手机号: 138****1234
- 邮箱: abc***@example.com

### 6.3 输入验证
- Bean Validation注解
- 敏感词过滤 (待实现)
- 验证码校验 (待实现)

## 7. 架构优势

### 7.1 优点
1. **清晰的分层结构**: 职责分离，易于维护
2. **性能优化**: 多层缓存 + 异步处理
3. **安全设计**: 多重加密 + 数据脱敏
4. **可扩展性**: 模块化设计，易于扩展
5. **监控完善**: 性能指标全面监控

### 7.2 技术亮点
1. **联合查询优化**: 单SQL支持用户名/手机/邮箱登录
2. **多级缓存**: 用户信息 + 存在性检查缓存
3. **异步解耦**: 非关键业务异步处理
4. **性能监控**: 实时性能指标收集
5. **数据安全**: 明文+加密+脱敏三重保护

## 8. 架构演进建议

### 8.1 短期优化
1. 完善验证码和敏感词功能
2. 添加请求频率限制
3. 增强异常处理和日志记录
4. 完善单元测试覆盖

### 8.2 中期演进
1. 引入Redis分布式缓存
2. 添加消息队列 (RabbitMQ/Kafka)
3. 实现微服务拆分
4. 集成APM监控 (Micrometer + Prometheus)

### 8.3 长期规划
1. 容器化部署 (Docker + Kubernetes)
2. 服务网格 (Istio)
3. 事件驱动架构
4. CQRS + Event Sourcing

---

**文档版本**: v1.0  
**创建时间**: 2024年  
**维护者**: 开发团队