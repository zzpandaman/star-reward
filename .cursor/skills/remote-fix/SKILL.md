---
name: remote-fix
description: Fixes server deployment issues. Receives error from remoteVerify, produces fix plan, maintains star-deploy-server (one-way upload via ssh-server-deploy), updates related rules, then triggers remoteVerify.
---

# Remote Fix

Resolves server deployment failures reported by remoteVerify. Use when remoteVerify returns `{ success: false, error: "..." }`.

## Input

`error`: The failure message from remoteVerify.

## Workflow

1. **Analyze** the error: SSH failure, pull error, container crash, health check timeout, nginx config error, etc.

2. **Produce fix** by modifying:
   - `/Users/cdmac14013/docker/star-deploy-server/` scripts, compose, nginx configs
   - `.cursor/rules/deploy/remote-deploy-standard.mdc` if design changed
   - `.cursor/rules/deploy/local-env-architecture.mdc` if path/structure changed

3. **Apply changes** (edit files locally).

4. **One-way upload** to server (use ssh-server-deploy):
   - Nginx: `cd ~/docker/star-deploy-server && ./deploy-nginx-config.sh`
   - Backend: `scp backend/docker-compose.yml backend/.env.prod myServer:/opt/star-deploy/backend/`
   - Root compose: `scp docker-compose.yml .env.prod myServer:/opt/star-deploy/`
   - Never download from server to overwrite local

5. **Trigger re-verification**: Ask to run remoteVerify again, or use remoteDeploy agent.若修复涉及代码变更需重新打镜像，先调用 trigger-deploy-workflow 等待构建完成，再调用 remoteVerify。

## Constraints

- One-way sync only: local → server. Do not pull server config to overwrite local.
- Use ssh-server-deploy for SSH/scp operations
- Only modify star-deploy-server; never touch star-deploy-local
