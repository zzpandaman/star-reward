# Star Reward Project

基于 Spring Boot + DDD 架构的奖励系统项目。

## 项目结构

```
star-reward/
├── api-client/          # Feign API 二方包模块
├── service/             # 主服务模块
└── pom.xml              # 父 POM
```

## 技术栈

- Spring Boot 2.7.18
- Spring Cloud 2021.0.9
- MyBatis + MyBatis Generator
- MySQL 8.0
- Druid
- Lombok
- EasyExcel

## 模块说明

### api-client
对外提供的 Feign 接口定义，独立打包供其他服务依赖。

### service
主服务模块，包含完整的 DDD 分层架构。

## 环境配置

配置文件位于 `service/src/main/resources/`：
- `application.yml` - 主配置
- `application-dev.yml` - 开发环境
- `application-test.yml` - 测试环境
- `application-prod.yml` - 生产环境

## 启动方式

```bash
cd service
mvn spring-boot:run
```
