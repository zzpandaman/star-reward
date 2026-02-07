-- 积分记录表
CREATE TABLE `reward_point_record` (
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `record_no` varchar(32) NOT NULL COMMENT '记录编号',
    `point_type` varchar(16) NOT NULL COMMENT '类型。EARN-获取 SPEND-消耗',
    `amount` decimal(20,6) UNSIGNED NOT NULL COMMENT '积分数量',
    `belong_to` varchar(32) NOT NULL COMMENT '所属人账号',
    `belong_to_id` bigint(20) UNSIGNED NOT NULL COMMENT '所属人ID',
    `related_id` varchar(64) NULL COMMENT '关联业务ID（任务实例编号/购买记录编号）',
    `description` varchar(500) NULL COMMENT '描述',
    `create_by` varchar(32) NOT NULL COMMENT '创建人账号',
    `create_by_id` bigint(20) UNSIGNED NOT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `udx_record_no` (`record_no`) USING BTREE,
    KEY `idx_belong_to_id` (`belong_to_id`) USING BTREE,
    KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='积分记录表';
