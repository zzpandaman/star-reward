---
name: remote-deploy
description: Manages server deployment (nginx, star-sso, star-reward, star-reward-app). Supports single-service redeploy, start/stop/restart, and full deploy. Uses ssh-server-deploy for SSH operations. Has waiting capability—no manual intervention.
---

# Remote Deploy Agent

You manage server deployment for star-deploy-server. Services: nginx, star-sso, star-reward, star-reward-app. Server path: `/opt/star-deploy`.

## 原则：全自动等待，无需人工介入

执行部署时**必须自动等待**所有异步步骤完成，不得让用户手动触发或轮询。需重新打镜像时，调用 trigger-deploy-workflow 并**等待其完成**后再执行后续步骤。

## Root Directory

`/Users/cdmac14013/docker/star-deploy-server`

## Capabilities

### Single-Service Redeploy

| Service | Command |
|---------|---------|
| nginx | `cd ~/docker/star-deploy-server && ./deploy-nginx-config.sh` |
| star-sso / star-reward | `ssh myServer "cd /opt/star-deploy/backend && docker compose --env-file .env.prod pull && docker compose --env-file .env.prod up -d --no-build --force-recreate"` |
| star-reward-app | `cd ~/docker/star-deploy-server && REGISTRY_PASS=<pwd> ./deploy-server.sh` (includes frontend) or build + scp manually |

### Single-Service Restart

| Service | Command |
|---------|---------|
| star-sso | `ssh myServer "cd /opt/star-deploy/backend && docker compose --env-file .env.prod restart star-sso"` |
| star-reward | `ssh myServer "cd /opt/star-deploy/backend && docker compose --env-file .env.prod restart star-reward"` |
| nginx | `ssh myServer "docker exec star-nginx nginx -s reload"` |

### Full Deploy / Full Stop

- **Full deploy**: `cd ~/docker/star-deploy-server && REGISTRY_PASS=<pwd> ./deploy-server.sh`
- **Full stop**: `ssh myServer "cd /opt/star-deploy/backend && docker compose --env-file .env.prod down"` then root compose if needed

## 需重新打镜像时（全自动）

1. 调用 **trigger-deploy-workflow**（指定 star-sso 或 star-reward）
2. **等待** workflow 完成（gh run watch 或 trigger-and-wait.sh 轮询，不得跳过）
3. star-reward 的 workflow 已含 Deploy 步骤，成功即已部署；否则调用 **remoteVerify** 执行 pull + deploy

## Verification

After deploy, use remoteVerify skill. On failure, pass error to remoteFix.

## References

- remote-deploy-standard rule
- ssh-server-deploy skill
- trigger-deploy-workflow skill（含 trigger-and-wait.sh 备用）
- local-env-architecture rule
