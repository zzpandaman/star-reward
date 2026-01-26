# SSO 单点登录微服务设计方案

## 1. 整体架构

### 1.1 服务架构

```
┌─────────────────┐         ┌─────────────────┐
│  star-reward    │────────▶│   star-sso       │
│   (业务服务)     │  Feign  │   (认证服务)     │
└─────────────────┘         └─────────────────┘
       │                            │
       │                            │
       └──────────┬─────────────────┘
                  │
            ┌─────▼─────┐
            │   MySQL   │
            └───────────┘
```

### 1.2 模块划分

**star-sso 项目结构**（参考 star-reward）：

```
star-sso/
├── api-client/          # Feign API 二方包（供其他服务依赖）
│   └── src/main/java/com/star/sso/api/
│       ├── SsoFeignApi.java
│       └── dto/
│           ├── request/
│           │   ├── LoginRequest.java
│           │   └── ValidateTokenRequest.java
│           └── response/
│               ├── LoginResponse.java
│               └── UserInfoResponse.java
└── service/             # 主服务模块（DDD架构）
    └── src/main/java/com/star/sso/
        ├── domain/      # 领域层
        ├── application/ # 应用层
        ├── infrastructure/ # 基础设施层
        └── interfaces/  # 接口层
```

## 2. 数据库表设计

### 2.1 用户表 (sso_user)

```sql
CREATE TABLE `sso_user` (
    -- 主键
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    
    -- 业务字段
    `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
    `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码（加密后）',
    `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '邮箱',
    `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '手机号',
    `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '昵称',
    `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '头像URL',
    `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态。ACTIVE-激活 LOCKED-锁定 DISABLED-禁用',
    
    -- 业务编号
    `user_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户编号',
    
    -- 状态字段
    `is_deleted` tinyint(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除。0-否 1-是',
    
    -- 审计字段
    `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人名字',
    `create_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人邮箱',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人名字',
    `update_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人邮箱',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 扩展字段
    `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
    `attributes` json NULL COMMENT '扩展字段',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `udx_user_no` (`user_no`) USING BTREE,
    UNIQUE KEY `udx_username` (`username`) USING BTREE,
    INDEX `idx_email` (`email`) USING BTREE,
    INDEX `idx_phone` (`phone`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE,
    INDEX `idx_is_deleted` (`is_deleted`) USING BTREE,
    INDEX `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='用户表';
