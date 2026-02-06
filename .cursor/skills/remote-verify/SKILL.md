---
name: remote-verify
description: Verifies server deployment of a single service (nginx, star-sso, star-reward, star-reward-app). Uses ssh-server-deploy. When image rebuild needed, calls trigger-deploy-workflow first and waits for completion before deploy. On failure, passes error to remoteFix.
---

# Remote Verify

Verifies server deployment for a single service. Use ssh-server-deploy for SSH execution. Use when deploying or validating after remoteFix modifications.

## Service Parameter

`service`: one of `nginx` | `star-sso` | `star-reward` | `star-reward-app`

## 与 trigger-deploy-workflow 衔接

部署 **star-sso** 或 **star-reward** 且需重新打镜像时：

1. **先调用 trigger-deploy-workflow**（指定 star-sso 或 star-reward）
2. **必须等待** workflow 完成（gh run watch 或 trigger-and-wait.sh，不得让用户手动介入）
3. **再执行** 下方 Deploy 步骤（pull 拉取新镜像）。star-reward 的 workflow 已含 Deploy，成功即已部署，可仅做健康检查。

## Workflow

1. **Clean environment** (via SSH, Host from ~/.ssh/config e.g. myServer):
   - Backend: `ssh myServer "cd /opt/star-deploy/backend && docker compose --env-file .env.prod down"`
   - Nginx config: deploy fresh via `deploy-nginx-config.sh`
   - Frontend: `ssh myServer "rm -rf /opt/star-deploy/nginx/html/star/*"` before scp

2. **Deploy**（star-sso/star-reward 若需新镜像，先完成 trigger-deploy-workflow）:
   - `nginx`: `cd ~/docker/star-deploy-server && ./deploy-nginx-config.sh`
   - `star-sso` / `star-reward`: `ssh myServer "cd /opt/star-deploy/backend && docker compose --env-file .env.prod pull && docker compose --env-file .env.prod up -d --no-build --force-recreate"`
   - `star-reward-app`: build locally, scp to server, `ssh myServer "docker exec star-nginx nginx -s reload"`
   - Or use `deploy-server.sh` for full backend+frontend

3. **Health check** (use SERVER_URL from deploy-server.sh or user-provided):
   - `curl -s -o /dev/null -w "%{http_code}" ${SERVER_URL}/api/sso/health`
   - `curl -s -o /dev/null -w "%{http_code}" ${SERVER_URL}/api/reward/health`
   - `curl -s -o /dev/null -w "%{http_code}" ${SERVER_URL}/star/`
   - `curl -s -o /dev/null -w "%{http_code}" ${SERVER_URL}/health`

4. **Return**:
   - Success: `{ success: true }`
   - Failure: `{ success: false, error: "<captured output>" }` → pass to remoteFix

## On Failure

Invoke remoteFix with the `error` content. Do not retry without fix.
