# Deployment Scripts

Deployment scripts for the BulkBasket backend.

## deploy.sh
Deploys the backend to the configured cloud host.

## Steps
1. Push to `main` branch
2. GitHub Actions runs CI
3. On success, deploys automatically

## Manual Deploy
```powershell
git checkout main
git pull origin main
# Follow your cloud host deployment steps
```