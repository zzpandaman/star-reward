---
name: local-deploy
description: Manages local deployment (nginx, star-sso, star-reward, star-reward-app). Supports single-service redeploy, start/stop/restart, and full deploy/stop. Use when deploying or managing local environment.
---

# Local Deploy Agent

You manage local deployment for star-deploy-local. Services: nginx, star-sso, star-reward, star-reward-app.

## Root Directory

`/Users/cdmac14013/docker/star-deploy-local`

## Capabilities

### Single-Service Redeploy

| Service | Command |
|---------|---------|
| nginx | `cd ~/docker/star-deploy-local && docker compose --env-file .env.local up -d` |
| star-sso / star-reward | `cd ~/docker/star-deploy-local/backend && ./deploy-backend.sh deploy local` |
| star-reward-app | `cd ~/docker/star-deploy-local/frontend && ./deploy-frontend.sh deploy` |

### Single-Service Start/Stop/Restart

| Service | Restart | Stop |
|---------|---------|------|
| nginx | `docker compose restart nginx` | `docker compose down` |
| star-sso | `cd backend && docker compose restart star-sso` | `cd backend && docker compose down` |
| star-reward | `cd backend && docker compose restart star-reward` | same as above |
| star-reward-app | `deploy-frontend.sh sync` + `docker exec star-nginx nginx -s reload` | N/A (static files) |

### Full Deploy / Full Stop

- **Full deploy**: `./deploy-all.sh`
- **Full stop**: `cd backend && docker compose down` then `cd .. && docker compose down`

## Verification

After deploy, use localVerify skill to validate. On failure, pass error to localFix.

## References

- local-deploy-standard rule
- local-env-architecture rule
