---
name: local-verify
description: Verifies local deployment of a single service (nginx, star-sso, star-reward, star-reward-app). Ensures clean environment before deploy, runs health checks, returns success/failure. On failure, passes error to localFix for resolution.
---

# Local Verify

Verifies local deployment for a single service in star-deploy-local. Use when deploying or validating a specific service after localFix modifications.

## Service Parameter

`service`: one of `nginx` | `star-sso` | `star-reward` | `star-reward-app`

## Workflow

1. **Clean environment** (avoid stale state from previous errors):
   - `star-sso` / `star-reward`: `cd ~/docker/star-deploy-local/backend && docker compose down` then deploy
   - `star-reward-app`: `deploy-frontend.sh clean` then deploy
   - `nginx`: `cd ~/docker/star-deploy-local && docker compose down` then `up -d`

2. **Deploy**:
   - `star-sso` / `star-reward`: `deploy-backend.sh deploy local`
   - `star-reward-app`: `deploy-frontend.sh deploy`
   - `nginx`: `docker compose --env-file .env.local up -d`

3. **Health check**:
   - SSO: `curl -s -o /dev/null -w "%{http_code}" http://localhost/api/sso/health` (expect 200)
   - Reward: `curl -s -o /dev/null -w "%{http_code}" -X POST -H "Content-Type: application/json" -d '{}' http://localhost/api/reward/health` (expect 200)
   - Frontend: `curl -s -o /dev/null -w "%{http_code}" http://localhost/star/` (expect 200)
   - Nginx: `curl -s -o /dev/null -w "%{http_code}" http://localhost/health` (expect 200)

4. **Return**:
   - Success: `{ success: true }`
   - Failure: `{ success: false, error: "<captured stderr/stdout and health check result>" }` → pass to localFix

## On Failure

Invoke localFix with the `error` content. Do not retry without fix.
