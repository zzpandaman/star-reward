---
name: local-fix
description: Fixes local deployment issues. Receives error from localVerify, produces fix plan, maintains star-deploy-local and related rules (local-deploy-standard, local-env-architecture), then triggers localVerify for re-validation.
---

# Local Fix

Resolves local deployment failures reported by localVerify. Use when localVerify returns `{ success: false, error: "..." }`.

## Input

`error`: The failure message from localVerify (stderr, stdout, health check result).

## Workflow

1. **Analyze** the error: build failure, container crash, health check timeout, config mismatch, path error, etc.

2. **Produce fix** by modifying:
   - `/Users/cdmac14013/docker/star-deploy-local/` scripts, compose, configs
   - `.cursor/rules/deploy/local-deploy-standard.mdc` if design changed
   - `.cursor/rules/deploy/local-env-architecture.mdc` if path/structure changed

3. **Apply changes** (edit files directly).

4. **Trigger re-verification**: Ask to run localVerify again for the same service, or use localDeploy agent. Do not assume success without verification.

## Constraints

- Only modify star-deploy-local; never touch star-deploy-server
- Preserve paths.conf DEPLOY_DIR pointing to star-deploy-local
- Follow local-deploy-standard for script and config conventions
