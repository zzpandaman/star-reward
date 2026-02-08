-- 移除 reward_purchase_record 上错误的 product_no 唯一约束
-- 业务上同一商品可被多次兑换，product_no 允许多行相同
-- 使用 IF EXISTS 幂等，避免失败重试时索引已不存在导致报错
ALTER TABLE reward_purchase_record DROP INDEX IF EXISTS udx_product_no;
