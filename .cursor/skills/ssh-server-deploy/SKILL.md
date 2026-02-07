---
name: ssh-server-deploy
description: Manages local SSH keys for direct server connection, and performs star-reward deployment tasks. Use when setting up SSH access, deploying to server, restarting services, viewing logs, or rolling back.
---

# SSH 服务器部署

## 执行原则（必读）

- **直接执行**：用户请求部署时，直接执行 SSH 命令，不输出命令让用户自行执行
- **不询问密码**：不询问 REGISTRY_PASS，服务器已配置 docker 凭据，直接执行 pull + restart 即可

## 本机 SSH 密钥管理（直连前置）

使本机能够免密直连服务器：

### 1. 生成密钥（若无）

```bash
# 检查是否已有密钥
ls ~/.ssh/id_ed25519.pub 2>/dev/null || ls ~/.ssh/id_rsa.pub 2>/dev/null

# 若无，生成 ed25519（推荐）
ssh-keygen -t ed25519 -C "deploy" -f ~/.ssh/id_ed25519 -N ""
```

### 2. 将公钥加入服务器

```bash
# user 为服务器登录用户，host 为服务器 IP 或域名
ssh-copy-id -i ~/.ssh/id_ed25519.pub user@host
```

首次需输入服务器密码，成功后即可免密登录。

### 3. 配置 ~/.ssh/config（可选，简化命令）

```bash
# 追加到 ~/.ssh/config
cat >> ~/.ssh/config << 'EOF'

Host myServer
    HostName 服务器IP
    User 服务器登录用户
    IdentityFile ~/.ssh/id_ed25519
EOF
```

配置后可用 `ssh myServer` 替代 `ssh user@host`。

### 4. 验证直连

```bash
ssh myServer "echo OK"
# 或 ssh user@host "echo OK"
```

输出 `OK` 即直连成功。

## 前置条件

- 本地已配置 SSH 密钥，服务器公钥已加入 `~/.ssh/authorized_keys`
- **本地执行**：优先使用 `~/.ssh/config` 中的 Host（如 myServer），无需 SERVER_HOST/SERVER_USER
- **GitHub Actions**：使用 Secrets 中的 SERVER_HOST、SERVER_USER、SERVER_SSH_KEY

## SSH 连接方式

### 本地执行（推荐）

优先使用 ~/.ssh/config 中配置的 Host：

```bash
ssh myServer "command"
scp file myServer:/path/
```

### 环境变量方式（GitHub Actions / 未配置 config 时）

```bash
ssh $SERVER_USER@$SERVER_HOST
```

## 部署工作流

### 1. 本地构建后部署到服务器

```bash
# 在 star-reward 项目根目录
cd service && mvn clean package spring-boot:repackage -DskipTests
cp service/target/service-*.jar service/target/app.jar
docker build -t $REGISTRY_URL/star-reward:latest -f deploy/Dockerfile .
docker push $REGISTRY_URL/star-reward:latest

# SSH 部署（myServer 为 ~/.ssh/config 中的 Host）
ssh myServer << 'EOF'
docker login $REGISTRY_URL -u $REGISTRY_USER -p $REGISTRY_PASS
docker pull $REGISTRY_URL/star-reward:latest
cd /opt/star-deploy/backend
docker compose --env-file .env.prod up -d --no-build star-reward
EOF
```

### 2. 仅拉取最新镜像并重启（推荐，无需密码）

服务器已配置 docker 凭据，直接执行：

```bash
ssh myServer "cd /opt/star-deploy/backend && docker compose --env-file .env.prod pull && docker compose --env-file .env.prod up -d --no-build --force-recreate"
```

需查看 Registry 镜像列表或部署前端时，使用 `cd ~/docker/star-deploy-server && REGISTRY_PASS=<密码> ./deploy-server.sh`。

### 3. Nginx 配置部署

```bash
cd ~/docker/star-deploy-server && ./deploy-nginx-config.sh
```

更新 nginx 配置（含 413 修复）、reload。默认 Host：myServer，可 `SSH_HOST=xxx` 覆盖。

### 4. 版本回滚

```bash
# 或访问 http://服务器IP/registry-ui/ 查看镜像列表
curl -u admin:password http://localhost/v2/star-reward/tags/list
ssh myServer "cd /opt/star-deploy/backend && docker pull localhost:5000/star-reward:<commit-sha> && docker compose --env-file .env.prod up -d --no-build --force-recreate star-reward"
```

### 5. Registry UI 部署

```bash
ssh myServer "cd /opt/star-deploy && docker compose --env-file .env.prod pull registry-ui && docker compose --env-file .env.prod up -d registry-ui"
```

### 6. 常用运维命令

```bash
ssh myServer "cd /opt/star-deploy/backend && docker compose ps"
ssh myServer "docker logs -f star-reward"
ssh myServer "tail -f /opt/star-deploy/logs/star-reward/star-reward-service.log"
```

## 健康检查

```bash
curl -X POST -H "Content-Type: application/json" -d '{}' http://服务器IP/api/reward/health
curl http://服务器IP/health
```

## 路径与变量

| 项目 | 值 |
|------|-----|
| 本地 SSH Host | `~/.ssh/config` 中的 Host（如 myServer） |
| 服务器部署目录 | `/opt/star-deploy/backend` |
| 根目录 compose | `/opt/star-deploy`（nginx、portainer、registry-ui） |
| 本地部署脚本 | `~/docker/star-deploy-server/`（deploy-server.sh、deploy-nginx-config.sh） |
| Registry | `$REGISTRY_URL`（nginx 代理时不含 `:5000`） |
| Registry UI | `http://服务器IP/registry-ui/`（admin / REGISTRY_PASS） |
| 镜像名 | `star-reward` |

## 代理后端诊断

排查 nginx 反向代理后端异常时，按以下顺序：

1. **容器状态**：`docker ps` 确认 star-sso/star-reward 运行
2. **容器日志**：`docker logs star-sso` / `docker logs star-reward` 查看崩溃或错误
3. **MySQL 权限**：MySQL 在宿主机时需 `root@127.0.0.1` 授权，否则 Access denied
4. **nginx 访问后端**：`docker exec star-nginx curl http://host.docker.internal:8081/api/sso/health`、`docker exec star-nginx curl -X POST -H "Content-Type: application/json" -d '{}' http://host.docker.internal:8081/api/reward/health` 验证
5. **健康端点**：`curl http://服务器IP/api/sso/health` 验证

## 注意事项

- 执行 SSH 命令需 `network` 权限
- 密码、密钥等敏感信息勿写入代码，使用环境变量或 secrets
- 项目内可参考 `.cursor/rules/deploy/docker-deploy.mdc` 获取完整部署规范
