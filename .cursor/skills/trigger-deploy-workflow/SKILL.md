---
name: trigger-deploy-workflow
description: Triggers GitHub Actions deploy workflow for star-sso, star-reward, study-reward-app. Waits until completion. No manual intervention. Used by remoteVerify when image rebuild is needed.
---

# 触发部署工作流

触发 GitHub Actions 部署工作流，**自动等待直至完成，无需人工介入**。需重新打镜像时由 remoteVerify 调用。

## 项目映射（owner/repo）

| 项目 | 仓库 | 工作流 |
|------|------|--------|
| star-sso | {owner}/star-sso | deploy.yml |
| star-reward | {owner}/star-reward | deploy.yml |
| study-reward-app | {owner}/study-reward-app | deploy.yml |

owner 从当前项目 git remote 解析（如 zzpandaman）。

## 执行流程（Agent 必遵）

1. **解析项目**：从用户输入识别 star-sso、star-reward、study-reward-app，未指定则询问
2. **触发并等待**：使用下方任一方式，**必须等待完成**，不得让用户手动介入
3. **返回**：成功则提示调用 remoteVerify 执行部署（若 workflow 未含 Deploy 步骤）

## 方式 A：gh CLI（优先，若已安装）

```bash
cd <项目路径> && gh workflow run deploy.yml && sleep 5 && gh run watch
```

## 方式 B：trigger-and-wait 脚本（gh 未安装时）

脚本位于 star-reward/.cursor/scripts/trigger-and-wait.sh：

```bash
cd /Users/cdmac14013/star-reward
GITHUB_TOKEN=${GITHUB_TOKEN} .cursor/scripts/trigger-and-wait.sh [owner/repo] deploy.yml master
```

- star-reward：可省略 owner/repo，脚本从 git remote 解析
- star-sso / study-reward-app：传 `owner/star-sso` 或 `owner/study-reward-app`
- 需 `GITHUB_TOKEN`（PAT，scope 含 workflow）
- 依赖 `jq`

## 前置

- `gh auth login` 或 `GITHUB_TOKEN` 已配置
- 项目路径存在且为 git 仓库

## 与 remoteVerify 衔接

- **需重新打镜像时**：先调用本 skill 并等待完成，再调用 remoteVerify 执行 pull + deploy
- star-reward 的 deploy.yml 已含 Deploy 步骤，workflow 成功即已部署，可仅做健康检查

## 返回格式

成功：`部署工作流执行成功：<项目名>`
失败：`部署工作流失败：<项目名> (conclusion=xxx)`，传递 error 给 remoteFix
