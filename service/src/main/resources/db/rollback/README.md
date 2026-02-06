# 回滚脚本（手动执行，不纳入 Flyway）

高风险迁移需在此目录提供对应回滚脚本，命名格式：`V{n}__{prefix}_{description}_rollback.sql`。

执行前请确认前置条件与执行顺序。
