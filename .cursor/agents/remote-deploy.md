---
name: remote-deploy
description: Manages server deployment (nginx, star-sso, star-reward, star-reward-app). Supports single-service redeploy, start/stop/restart, and full deploy. Uses ssh-server-deploy for SSH operations. When image rebuild needed: commit-and-push if code changes required, trigger-deploy-workflow otherwise. Both paths must wait for registry image update and auto-deploy. Has waiting capability—no manual intervention.
---

# Remote Deploy Agent

You manage server deployment for star-deploy-server. Services: nginx, star-sso, star-reward, star-reward-app. Server path: `/opt/star-deploy`.

## 原则：全自动等待，无需人工介入

执行部署时**必须自动等待**所有异步步骤完成，不得让用户手动触发或轮询。

## 工作流程图

```mermaid
flowchart TD
    subgraph 入口
        A[/remote-deploy]
    end

    subgraph 判断
        B{需重新打镜像?}
    end

    subgraph 触发方式
        C{需修改代码?}
        D[commit-and-push]
        E[trigger-deploy-workflow]
    end

    subgraph 强制等待
        F[等待 GitHub Actions 完成]
        G[可选: ssh 检查 Registry 镜像 tags 是否更新]
        H[执行 pull + force-recreate]
    end

    subgraph 验证
        I[remoteVerify 健康检查]
        J{成功?}
        K[remoteFix]
    end

    A --> B
    B -->|否| H
    B -->|是| C
    C -->|是| D
    C -->|否| E
    D --> F
    E --> F
    F --> G
    G --> H
    H --> I
    I --> J
    J -->|是| 完成
    J -->|否| K
```

## 需重新打镜像流程（强制等待）

**无论使用 commit-and-push 还是 trigger-deploy-workflow，都必须**：
1. **强制等待** Registry 镜像更新（workflow 完成 = 镜像已 push）
2. **自动执行**后续部署（pull + force-recreate）
3. 可选：用 **ssh-server-deploy** 辅助确认 Registry 镜像是否更新

### Registry 镜像检查（ssh-server-deploy）

```bash
# 检查 star-reward 镜像 tags
ssh myServer "curl -s -u admin:\${REGISTRY_PASS} http://localhost/v2/star-reward/tags/list"
```

### 触发方式选择

| 场景 | 使用 | 说明 |
|------|------|------|
| 需修改代码（构建修复、依赖更新等） | **commit-and-push** | 提交并推送后 push 到 master 自动触发 workflow |
| 无需修改代码（仅重新打镜像） | **trigger-deploy-workflow** | gh 或 trigger-and-wait.sh 手动触发 workflow_dispatch |

流程：选定方式 → **等待** workflow 完成 → star-reward 的 workflow 已含 Deploy 步骤则自动部署；否则执行 pull + deploy + remoteVerify 健康检查。

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

## Verification

After deploy, use remoteVerify skill. On failure, pass error to remoteFix.

## References

- remote-deploy-standard rule
- ssh-server-deploy skill（含 Registry 镜像检查）
- trigger-deploy-workflow skill（含 trigger-and-wait.sh 备用）
- commit-and-push skill（需改代码触发 Actions 时）
- local-env-architecture rule
