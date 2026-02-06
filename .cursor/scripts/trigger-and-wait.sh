#!/bin/bash
# 触发 GitHub Actions workflow 并轮询等待完成，无需人工介入
# 用法: GITHUB_TOKEN=<token> ./trigger-and-wait.sh [owner/repo] [workflow_file] [branch]
# 示例: GITHUB_TOKEN=xxx ./trigger-and-wait.sh zzpandaman/star-reward deploy.yml master

set -e
REPO="${1:-}"
WORKFLOW="${2:-deploy.yml}"
BRANCH="${3:-master}"

if [ -z "$REPO" ]; then
  # 从当前目录 git remote 解析
  ORIGIN=$(git remote get-url origin 2>/dev/null || true)
  if [[ "$ORIGIN" =~ github\.com[:/]([^/]+/[^/.]+) ]]; then
    REPO="${BASH_REMATCH[1]%.git}"
  else
    echo "错误: 请提供 owner/repo 或确保在 git 仓库内"
    exit 1
  fi
fi

TOKEN="${GITHUB_TOKEN:-}"
if [ -z "$TOKEN" ] && command -v gh &>/dev/null; then
  TOKEN=$(gh auth token 2>/dev/null || true)
fi
if [ -z "$TOKEN" ]; then
  echo "错误: 请设置 GITHUB_TOKEN 或安装 gh 并执行 gh auth login"
  exit 1
fi

API="https://api.github.com"
echo "触发 $REPO $WORKFLOW (ref=$BRANCH)..."
curl -sS -X POST \
  -H "Accept: application/vnd.github+json" \
  -H "Authorization: Bearer $TOKEN" \
  "$API/repos/$REPO/actions/workflows/$WORKFLOW/dispatches" \
  -d "{\"ref\":\"$BRANCH\"}"

echo "等待 run 出现..."
sleep 8
for i in $(seq 1 60); do
  RES=$(curl -sS -H "Authorization: Bearer $TOKEN" \
    "$API/repos/$REPO/actions/runs?per_page=1" | \
    jq -r '.workflow_runs[0] | "\(.status) \(.conclusion)"' 2>/dev/null || echo "null null")
  STATUS=$(echo "$RES" | awk '{print $1}')
  CONCLUSION=$(echo "$RES" | awk '{print $2}')
  if [ "$STATUS" = "completed" ]; then
    if [ "$CONCLUSION" = "success" ]; then
      echo "部署工作流执行成功：$REPO"
      exit 0
    else
      echo "部署工作流失败：$REPO (conclusion=$CONCLUSION)"
      exit 1
    fi
  fi
  echo "  [$i/60] 运行中 (status=$STATUS)..."
  sleep 10
done
echo "超时：工作流未在 10 分钟内完成"
exit 1
