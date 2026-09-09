import os
from .base import *

DEBUG = False

# Fail fast if the real secret key is missing rather than silently running
# with the dev fallback from base.py.
SECRET_KEY = os.environ['SECRET_KEY']

ALLOWED_HOSTS = os.environ.get(
    'ALLOWED_HOSTS', 'bulkbasket-backend.onrender.com'
).split(',')

# Database — use Supabase PostgreSQL in production
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.postgresql',
        'NAME': os.environ.get('DB_NAME'),
        'USER': os.environ.get('DB_USER'),
        'PASSWORD': os.environ.get('DB_PASSWORD'),
        'HOST': os.environ.get('DB_HOST'),
        'PORT': os.environ.get('DB_PORT', '6543'),
        'OPTIONS': {
            # 'sslmode': 'require',
        },
    }
}

# Redis — Railway provides this
CELERY_BROKER_URL = os.environ.get('REDIS_URL', '')
CELERY_RESULT_BACKEND = CELERY_BROKER_URL

# Static files
STATIC_ROOT = os.path.join(BASE_DIR, 'staticfiles')
STATIC_URL = '/static/'

# Security
SECURE_PROXY_SSL_HEADER = ('HTTP_X_FORWARDED_PROTO', 'https')
SECURE_SSL_REDIRECT = True
SESSION_COOKIE_SECURE = True
CSRF_COOKIE_SECURE = True

# CORS — the clients are native mobile apps (no browser origin), so no
# cross-origin browser access is needed. Add origins explicitly if a web
# frontend is ever introduced.
CORS_ALLOW_ALL_ORIGINS = False
CORS_ALLOWED_ORIGINS = [
    origin
    for origin in os.environ.get('CORS_ALLOWED_ORIGINS', '').split(',')
    if origin
]