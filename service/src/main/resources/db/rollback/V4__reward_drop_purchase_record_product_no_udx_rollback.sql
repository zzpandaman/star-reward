-- 回滚 V4：恢复 reward_purchase_record.product_no 唯一约束
-- 执行前需确保表中无重复 product_no，否则会失败
ALTER TABLE reward_purchase_record ADD UNIQUE KEY udx_product_no (product_no);
