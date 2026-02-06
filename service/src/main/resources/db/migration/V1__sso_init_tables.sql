-- sso 表，从 star-sso docs/schema.sql 迁入

-- 用户表
CREATE TABLE `sso_user` (
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
    `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码（加密后）',
    `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '邮箱',
    `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '手机号',
    `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '昵称',
    `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '头像URL',
    `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态。ACTIVE-激活 LOCKED-锁定 DISABLED-禁用',
    `user_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户编号',
    `is_deleted` tinyint(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除。0-否 1-是',
    `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人名字',
    `create_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人邮箱',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人名字',
    `update_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人邮箱',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- 角色表
CREATE TABLE `sso_role` (
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
    `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '角色描述',
    `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态。ACTIVE-激活 DISABLED-禁用',
    `role_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编号',
    `is_deleted` tinyint(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除。0-否 1-是',
    `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人名字',
    `create_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人邮箱',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人名字',
    `update_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人邮箱',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
    `attributes` json NULL COMMENT '扩展字段',
    PRIMARY KEY (`id`),
    UNIQUE KEY `udx_role_no` (`role_no`) USING BTREE,
    UNIQUE KEY `udx_code` (`code`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE,
    INDEX `idx_is_deleted` (`is_deleted`) USING BTREE,
    INDEX `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';

-- 权限表
CREATE TABLE `sso_permission` (
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限名称',
    `code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限编码（如：user:create）',
    `resource` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '资源路径（如：/api/user）',
    `method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'HTTP方法（GET/POST/PUT/DELETE）',
    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '权限描述',
    `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'MENU' COMMENT '权限类型。MENU-菜单 BUTTON-按钮 API-接口',
    `parent_id` bigint(20) UNSIGNED NULL COMMENT '父权限ID',
    `sort_order` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '排序',
    `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态。ACTIVE-激活 DISABLED-禁用',
    `permission_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限编号',
    `is_deleted` tinyint(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除。0-否 1-是',
    `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人名字',
    `create_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人邮箱',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人名字',
    `update_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人邮箱',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE `sso_user_role` (
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户ID',
    `role_id` bigint(20) UNSIGNED NOT NULL COMMENT '角色ID',
    `is_deleted` tinyint(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除。0-否 1-是',
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
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE `sso_role_permission` (
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `role_id` bigint(20) UNSIGNED NOT NULL COMMENT '角色ID',
    `permission_id` bigint(20) UNSIGNED NOT NULL COMMENT '权限ID',
    `is_deleted` tinyint(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除。0-否 1-是',
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
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限关联表';

-- 登录令牌表（预留，暂不使用）
CREATE TABLE `sso_token` (
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户ID',
    `token` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'JWT令牌',
    `token_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'BEARER' COMMENT '令牌类型',
    `expire_time` datetime NOT NULL COMMENT '过期时间',
    `is_blacklisted` tinyint(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否黑名单。0-否 1-是',
    `token_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '令牌编号',
    `is_deleted` tinyint(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除。0-否 1-是',
    `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人名字',
    `create_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创建人邮箱',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人名字',
    `update_by_email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '更新人邮箱',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='登录令牌表（用于黑名单，暂不使用）';
