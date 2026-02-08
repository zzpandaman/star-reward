-- 移除 reward_purchase_record 上错误的 product_no 唯一约束
-- 业务上同一商品可被多次兑换，product_no 允许多行相同
ALTER TABLE reward_purchase_record DROP INDEX udx_product_no;
