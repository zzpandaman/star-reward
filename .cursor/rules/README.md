# Rules 分类

## architecture/ - 架构

| 规则 | 说明 |
|------|------|
| project-structure | 项目结构、DDD 分层 |
| microservice-integration | 微服务体系（star-common、star-sso、Feign） |
| pagination-query | 分页查询（QueryRequest/Command 继承 PageRequest、默认值、去冗余） |

## deploy/ - 部署

| 规则 | 说明 |
|------|------|
| docker-deploy | Docker 部署配置、命令速查 |
| deploy-verify-before-change | 部署修改前核查 |
| local-env-architecture | 本地/服务器环境隔离 |
| local-deploy-standard | star-deploy-local 设计规范 |
| remote-deploy-standard | star-deploy-server 设计规范 |

## domain/ - 领域与持久化

| 规则 | 说明 |
|------|------|
| attribute-extension | AttributeHolder 扩展字段 |
| domain-constants-rich-model | 域常量收束、充血工厂方法 |
| flyway-migration | Flyway 迁移规范 |
