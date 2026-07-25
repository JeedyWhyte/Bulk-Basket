# BulkBasket Development Start Script
# Run this from the repo root to start the backend

Write-Host "Starting BulkBasket Backend..." -ForegroundColor Green

# Start backend
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend; venv\Scripts\activate; python manage.py runserver"

# Start Celery
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend; venv\Scripts\activate; celery -A config worker --pool=solo"

Write-Host "Backend running at http://localhost:8000" -ForegroundColor Green
Write-Host "Open Android Studio and run the app" -ForegroundColor Green