```

### 2.2 角色表 (sso_role)

```sql
CREATE TABLE `sso_role` (
    -- 主键
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    
    -- 业务字段
    `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
    `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '角色描述',
    `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态。ACTIVE-激活 DISABLED-禁用',
    
    -- 业务编号
    `role_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编号',
    
    -- 状态字段
    `is_deleted` tinyint(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除。0-否 1-是',
    
    -- 审计字段
    `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人名字',
    `create_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人邮箱',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人名字',
    `update_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人邮箱',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 扩展字段
    `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
    `attributes` json NULL COMMENT '扩展字段',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `udx_role_no` (`role_no`) USING BTREE,
    UNIQUE KEY `udx_code` (`code`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE,
    INDEX `idx_is_deleted` (`is_deleted`) USING BTREE,
    INDEX `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='角色表';
```

### 2.3 权限表 (sso_permission)

```sql
CREATE TABLE `sso_permission` (
    -- 主键
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    
    -- 业务字段
    `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限名称',
    `code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限编码（如：user:create）',
    `resource` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '资源路径（如：/api/user）',
    `method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'HTTP方法（GET/POST/PUT/DELETE）',
    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '权限描述',
    `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'MENU' COMMENT '权限类型。MENU-菜单 BUTTON-按钮 API-接口',
    `parent_id` bigint(20) UNSIGNED NULL COMMENT '父权限ID',
    `sort_order` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '排序',
    `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态。ACTIVE-激活 DISABLED-禁用',
    
    -- 业务编号
    `permission_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限编号',
    
    -- 状态字段
    `is_deleted` tinyint(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除。0-否 1-是',
    
    -- 审计字段
    `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人名字',
    `create_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人邮箱',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人名字',
    `update_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人邮箱',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 扩展字段
    `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
    `attributes` json NULL COMMENT '扩展字段',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `udx_permission_no` (`permission_no`) USING BTREE,
    UNIQUE KEY `udx_code` (`code`) USING BTREE,
    INDEX `idx_parent_id` (`parent_id`) USING BTREE,
    INDEX `idx_type` (`type`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE,
    INDEX `idx_is_deleted` (`is_deleted`) USING BTREE,
    INDEX `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='权限表';
```

### 2.4 用户角色关联表 (sso_user_role)

```sql
CREATE TABLE `sso_user_role` (
    -- 主键
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    
    -- 业务字段
    `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户ID',
    `role_id` bigint(20) UNSIGNED NOT NULL COMMENT '角色ID',
    
    -- 状态字段
    `is_deleted` tinyint(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除。0-否 1-是',
    
    -- 审计字段
    `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人名字',
    `create_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人邮箱',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人名字',
    `update_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人邮箱',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `udx_user_role` (`user_id`, `role_id`) USING BTREE,
    INDEX `idx_user_id` (`user_id`) USING BTREE,
    INDEX `idx_role_id` (`role_id`) USING BTREE,
    INDEX `idx_is_deleted` (`is_deleted`) USING BTREE
) ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='用户角色关联表';
```

### 2.5 角色权限关联表 (sso_role_permission)

```sql
CREATE TABLE `sso_role_permission` (
    -- 主键
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    
    -- 业务字段
    `role_id` bigint(20) UNSIGNED NOT NULL COMMENT '角色ID',
    `permission_id` bigint(20) UNSIGNED NOT NULL COMMENT '权限ID',
    
    -- 状态字段
    `is_deleted` tinyint(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除。0-否 1-是',
    
    -- 审计字段
    `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人名字',
    `create_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人邮箱',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人名字',
    `update_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人邮箱',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `udx_role_permission` (`role_id`, `permission_id`) USING BTREE,
    INDEX `idx_role_id` (`role_id`) USING BTREE,
    INDEX `idx_permission_id` (`permission_id`) USING BTREE,
    INDEX `idx_is_deleted` (`is_deleted`) USING BTREE
) ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='角色权限关联表';
```

### 2.6 登录令牌表 (sso_token) - 可选，用于令牌黑名单（未来扩展）

```sql
CREATE TABLE `sso_token` (
    -- 主键
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    
    -- 业务字段
    `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户ID',
    `token` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'JWT令牌',
    `token_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'BEARER' COMMENT '令牌类型',
    `expire_time` datetime NOT NULL COMMENT '过期时间',
    `is_blacklisted` tinyint(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否黑名单。0-否 1-是',
    
    -- 业务编号
    `token_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '令牌编号',
    
    -- 状态字段
    `is_deleted` tinyint(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除。0-否 1-是',
    
    -- 审计字段
    `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人名字',
    `create_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人邮箱',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人名字',
    `update_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人邮箱',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 扩展字段
    `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
    `attributes` json NULL COMMENT '扩展字段',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `udx_token_no` (`token_no`) USING BTREE,
    INDEX `idx_user_id` (`user_id`) USING BTREE,
    INDEX `idx_token` (`token`(255)) USING BTREE,
    INDEX `idx_expire_time` (`expire_time`) USING BTREE,
    INDEX `idx_is_blacklisted` (`is_blacklisted`) USING BTREE,
    INDEX `idx_is_deleted` (`is_deleted`) USING BTREE,
    INDEX `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='登录令牌表（用于黑名单，暂不使用）';
```

## 3. JWT 设计

### 3.1 JWT 结构

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "user_no",           // 用户编号
    "userId": 123,              // 用户ID
    "username": "admin",        // 用户名
    "roles": ["ADMIN", "USER"], // 角色编码列表
    "permissions": ["user:create", "user:update"], // 权限编码列表
    "iat": 1234567890,          // 签发时间
    "exp": 1234571490           // 过期时间（默认2小时）
  },
  "signature": "..."
}
```

### 3.2 JWT 配置

```yaml
jwt:
  secret: your-secret-key-here  # 密钥（建议使用环境变量）
  expiration: 7200              # 过期时间（秒），默认2小时
  refresh-expiration: 604800    # 刷新令牌过期时间（秒），默认7天
  header: Authorization         # 请求头名称
  prefix: Bearer                # 令牌前缀
```

## 4. 核心功能设计

### 4.1 登录流程

```
1. 用户提交用户名密码
   ↓
2. SSO服务验证用户信息
   ↓
3. 生成JWT Token（包含用户信息、角色、权限）
   ↓
4. 返回Token给客户端
   ↓
5. 客户端存储Token（Cookie或LocalStorage）
```

### 4.2 Token验证流程

```
1. 客户端请求业务服务（携带Token）
   ↓
2. 业务服务拦截器验证Token
   ↓
3. 调用SSO服务验证Token有效性（可选，可本地验证）
   ↓
4. 解析Token获取用户信息
   ↓
5. 将用户信息放入ThreadLocal
   ↓
6. 继续执行业务逻辑
```

### 4.3 登出流程

```
1. 客户端调用登出接口
   ↓
2. SSO服务将Token加入黑名单（如果使用Redis）
   ↓
3. 返回成功（当前版本暂不实现黑名单）
```

## 5. 与 star-reward 服务集成

### 5.1 star-reward 需要添加的功能

1. **JWT拦截器**：验证Token并解析用户信息
2. **当前用户获取**：从ThreadLocal获取当前登录用户
3. **Feign客户端**：调用SSO服务验证Token（可选）

### 5.2 集成方式

**方式一：本地验证（推荐，性能更好）**

- star-reward 本地验证JWT签名
- 从JWT中解析用户信息
- 无需调用SSO服务

**方式二：远程验证**

- star-reward 通过Feign调用SSO服务验证Token
- SSO服务返回用户信息
- 适合需要实时验证的场景

## 6. API 设计

### 6.1 SSO服务API

```
POST /api/sso/login          # 登录
POST /api/sso/logout         # 登出
POST /api/sso/refresh         # 刷新Token
POST /api/sso/validate       # 验证Token（供其他服务调用）
GET  /api/sso/user/info      # 获取当前用户信息
```

### 6.2 star-reward 集成API

```
GET  /api/current-user       # 获取当前登录用户信息（从ThreadLocal）
```

## 7. 技术选型

- **JWT库**：`io.jsonwebtoken:jjwt` (0.11.5)
- **密码加密**：BCrypt（Spring Security提供）
- **Token存储**：暂不使用Redis，未来可扩展

## 8. 安全考虑

1. **密码加密**：使用BCrypt加密存储
2. **Token安全**：
   - 使用HTTPS传输
   - 设置合理的过期时间
   - 签名密钥妥善保管
3. **防重放攻击**：可添加nonce或时间戳（未来扩展）
4. **防暴力破解**：登录失败次数限制（未来扩展）

## 9. 待确认事项

1. ✅ 表结构设计是否符合需求？
2. ✅ JWT过期时间设置（默认2小时）是否合适？
3. ✅ 是否需要刷新Token机制？
4. ✅ Token验证方式：本地验证 or 远程验证？
5. ✅ 是否需要登录日志记录？
6. ✅ 是否需要多端登录（同一账号多个设备）？

请确认以上设计，确认后开始实现。
