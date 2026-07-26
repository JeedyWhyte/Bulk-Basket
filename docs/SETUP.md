# Development Setup Guide

## Backend Setup (Windows)

### Prerequisites
- Python 3.11+
- PostgreSQL 15+
- Memurai (Redis for Windows) from memurai.com
- Git for Windows

### Steps
1. Clone the repo
2. `cd backend`
3. `python -m venv venv`
4. `venv\Scripts\activate`
5. `pip install -r requirements.txt`
6. Copy `.env.example` to `.env` and fill in values
7. `python manage.py migrate`
8. `python manage.py createsuperuser`
9. `python manage.py runserver`

## Android Setup

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17 (bundled with Android Studio)
- Android emulator (API 26+) or physical device

### Steps
1. Open `android/` in Android Studio
2. Wait for Gradle sync
3. Add `google-services.json` from Firebase to `android/app/`
4. Update `BASE_URL` in `utils/Constants.kt`
5. Start the Django backend
6. Press Run in Android Studio