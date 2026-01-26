# Star Reward 项目结构说明

## 项目概述

基于 Spring Boot + DDD 架构的奖励系统项目，采用多模块结构。

## 模块说明

### 1. api-client 模块
**位置**: `api-client/`

**作用**: Feign API 二方包，独立打包供其他服务依赖

**包含**:
- Feign 接口定义 (`RewardFeignApi.java`)
- 对外 DTO (`dto/request/`, `dto/response/`)

**打包**: `star-reward-api-client-1.0.0-SNAPSHOT.jar`

### 2. service 模块
**位置**: `service/`

**作用**: 主服务模块，包含完整的 DDD 分层架构

## DDD 分层结构

### Domain (领域层)
**位置**: `service/src/main/java/com/star/reward/domain/`

- `model/entity/` - 领域实体
- `model/valueobject/` - 值对象
- `model/aggregate/` - 聚合根
- `service/` - 领域服务
- `repository/` - 仓储接口（只定义接口）
- `event/` - 领域事件
- `exception/` - 领域异常

### Application (应用层)
**位置**: `service/src/main/java/com/star/reward/application/`

- `service/` - 应用服务（用例编排）
- `dto/` - 数据传输对象
- `command/` - 命令对象（CQRS）
- `query/` - 查询对象（CQRS）
- `mapper/` - DTO 转换器
- `scheduler/` - 定时任务

### Infrastructure (基础设施层)
**位置**: `service/src/main/java/com/star/reward/infrastructure/`

- `persistence/`
  - `dao/` - MyBatis DAO（Generator 生成）
    - `mapper/` - Mapper 接口
    - `entity/` - DO 实体
    - `example/` - Example 类
  - `repository/` - 仓储实现
  - `converter/` - DO 与领域模型转换器
- `config/` - 配置类
- `external/feign/` - 调用其他服务的 Feign 客户端
  - `client/` - Feign 客户端接口
  - `dto/` - 请求/响应 DTO
  - `fallback/` - 降级处理
  - `config/` - Feign 配置
- `event/` - 事件发布实现

### Interfaces (接口层)
**位置**: `service/src/main/java/com/star/reward/interfaces/`

- `rest/` - REST API
  - `controller/` - 控制器
  - `dto/` - API DTO
  - `importexport/` - 导入导出（接口定制）
    - `dto/` - 导入导出 DTO
    - `handler/` - 导入导出处理器
    - `validator/` - 导入导出校验器
  - `exception/` - REST 异常处理
- `feign/impl/` - Feign 接口实现（实现 api-client 的接口）
- `rpc/` - RPC 接口（可选）
- `messaging/` - 消息队列（可选）

### Shared (共享层)
**位置**: `service/src/main/java/com/star/reward/shared/`

- `constant/` - 常量
- `util/` - 工具类
- `exception/` - 全局异常处理
  - `GlobalExceptionHandler.java` - 全局异常处理器
  - `BusinessException.java` - 业务异常
  - `SystemException.java` - 系统异常
- `filter/` - 全局过滤器
- `interceptor/` - 全局拦截器
- `aspect/` - 全局切面（AOP）
- `annotation/` - 自定义注解
- `result/` - 统一响应

## 配置文件

### 主配置
- `service/src/main/resources/application.yml` - 主配置文件
- `service/src/main/resources/application-dev.yml` - 开发环境
- `service/src/main/resources/application-test.yml` - 测试环境
- `service/src/main/resources/application-prod.yml` - 生产环境

### MyBatis Generator
- `service/src/main/resources/generator/generatorConfig.xml` - Generator 配置

### MyBatis Mapper
- `service/src/main/resources/mapper/` - Mapper XML 文件
- `service/src/main/resources/mapper/custom/` - 自定义 Mapper

### 数据库迁移
- `service/src/main/resources/db/migration/` - 数据库迁移脚本

## 依赖关系

```
api-client (独立模块)
    ↓ 实现
service
    ├─ interfaces → application → domain
    ├─ infrastructure → domain
    └─ shared (被所有层使用)
```

## 使用说明

### 1. 启动项目
```bash
cd service
mvn spring-boot:run
```

### 2. 使用 MyBatis Generator
修改 `generatorConfig.xml` 中的数据库连接和表配置，然后运行：
```bash
mvn mybatis-generator:generate
```

### 3. 打包
```bash
# 打包所有模块
mvn clean package

# 只打包 api-client
cd api-client
mvn clean package
```

## 注意事项

1. **MyBatis Generator 生成的文件**：
   - `dao/mapper/` 下的 Mapper 接口
   - `dao/entity/` 下的 DO 实体
   - `dao/example/` 下的 Example 类
   - `resources/mapper/` 下的 XML 文件
   
   这些文件由 Generator 生成，不要手动修改。

2. **自定义 Mapper**：
   放在 `resources/mapper/custom/` 目录下。

3. **领域模型与 DO 分离**：
   - 领域模型在 `domain/model/`
   - DO 在 `infrastructure/persistence/dao/entity/`
   - 使用 Converter 进行转换

4. **Feign 接口**：
   - 提供给外部：`api-client` 模块
   - 调用外部：`infrastructure/external/feign/client/`
