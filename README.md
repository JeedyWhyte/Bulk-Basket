<div align="center">

# 🧺 BulkBasket

### From the Market, to Your Kitchen Door.

**A three-sided mobile marketplace connecting bulk buyers, local sellers, and dispatch riders.**

![Status](https://img.shields.io/badge/status-in_development-yellow)
![Platform](https://img.shields.io/badge/platform-Android-3DDC84)
![Backend](https://img.shields.io/badge/backend-Django-092E20)
![Database](https://img.shields.io/badge/database-Supabase-3ECF8E)
![License](https://img.shields.io/badge/license-Academic-blue)

[Overview](#-overview) • [Architecture](#-architecture) • [Tech Stack](#-tech-stack) • [Setup](#-setup--installation) • [API Docs](#-api-documentation) • [Contributing](#-contributing) • [Roadmap](#-roadmap--milestones)

</div>

---

## 📋 Table of Contents

1. [Overview](#-overview)
2. [The Problem](#-the-problem)
3. [The Solution](#-the-solution)
4. [Features](#-features)
5. [Architecture](#-architecture)
6. [Tech Stack](#-tech-stack)
7. [Setup & Installation](#-setup--installation)
8. [Project Structure](#-project-structure)
9. [API Documentation](#-api-documentation)
10. [Database Schema](#-database-schema)
11. [Team Structure](#-team-structure)
12. [Contributing](#-contributing)
13. [Roadmap & Milestones](#-roadmap--milestones)
14. [Testing](#-testing)
15. [Deployment](#-deployment)
16. [License & Academic Info](#-license--academic-info)

---

## 🎯 Overview

**BulkBasket** is a native Android mobile application built as a group project for the *Mobile Application Development* course (Academic Year 2025/2026). It digitises the informal bulk goods trade by creating a unified marketplace where:

- 🛒 **Buyers** can browse and purchase goods in bulk from local sellers
- 🏪 **Sellers** can list their stock, receive digital orders, and grow their customer base
- 🛵 **Dispatch Riders** can pick up delivery jobs and earn from short-distance deliveries

All three user types interact through a single Android application powered by a shared backend.

> **In one sentence:** BulkBasket is to bulk market goods what Uber Eats is to restaurant food — except we serve raw ingredients and household supplies, not prepared meals.

---

## 🔍 The Problem

The informal retail market is one of the largest sources of everyday goods, yet it's largely undigitised. Three specific pain points exist:

### For Buyers 🛒
- Open markets are far, time-consuming, and physically demanding
- Convenience stores charge premium prices for small quantities
- Restaurant delivery apps don't serve raw ingredients or bulk goods
- No structured way to order bulk market goods online

### For Sellers 🏪
- Limited customer reach beyond physical stall foot traffic
- No digital presence or way to receive online orders
- No affordable delivery infrastructure
- Lost revenue from unsold perishable inventory

### For Riders 🛵
- No structured platform for short-distance bulk goods delivery
- Underutilised time between gigs on other platforms
- No tailored job assignment system for local trips

---

## 💡 The Solution

BulkBasket bridges all three gaps with one platform:

| User Type | Their Need | What BulkBasket Provides |
|-----------|-----------|--------------------------|
| **Buyer** | Bulk goods delivered to their door | Browse sellers, order in bulk, real-time tracking |
| **Seller** | More customers, digitally | Product listings, order management, delivery handled |
| **Rider** | Income from local deliveries | Job assignments, navigation, earnings tracker |

### User Journey

```
1. Buyer opens app    →   Searches for ingredient or category
2. App shows          →   Nearby sellers with bulk pricing & stock
3. Buyer selects      →   Adds items to cart, places order
4. Seller receives    →   Notification, confirms, prepares goods
5. Rider assigned     →   Picks up from seller, delivers to buyer
6. Buyer tracks       →   Real-time delivery progress
7. Order complete     →   Goods delivered, payment settled
```

---

## ✨ Features

### 🛒 Buyer App
- User registration & login (email/password via Supabase Auth)
- Location-based seller discovery
- Product search & category filtering
- Seller profile with stock & bulk pricing
- Shopping cart & checkout flow
- Real-time order tracking with map view
- Order history & profile management
- Offline-first product browsing

### 🏪 Seller App
- Business profile setup & verification
- Product listing management (add, edit, remove)
- Incoming order notifications with accept/reject
- Active order tracking (preparing → ready → dispatched)
- Daily sales dashboard
- Inventory management with stock alerts
- Earnings & analytics summary

### 🛵 Dispatch Rider App
- Rider registration & availability toggle
- Incoming delivery job queue
- Active delivery navigation
- Pickup & drop-off confirmation
- Earnings summary & history
- Rating system

---

## 🏗 Architecture

BulkBasket follows a **layered client-server architecture** with a real-time event layer.

### High-Level Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                      CLIENT LAYER (Android)                     │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐       │
│  │  Buyer App   │    │  Seller App  │    │  Rider App   │       │
│  │   (Kotlin)   │    │   (Kotlin)   │    │   (Kotlin)   │       │
│  └──────┬───────┘    └──────┬───────┘    └──────┬───────┘       │
│         │                   │                    │              │
│         └───────────────────┴────────────────────┘              │
│                             │                                   │
│              HTTPS / JSON / JWT Authorization                   │
└─────────────────────────────┼───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    APPLICATION LAYER (Backend)                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │              Django REST Framework API                  │    │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────┐   │    │
│  │  │ Products │  │  Orders  │  │  Users   │  │Delivery│   │    │
│  │  └──────────┘  └──────────┘  └──────────┘  └────────┘   │    │
│  └─────────────────────────────────────────────────────────┘    │
│                             │                                   │
│  ┌──────────────┐    ┌─────────────┐    ┌────────────────┐      │
│  │ Celery       │    │   Redis     │    │ Supabase Auth  │      │
│  │ (configured, │◄──►│  (Cache &   │    │  (JWT Tokens)  │      │
│  │  no jobs yet)│    │   Broker)   │    │                │      │
│  └──────────────┘    └─────────────┘    └────────────────┘      │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                       DATA LAYER (Supabase)                     │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────────────┐    │
│  │  PostgreSQL  │  │   Realtime   │  │   Object Storage    │    │
│  │  (Database)  │  │ (WebSockets) │  │ (Images & Media)    │    │
│  └──────────────┘  └──────────────┘  └─────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

### Android App Architecture (MVVM + Repository)

```
┌─────────────────────────────────────────────────────────┐
│                     UI LAYER                            │
│  ┌────────────────┐         ┌────────────────────┐      │
│  │   Activity /   │◄───────►│    ViewModel       │      │
│  │   Fragment     │ observe │  (StateFlow /      │      │
│  │   (Composable) │         │   LiveData)        │      │
│  └────────────────┘         └─────────┬──────────┘      │
└──────────────────────────────────────┬─┴────────────────┘
                                       │
┌──────────────────────────────────────▼─────────────────┐
│                  REPOSITORY LAYER                      │
│  ┌──────────────────────────────────────────────────┐  │
│  │            Repository (Single Source)            │  │
│  │   Decides: Remote (API) or Local (Room) ?        │  │
│  └─────┬───────────────────────────┬────────────────┘  │
└────────┼───────────────────────────┼───────────────────┘
         │                           │
┌────────▼────────────┐    ┌─────────▼────────────┐
│   REMOTE DATA       │    │   LOCAL DATA         │
│ ┌────────────────┐  │    │ ┌────────────────┐   │
│ │  Retrofit 2    │  │    │ │  Room (SQLite) │   │
│ │  + OkHttp      │  │    │ │                │   │
│ └───────┬────────┘  │    │ └────────────────┘   │
└─────────┼───────────┘    └──────────────────────┘
          │
          ▼
   Django REST API
```

### Real-Time Order Flow

```
Buyer places order
        │
        ▼
┌──────────────────┐
│  Django API      │
│  POST /orders/   │──────►  PostgreSQL (INSERT)
└────────┬─────────┘                │
         │                          │
         │                          ▼
         │              ┌───────────────────────┐
         │              │ Supabase Realtime     │
         │              │ broadcasts to channel │
         │              └───────────┬───────────┘
         │                          │
         ▼                          ▼
   Django notifications     ┌──────────────────────┐
   service sends FCM        │ WebSocket subscribers│
   push directly (no        │  • Buyer (track)     │
   Celery task involved)    │  • Seller (new order)│
                            │  • Rider (job alert) │
                            └──────────────────────┘
```

---

## 🛠 Tech Stack

### Mobile Client (Android)
| Technology | Purpose |
|-----------|---------|
| **Kotlin** | Primary language for Android development |
| **Jetpack Compose / XML** | UI framework |
| **Retrofit 2 + OkHttp** | Type-safe HTTP client for API calls |
| **Kotlin Coroutines + Flow** | Asynchronous operations & reactive streams |
| **Room (SQLite)** | Local database for offline caching |
| **Hilt (Dagger)** | Dependency injection |
| **Coil** | Image loading & caching |
| **Firebase Cloud Messaging** | Push notifications |

### Backend (Server)
| Technology | Purpose |
|-----------|---------|
| **Python 3.11+** | Backend programming language |
| **Django 5.0+** | Web framework |
| **Django REST Framework** | REST API framework |
| **Celery** | Async background task queue (configured; no tasks are wired up yet — notifications currently send synchronously) |
| **Redis** | Cache + Celery message broker |
| **PyJWT** | JWT token verification |
| **python-dotenv** | Environment configuration |

### Database & Auth (Supabase)
| Technology | Purpose |
|-----------|---------|
| **PostgreSQL** | Primary relational database |
| **Supabase Auth** | User authentication (JWT-based) |
| **Supabase Realtime** | WebSocket-based live updates |
| **Supabase Storage** | Object storage for images |
| **Row Level Security (RLS)** | Database-level access control |

### DevOps & Tooling
| Technology | Purpose |
|-----------|---------|
| **Docker + Docker Compose** | Local development environment |
| **GitHub Actions** | CI/CD pipeline |
| **Pytest** | Backend testing |
| **JUnit + Espresso** | Android testing |
| **Postman** | API testing & documentation |
| **Figma** | UI/UX design |

---

## 🚀 Setup & Installation

### Prerequisites

Before starting, ensure you have the following installed:

- [ ] **Python 3.11+** ([download](https://python.org))
- [ ] **Node.js 18+** (for any tooling)
- [ ] **Docker Desktop** ([download](https://docker.com/products/docker-desktop))
- [ ] **Android Studio** Hedgehog or later ([download](https://developer.android.com/studio))
- [ ] **Git** ([download](https://git-scm.com))
- [ ] A **Supabase account** ([sign up](https://supabase.com))
- [ ] A **Firebase project** (for FCM)

### 1. Clone the Repository

```bash
git clone https://github.com/jeedywhyte/bulkbasket.git
cd bulkbasket
```

### 2. Backend Setup

```bash
# Navigate to backend directory
cd backend

# Copy environment variables template
cp .env.example .env

# Edit .env with your credentials (see backend/.env.example for the full list)
# DJANGO_ENV=development
# SECRET_KEY=your-secret-key-here
# DB_NAME=bulkbasket_dev
# DB_USER=postgres
# DB_PASSWORD=postgres
# DB_HOST=db
# DB_PORT=5432
# REDIS_URL=redis://redis:6379/0
# SUPABASE_URL=https://xxxxx.supabase.co
# SUPABASE_KEY=your-anon-key
# FCM_SERVER_KEY=your-firebase-key

# Spin up services with Docker Compose
docker-compose up -d

# This starts:
#   - Django backend on port 8000
#   - Redis on port 6379
#   - Celery worker

# Run migrations
docker-compose exec backend python manage.py migrate

# Create a superuser (optional, for Django admin)
docker-compose exec backend python manage.py createsuperuser

# Verify backend is running (there's no dedicated /health/ endpoint yet,
# so hit the admin login page instead)
curl -I http://localhost:8000/admin/
# Expected: HTTP/1.1 302 Found
```

### 3. Supabase Setup

1. Create a new project at [supabase.com](https://supabase.com)
2. Navigate to **Settings → API** and copy:
   - Project URL
   - `anon` public key
   - `service_role` secret key
3. Navigate to **Settings → Database** and copy the individual connection values (host, port, database name, user, password) into `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
4. Schema is managed by Django's own migrations (`python manage.py migrate`, run in step 2) — apply the RLS policies from `backend/supabase/policies/` via the Supabase SQL editor
5. Enable Row Level Security on all tables
6. Add the credentials to your `.env` file

### 4. Android App Setup

```bash
# Open Android Studio
# File → Open → Select the `android/` folder

# Sync Gradle
# Build → Make Project

# Run on emulator or device
# Run → Run 'app'
```

> **Note:** The API base URL is a hardcoded constant in
> `android/app/src/main/java/com/bulkbasket/utils/Constants.kt` — it is
> **not** read from `local.properties`. It defaults to the production
> backend (`https://bulkbasket-backend.onrender.com/api/v1/`). To point the
> app at a local backend instead, temporarily edit that constant to your
> PC's LAN IP (e.g. `http://192.168.x.x:8000/api/v1/`) and rebuild —
> physical devices can't reach `10.0.2.2`, only the emulator can.

### 5. Firebase Cloud Messaging Setup

1. Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
2. Add an Android app with your package name
3. Download `google-services.json` and place in `android/app/`
4. In Firebase Console → Project Settings → Service Accounts, generate a new private key
5. Save the key file as `backend/firebase-credentials.json`

### 6. Verify the Setup

```bash
# Check backend (no /health/ endpoint yet — check the admin page responds)
curl -I http://localhost:8000/admin/
# Expected: HTTP/1.1 302 Found

# Check Redis
docker-compose exec redis redis-cli ping
# Expected: PONG

# Check Celery
docker-compose logs celery
# Expected: "celery@worker ready."
```

---

## 📁 Project Structure

```
bulkbasket/
├── README.md                       # This file
├── docker-compose.yml              # Local dev environment
├── .github/
│   └── workflows/
│       ├── backend-ci.yml          # Backend CI pipeline
│       └── android-ci.yml          # Android CI pipeline
│
├── backend/                        # Django backend
│   ├── manage.py
│   ├── Dockerfile
│   ├── requirements.txt
│   ├── .env.example
│   ├── config/                     # Django project config
│   │   ├── settings.py
│   │   ├── urls.py
│   │   └── celery.py
│   ├── apps/
│   │   ├── users/                  # User management
│   │   ├── products/               # Product catalog
│   │   ├── orders/                 # Order processing
│   │   ├── delivery/               # Dispatch & tracking
│   │   └── notifications/          # FCM integration
│   ├── supabase/
│   │   └── policies/               # RLS policies (schema itself is
│   │                               #   managed by Django migrations)
│   └── apps/*/tests/               # Pytest test suite (per-app, e.g.
│                                   #   apps/orders/tests/)
│
├── android/                        # Android app
│   ├── build.gradle.kts
│   ├── app/
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/bulkbasket/
│   │       ├── data/               # Data layer (Repository, API, DB)
│   │       │   ├── remote/         # Retrofit API service
│   │       │   ├── local/          # Room database
│   │       │   └── repository/     # Repositories
│   │       ├── domain/             # Domain models & use cases
│   │       ├── ui/                 # UI layer
│   │       │   ├── buyer/          # Buyer screens & ViewModels
│   │       │   ├── seller/         # Seller screens & ViewModels
│   │       │   ├── rider/          # Rider screens & ViewModels
│   │       │   └── common/         # Shared components
│   │       └── di/                 # Hilt modules
│   └── design/                     # Figma exports, assets
│
├── docs/                           # Documentation
│   ├── architecture.md
│   ├── api-spec.md
│   ├── database-schema.md
│   ├── deployment.md
│   └── diagrams/
│
└── scripts/                        # Utility scripts
    ├── seed-db.py
    └── reset-db.sh
```

---

## 📡 API Documentation

Base URL: `http://localhost:8000/api/v1/`

Tokens are issued directly by this API (`djangorestframework-simplejwt`) — there is no Supabase Auth integration. All endpoints except `/users/register/`, `/users/login/`, and `/users/token/refresh/` require an `Authorization: Bearer <JWT>` header. There is no `/auth/` prefix and no logout endpoint (clients just discard their tokens).

See [API.md](./API.md) for the full reference — this section is a condensed summary.

### Auth & Users

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/users/register/` | Register a new user | ❌ |
| `POST` | `/users/login/` | Log in with username/password | ❌ |
| `POST` | `/users/token/refresh/` | Refresh an expired access token | ❌ |
| `GET`/`PATCH` | `/users/profile/` | Get / update own profile | ✅ |
| `GET`/`POST` | `/users/addresses/` | List / add delivery addresses | ✅ |

**Register Request:**
```json
POST /api/v1/users/register/
{
  "username": "janedoe",
  "email": "user@example.com",
  "password": "SecurePass123!",
  "role": "buyer",
  "phone_number": "+2348012345678"
}
```

**Login Response** (unwrapped, from `TokenObtainPairView`):
```json
{
  "refresh": "eyJhbGc...",
  "access": "eyJhbGc..."
}
```

### Products

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/products/` | List products (paginated, filterable) | ✅ |
| `GET` | `/products/<id>/` | Get product details | ✅ |
| `GET` | `/products/categories/` | List categories (paginated) | ✅ |
| `POST` | `/products/` | Create product (seller only) | ✅ |
| `PATCH` | `/products/<id>/` | Update product (seller only) | ✅ |
| `DELETE` | `/products/<id>/` | Soft-delete product (seller only) | ✅ |

### Sellers

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/sellers/nearby/` | List nearby sellers | ✅ |
| `GET` | `/sellers/<id>/` | Get seller profile | ✅ |
| `POST` | `/sellers/profile/` | Create seller profile | ✅ |
| `GET`/`PATCH` | `/sellers/profile/me/` | Get / update own seller profile | ✅ |

### Orders

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/orders/` | Place a new order (buyer) | ✅ |
| `GET` | `/orders/` | List my orders (buyer) | ✅ |
| `GET` | `/orders/<id>/` | Get order details (buyer, own order) | ✅ |
| `GET` | `/orders/seller/` | List orders for my store (seller) | ✅ |
| `PATCH` | `/orders/seller/<id>/status/` | Advance order status (seller) | ✅ |

**Place Order Request:**
```json
POST /api/v1/orders/
{
  "seller_id": 7,
  "delivery_address_id": 3,
  "items": [
    { "product_id": 12, "quantity": 2 },
    { "product_id": 15, "quantity": 1 }
  ],
  "notes": "Please call on arrival"
}
```
`seller_id`, `delivery_address_id`, and `product_id` are integer IDs — only the `Order` row itself uses a UUID primary key. There's a single generic status-update endpoint for sellers, not separate accept/reject/ready routes.

### Delivery

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/delivery/available/` | Available jobs (rider) | ✅ |
| `GET` | `/delivery/active/` | My active deliveries (rider) | ✅ |
| `POST` | `/delivery/<id>/accept/` | Accept a delivery job | ✅ |
| `PATCH` | `/delivery/<id>/status/` | Update delivery status | ✅ |
| `PATCH` | `/delivery/location/` | Push current GPS location | ✅ |

There's no earnings-summary endpoint currently.

### Response & Error Shapes

Two response shapes are used, not one global envelope: plain DRF serialization (most `GET`s) and a `{"status", "message", "data"}` envelope for actions like registration, placing an order, or updating a status. List endpoints use DRF's standard `{"count", "next", "previous", "results"}` pagination envelope.

**Common HTTP Status Codes actually returned:** `200`, `201`, `204`, `400`, `401`, `403`, `404`, `500`. No rate limiting is configured yet, so `429` is never returned.

---

## 🗄 Database Schema

This reflects the actual Django models (`backend/apps/*/models.py`), not the earlier Supabase-Auth-style design this section originally described. Table names below are the real Postgres table names Django generates (`<app_label>_<model_name>`). Every user-facing PK is a plain auto-incrementing integer **except** `orders_order`, which genuinely uses a UUID (`default=uuid.uuid4`). There is no separate `sellers` table with its own UUID — seller info lives in `sellers_sellerprofile`, one-to-one with `users_user`, and `products_product.seller_id`/`orders_order.seller_id` point straight at `users_user`, not at the seller profile.

### Entity Relationship Diagram

```
┌───────────────────────┐        ┌───────────────────────┐
│      users_user         │        │     users_address       │
├───────────────────────┤        ├───────────────────────┤
│ id (PK, int)              │◄───────┤ user_id (FK, CASCADE)   │
│ username, email           │  1:N   │ label, street, city,    │
│ password (Django auth,    │        │   state                  │
│   not Supabase Auth)      │        │ latitude, longitude      │
│ role: buyer / seller /   │        │ is_default                │
│   rider (no "admin")     │        └───────────┬───────────┘
│ phone_number               │                    │
│ avatar_url, is_verified   │                    │ delivery_address_id
│ fcm_token                  │                    │ (FK, nullable, SET_NULL)
│ is_staff / is_superuser   │                    │
│   (Django admin access)   │                    │
│ created_at / updated_at   │                    │
└──┬───────────┬──────────┘                    │
   │ 1:1         │ 1:1                            │
   │ (seller)    │ (rider)                        │
   ▼             ▼                                 │
┌────────────────┐ ┌────────────────────┐         │
│ sellers_          │ │ delivery_            │         │
│ sellerprofile      │ │ riderprofile         │         │
├────────────────┤ ├────────────────────┤         │
│ user_id (FK, 1:1)  │ │ user_id (FK, 1:1)      │         │
│ business_name       │ │ is_available            │         │
│ market_name         │ │ current_latitude/        │         │
│ description         │ │   longitude                │         │
│ latitude/longitude  │ │ total_deliveries          │         │
│ rating,               │ │ rating                     │         │
│   total_ratings      │ └────────────────────┘         │
│ is_open, opening_time/                                     │
│   closing_time        │                                     │
└────────────────┘                                     │
                                                             │
users_user (role='seller') ───────────────┐                │
        │ seller_id (FK, CASCADE)          │                │
        ▼                                    │                │
┌─────────────────────┐                    │                │
│ products_product       │                    │                │
├─────────────────────┤                    │                │
│ id (PK, int)              │                    │                │
│ seller_id (FK → users)    │                    │                │
│ category_id (FK, nullable,│                    │                │
│   SET_NULL)                │                    │                │
│ name, description          │                    │                │
│ price, unit                 │                    │                │
│ min_order_qty                │                    │                │
│ stock_quantity                │                    │                │
│ image_url, is_available      │                    │                │
└──────────┬──────────────┘                    │                │
           │ product_id (FK, PROTECT)             │                │
           ▼                                        ▼                ▼
┌─────────────────────┐               ┌────────────────────────────┐
│ orders_orderitem       │               │        orders_order            │
├─────────────────────┤               ├────────────────────────────┤
│ order_id (FK, CASCADE)   │◄──────────────┤ id (PK, UUID)                    │
│ product_id (FK, PROTECT) │   1:N          │ buyer_id (FK → users_user)     │
│ quantity                   │               │ seller_id (FK → users_user)    │
│ unit_price                  │               │ delivery_address_id (FK,       │
│ total_price                 │               │   nullable)                       │
└─────────────────────┘               │ status, subtotal, delivery_fee,│
                                          │   total, notes                     │
                                          └──────────────┬──────────────┘
                                                             │ 1:1 (order_id)
                                                             ▼
                                          ┌────────────────────────────┐
                                          │      delivery_delivery         │
                                          ├────────────────────────────┤
                                          │ order_id (FK, 1:1, CASCADE)    │
                                          │ rider_id (FK → users_user,     │
                                          │   nullable, SET_NULL)            │
                                          │ status                            │
                                          │ current_latitude/longitude     │
                                          │ assigned_at / picked_up_at /   │
                                          │   delivered_at                    │
                                          └────────────────────────────┘

┌────────────────────────────┐
│  notifications_notification    │
├────────────────────────────┤
│ recipient_id (FK → users_user, │
│   CASCADE)                        │
│ title, body                       │
│ notification_type                 │
│ data (JSONB)                       │
│ is_read, created_at                │
└────────────────────────────┘
```

`role='seller'` / `role='rider'` / `role='buyer'` constraints on FKs (e.g. `Product.seller`, `Order.buyer`) are enforced via DRF `limit_choices_to` and application logic, not a database-level `CHECK` constraint.

### Core Tables

#### `users_user` (extends Django's `AbstractUser`)
| Column | Type | Description |
|--------|------|-------------|
| `id` | INTEGER (PK, auto) | Plain auto-increment PK — **not** a UUID, and not tied to any Supabase Auth UID |
| `username` | VARCHAR(150) | Django's built-in login username (unique) |
| `email` | VARCHAR(254) | From `AbstractUser`; not unique by default |
| `password` | VARCHAR | Hashed by Django's own auth system — there is no Supabase Auth integration |
| `role` | VARCHAR(10) | `buyer`, `seller`, or `rider`. **There is no `admin` role** — admin/staff access uses Django's built-in `is_staff` / `is_superuser` flags instead |
| `phone_number` | VARCHAR(15) | Not called `phone` |
| `avatar_url` | VARCHAR (URL) | |
| `is_verified` | BOOLEAN | |
| `fcm_token` | VARCHAR(255) | Firebase Cloud Messaging push token |
| `is_staff` / `is_superuser` | BOOLEAN | Inherited from `AbstractUser` |
| `created_at` / `updated_at` | TIMESTAMP | |

#### `users_address`
| Column | Type | Description |
|--------|------|-------------|
| `id` | INTEGER (PK, auto) | |
| `user_id` | INTEGER (FK → `users_user.id`) | `on_delete=CASCADE` |
| `label` | VARCHAR(50) | e.g. "Home", "Office" |
| `street` | VARCHAR(255) | |
| `city` | VARCHAR(100) | |
| `state` | VARCHAR(100) | |
| `latitude` | DECIMAL(9,6), nullable | Named `latitude`, not `lat` |
| `longitude` | DECIMAL(9,6), nullable | Named `longitude`, not `lng` |
| `is_default` | BOOLEAN | |

This is its own table, one-to-many from `users_user` — orders reference a row here (`delivery_address_id`), not an embedded address string or JSON blob.

#### `sellers_sellerprofile`
| Column | Type | Description |
|--------|------|-------------|
| `id` | INTEGER (PK, auto) | |
| `user_id` | INTEGER (FK → `users_user.id`, one-to-one) | `on_delete=CASCADE` |
| `business_name` | VARCHAR(200) | |
| `market_name` | VARCHAR(200) | |
| `description` | TEXT, blank | |
| `latitude` / `longitude` | DECIMAL(9,6), nullable | No flat `address` text field — location is coordinates only |
| `rating` | DECIMAL(3,2), default 0.00 | |
| `total_ratings` | INTEGER (unsigned), default 0 | |
| `is_open` | BOOLEAN, default true | |
| `opening_time` / `closing_time` | TIME, nullable | |
| `created_at` / `updated_at` | TIMESTAMP | |

There is no `verified` column here — seller verification, if used, would be `users_user.is_verified` on the linked user.

#### `products_category`
| Column | Type | Description |
|--------|------|-------------|
| `id` | INTEGER (PK, auto) | |
| `name` | VARCHAR(100), unique | |
| `slug` | VARCHAR (slug), unique | |
| `icon_url` | VARCHAR (URL), blank | |

Categories are a real table with an FK, not a free-text string on `products`.

#### `products_product`
| Column | Type | Description |
|--------|------|-------------|
| `id` | INTEGER (PK, auto) | |
| `seller_id` | INTEGER (FK → `users_user.id`) | `on_delete=CASCADE`; must be a `role='seller'` user |
| `category_id` | INTEGER (FK → `products_category.id`), nullable | `on_delete=SET_NULL` |
| `name` | VARCHAR(200) | |
| `description` | TEXT, blank | |
| `price` | DECIMAL(12,2) | |
| `unit` | VARCHAR(10) | `kg`, `bag`, `basket`, `piece`, `bundle` — not a free-text string like `"50kg bag"` |
| `min_order_qty` | INTEGER (unsigned), default 1 | |
| `stock_quantity` | INTEGER (unsigned), default 0 | Named `stock_quantity`, not `stock_qty` |
| `image_url` | VARCHAR (URL), blank | Plain URL field — not necessarily Supabase Storage |
| `is_available` | BOOLEAN, default true | Also doubles as the soft-delete flag; the API never hard-deletes products |
| `created_at` / `updated_at` | TIMESTAMP | |

#### `orders_order`
| Column | Type | Description |
|--------|------|-------------|
| `id` | **UUID** (PK) | The one table that genuinely uses a UUID primary key (`default=uuid.uuid4`) |
| `buyer_id` | INTEGER (FK → `users_user.id`) | `on_delete=CASCADE`; must be `role='buyer'` |
| `seller_id` | INTEGER (FK → `users_user.id`) | `on_delete=CASCADE`; must be `role='seller'` — points at `users_user` directly, not at `sellers_sellerprofile` |
| `delivery_address_id` | INTEGER (FK → `users_address.id`), nullable | `on_delete=SET_NULL` — a real row reference, not a JSONB address object |
| `status` | VARCHAR(20) | `pending`, `confirmed`, `preparing`, `ready`, `in_transit`, `delivered`, `cancelled` |
| `subtotal` | DECIMAL(12,2) | |
| `delivery_fee` | DECIMAL(10,2), default 0 | |
| `total` | DECIMAL(12,2) | Named `total`, not `total_amount` |
| `notes` | TEXT, blank | |
| `created_at` / `updated_at` | TIMESTAMP | |

#### `orders_orderitem`
| Column | Type | Description |
|--------|------|-------------|
| `id` | INTEGER (PK, auto) | |
| `order_id` | UUID (FK → `orders_order.id`) | `on_delete=CASCADE` |
| `product_id` | INTEGER (FK → `products_product.id`) | **`on_delete=PROTECT`** — a product with order history can't be hard-deleted at the DB level; the API soft-deletes it (`is_available=False`) instead |
| `quantity` | INTEGER (unsigned) | |
| `unit_price` | DECIMAL(12,2) | Named `unit_price`, not `price_at_purchase` |
| `total_price` | DECIMAL(12,2) | Computed as `unit_price * quantity` in `Model.save()` — not a DB-generated column |

#### `delivery_delivery`
| Column | Type | Description |
|--------|------|-------------|
| `id` | INTEGER (PK, auto) | |
| `order_id` | UUID (FK → `orders_order.id`, one-to-one) | `on_delete=CASCADE` |
| `rider_id` | INTEGER (FK → `users_user.id`), nullable | `on_delete=SET_NULL`; must be `role='rider'` |
| `status` | VARCHAR(20) | `pending`, `assigned`, `picked_up`, `delivered`, `failed` |
| `current_latitude` / `current_longitude` | DECIMAL(9,6), nullable | |
| `assigned_at` / `picked_up_at` / `delivered_at` | TIMESTAMP, nullable | |
| `created_at` / `updated_at` | TIMESTAMP | |

#### `delivery_riderprofile`
| Column | Type | Description |
|--------|------|-------------|
| `id` | INTEGER (PK, auto) | |
| `user_id` | INTEGER (FK → `users_user.id`, one-to-one) | `on_delete=CASCADE` |
| `is_available` | BOOLEAN, default true | |
| `current_latitude` / `current_longitude` | DECIMAL(9,6), nullable | |
| `total_deliveries` | INTEGER (unsigned), default 0 | |
| `rating` | DECIMAL(3,2), default 0.00 | |
| `created_at` | TIMESTAMP | |

This table wasn't in the original diagram at all — rider metadata is a distinct profile, mirroring `sellers_sellerprofile`.

#### `notifications_notification`
| Column | Type | Description |
|--------|------|-------------|
| `id` | INTEGER (PK, auto) | |
| `recipient_id` | INTEGER (FK → `users_user.id`) | `on_delete=CASCADE` |
| `title` | VARCHAR(255) | |
| `body` | TEXT | |
| `notification_type` | VARCHAR(30) | `order_placed`, `order_confirmed`, `order_preparing`, `order_ready`, `order_in_transit`, `order_delivered`, `order_cancelled`, `delivery_assigned`, `general` |
| `data` | JSONB, default `{}` | Arbitrary structured payload for the client |
| `is_read` | BOOLEAN, default false | |
| `created_at` | TIMESTAMP | |

This table also didn't appear in the original diagram.

### Row Level Security (RLS) Policies

**This subsection previously listed several `CREATE POLICY` statements as if they were live and enforced. They weren't real — they referenced tables/columns (`users`, `sellers`, `stock_qty`) that don't exist in this schema.**

The actual situation is more nuanced than "no RLS at all":

- A single real SQL file exists at [`backend/supabase/policies/orders_rls.sql`](./backend/supabase/policies/orders_rls.sql), defining three `SELECT` policies (buyers see their own orders, sellers see orders placed with them, riders see their assigned deliveries) using Supabase's `auth.uid()`.
- That file is **not wired into anything**: it's not run by any Django migration, management command, or CI step, and nothing in the codebase references it. It also still targets the old fictional table/column names (`orders.buyer_id`, `deliveries.rider_id`) rather than the real `orders_order` / `delivery_delivery` tables, and it never issues `ALTER TABLE ... ENABLE ROW LEVEL SECURITY`, so even applied verbatim the policies would be inert.
- More fundamentally, `auth.uid()` is populated by Supabase's PostgREST/GoTrue layer from a Supabase Auth JWT. This backend's Django app connects to Postgres directly (`django.db.backends.postgresql` in `backend/config/settings/*.py`) using its own database credentials and issues its own JWTs via `djangorestframework-simplejwt` — there's no Supabase Auth session for `auth.uid()` to read, so these policies wouldn't do anything meaningful even if enabled.

**Access control is actually enforced in the Django application layer**, via DRF permission classes and `get_queryset()` filtering — not Postgres RLS. See [`backend/apps/common/permissions.py`](./backend/apps/common/permissions.py) (`IsBuyer`, `IsSeller`, `IsRider`) and per-view filtering such as:

```python
# backend/apps/orders/views.py
class BuyerOrderListCreateView(generics.ListCreateAPIView):
    permission_classes = [IsBuyer]

    def get_queryset(self):
        return Order.objects.filter(buyer=self.request.user)

class SellerOrderListView(generics.ListAPIView):
    permission_classes = [IsSeller]

    def get_queryset(self):
        return Order.objects.filter(seller=self.request.user)
```

Each role-scoped endpoint filters its queryset to `request.user` this way (buyers only ever see their own orders, sellers only see orders placed with them, riders only see deliveries assigned to them) rather than relying on database-level policies.

---

## 👥 Team Structure

BulkBasket is built by a 5-person team with clearly defined roles:

### 🎯 Role 1: Project Lead & Backend Engineer
- Owns project coordination, repo management, backend API
- Designs database schema, API contracts, deployment
- **Tech focus:** Python, Django, DRF, PostgreSQL, Docker, CI/CD

### 📱 Role 2: Android Developer — Buyer App
- Builds buyer-facing screens (home, browse, cart, tracking)
- Implements MVVM, Retrofit, Room caching
- **Tech focus:** Kotlin, Jetpack, Retrofit, Coroutines, Room

### 🏪 Role 3: Android Developer — Seller & Rider Apps
- Builds seller dashboard + rider delivery flows
- Integrates Firebase Cloud Messaging for notifications
- **Tech focus:** Kotlin, Jetpack, FCM, Room, MVVM

### 🎨 Role 4: UI/UX Designer & Brand Lead
- Owns design system (Sleek / Bold / Dashboard direction)
- Produces Figma mockups, prototypes, design specs
- **Tools:** Figma, Adobe CC (optional), Android design guidelines

### 🔧 Role 5: DevOps, Testing & Documentation Lead
- Sets up Docker, CI/CD, test infrastructure
- Writes backend (Pytest) and Android (JUnit/Espresso) tests
- Produces technical documentation
- **Tech focus:** Docker, GitHub Actions, Pytest, JUnit, Espresso

> 📋 **Detailed role responsibilities** → see `docs/team-structure.md`

---

## 🤝 Contributing

This is a private academic project, but here's how team members should collaborate:

### Git Workflow

We follow a **feature-branch workflow** with mandatory code reviews.

#### Branch Naming Convention

```
feature/<short-description>   # New feature
bugfix/<short-description>    # Bug fix
docs/<short-description>      # Documentation only
refactor/<short-description>  # Code refactoring
test/<short-description>      # Test additions
```

**Examples:**
- `feature/buyer-cart-screen`
- `bugfix/order-status-update`
- `docs/api-endpoints`

#### Step-by-Step Process

1. **Create a branch from `main`**
   ```bash
   git checkout main
   git pull
   git checkout -b feature/your-feature-name
   ```

2. **Commit early and often**
   ```bash
   git add .
   git commit -m "feat(buyer): add cart screen layout"
   ```

3. **Push to remote**
   ```bash
   git push -u origin feature/your-feature-name
   ```

4. **Open a Pull Request**
   - Use the PR template (auto-loaded)
   - Link related issues with `Closes #123`
   - Tag reviewers (Lead + 1 peer minimum)

5. **Address review comments**
   - Push fixes to the same branch
   - Mark conversations resolved after addressing
   - Re-request review when ready

6. **Merge after approval**
   - All CI checks must pass ✅
   - At least 2 approvals required
   - Use "Squash and merge" for clean history

### Commit Message Convention

We use **Conventional Commits**:

```
<type>(<scope>): <description>

Types:
  feat     - New feature
  fix      - Bug fix
  docs     - Documentation only
  style    - Code style/formatting
  refactor - Code refactoring
  test     - Adding tests
  chore    - Build/dependency updates

Examples:
  feat(api): add product search endpoint
  fix(buyer): resolve cart total calculation bug
  docs(readme): update setup instructions
  test(orders): add integration tests for checkout
```

### Code Review Standards

**Reviewers must check:**
- [ ] Code is readable and well-commented
- [ ] No hardcoded secrets or credentials
- [ ] Tests are included for new functionality
- [ ] Follows project coding conventions
- [ ] No commented-out code blocks
- [ ] Sensible variable/function names
- [ ] Error handling is implemented

**Authors must:**
- Self-review their PR before requesting reviews
- Include screenshots for UI changes
- Update relevant documentation
- Ensure all CI checks pass

### Coding Standards

#### Python (Backend)
- Follow **PEP 8**
- Use **type hints** where possible
- Run `black` formatter before commits
- Maximum line length: 100 characters

#### Kotlin (Android)
- Follow **Kotlin coding conventions**
- Use **ktlint** for formatting
- Prefer immutable `val` over mutable `var`
- Maximum line length: 120 characters

#### Naming Conventions
- **Files:** `snake_case.py` (Python), `PascalCase.kt` (Kotlin)
- **Variables:** `snake_case` (Python), `camelCase` (Kotlin)
- **Constants:** `UPPER_SNAKE_CASE`
- **Classes:** `PascalCase`

### Issue Tracking

We use **GitHub Projects** for task management. All work must be tracked:

- 🟢 **Backlog** — Future work
- 🟡 **In Progress** — Currently being worked on
- 🔵 **In Review** — PR open, awaiting review
- ✅ **Done** — Merged to main

Tag issues with appropriate labels:
- `frontend`, `backend`, `design`, `devops`, `docs`
- `priority:high`, `priority:medium`, `priority:low`
- `good-first-issue`, `bug`, `enhancement`

---

## 🗓 Roadmap & Milestones

### 14-Week Project Timeline

```
Week  ░░░░░░░░░░░░░░░░░░░░░░░░░░░░  14-Week Roadmap
─────────────────────────────────────────────────────
1-2   ▓▓░░░░░░░░░░░░░░░░░░░░░░░░░  Phase 1: Planning
3-4   ░░▓▓░░░░░░░░░░░░░░░░░░░░░░░  Phase 2: Design
5-7   ░░░░▓▓▓░░░░░░░░░░░░░░░░░░░  Phase 3: Backend
8-10  ░░░░░░░▓▓▓░░░░░░░░░░░░░░░░  Phase 4: Android
11-12 ░░░░░░░░░░▓▓░░░░░░░░░░░░░░  Phase 5: Testing
13-14 ░░░░░░░░░░░░▓▓░░░░░░░░░░░░  Phase 6: Delivery
```

### Phase 1: Planning & Setup *(Week 1–2)*
**Owner:** Lead | **Status:** 🟡 In Progress

- [x] Project concept defined
- [x] Tech stack decided
- [ ] API specification drafted
- [ ] Database schema designed
- [ ] GitHub repo + CI/CD pipeline set up
- [ ] Design direction selected (Sleek / Bold / Dashboard)
- [ ] Project proposal submitted

### Phase 2: Design *(Week 3–4)*
**Owner:** UI/UX Designer | **Status:** ⚪ Not Started

- [ ] Complete Figma design system
- [ ] Mockups for all Buyer screens
- [ ] Mockups for all Seller screens
- [ ] Mockups for all Rider screens
- [ ] Interactive prototype
- [ ] Design specs handoff document
- [ ] Android drawable asset pack

### Phase 3: Backend Development *(Week 5–7)*
**Owner:** Lead + DevOps | **Status:** ✅ Complete

- [ ] Supabase project configured
- [ ] PostgreSQL schema deployed
- [ ] User authentication flow (signup/login)
- [ ] Products CRUD endpoints
- [ ] Orders endpoints with status transitions
- [ ] Delivery assignment endpoints
- [ ] Celery background tasks
- [ ] Redis caching for hot paths
- [ ] Backend unit & integration tests (>80% coverage)
- [ ] Postman collection published

### Phase 4: Android Development *(Week 8–10)*
**Owner:** Android Devs | **Status:** ✅ Complete

#### Buyer App
- [ ] Authentication screens (login/signup)
- [ ] Home & discovery screen
- [ ] Seller profile & product listing
- [ ] Shopping cart
- [ ] Checkout flow
- [ ] Real-time order tracking
- [ ] Order history

#### Seller App
- [ ] Seller registration & profile
- [ ] Product management (CRUD)
- [ ] Order notifications
- [ ] Order acceptance/rejection
- [ ] Dashboard with daily stats

#### Rider App
- [ ] Rider registration
- [ ] Availability toggle
- [ ] Job queue
- [ ] Active delivery navigation
- [ ] Earnings summary

### Phase 5: Integration & Testing *(Week 11–12)*
**Owner:** DevOps + All | **Status:** ⚪ Not Started

- [ ] End-to-end integration testing
- [ ] Android unit tests (ViewModels)
- [ ] Espresso UI tests
- [ ] Bug fixes from QA
- [ ] Performance optimization
- [ ] Real-time event testing
- [ ] Push notification delivery testing
- [ ] Offline mode validation

### Phase 6: Final Delivery *(Week 13–14)*
**Owner:** Lead | **Status:** ⚪ Not Started

- [ ] Final technical documentation
- [ ] Final test report
- [ ] Deployment to production environment
- [ ] Presentation slides prepared
- [ ] Demo video recorded
- [ ] Final report submitted
- [ ] Live demo presentation delivered

### Future Roadmap (Post-Academic)

These features are **out of scope** for the academic deliverable but planned for future iterations:

- 💳 **Payment integration** — Paystack, Flutterwave
- 📊 **Analytics dashboard** — Advanced seller insights
- ⭐ **Rating & reviews** — Buyer reviews for sellers/riders
- 🌐 **Web admin panel** — Internal admin operations
- 📱 **iOS app** — Swift/SwiftUI version
- 🤖 **AI recommendations** — Personalized product suggestions
- 🌍 **Multi-language support** — Yoruba, Hausa, Igbo, Pidgin
- 🚚 **B2B channel** — Restaurants, canteens, caterers

---

## 🧪 Testing

### Running Tests

#### Backend (Pytest)
```bash
# Run all tests
docker-compose exec backend pytest

# Run with coverage report
docker-compose exec backend pytest --cov=apps --cov-report=html

# Run specific test file
docker-compose exec backend pytest apps/orders/tests/test_state_machine.py

# Run with verbose output
docker-compose exec backend pytest -v
```

#### Android (Gradle)
```bash
# Run unit tests
cd android
./gradlew test

# Run instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest

# Generate coverage report
./gradlew jacocoTestReport
```

### Test Coverage Goals
- **Backend:** ≥80% line coverage
- **Android ViewModels:** ≥75% line coverage
- **Critical paths (checkout, payment, auth):** 100% coverage

---

## 🚢 Deployment

### Backend Deployment

The backend runs on **Render** (see `backend/render.yaml`), currently at
`bulkbasket-backend.onrender.com`, deployed via Render's native Python
runtime (`gunicorn config.wsgi:application`) rather than a Docker image.
**GitHub Actions** (`.github/workflows/backend-ci.yml`) runs the test suite
against every push/PR touching `backend/**`:

```yaml
# Triggered on push/PR touching backend/**
on:
  push:
    paths: ['backend/**']
  pull_request:
    paths: ['backend/**']

steps:
  - Set up Python & install dependencies
  - Run database migrations (against a CI Postgres service)
  - Run pytest
```

### Android APK Distribution

- **Development builds:** Distributed via Firebase App Distribution
- **Final demo build:** Signed APK provided in the submission

### Environment Variables (Production)

```bash
# Required environment variables (see backend/config/settings/production.py
# and backend/.env.example — DEBUG is hardcoded False in production, not
# env-controlled)
DJANGO_ENV=production
SECRET_KEY=<strong-random-key>
ALLOWED_HOSTS=bulkbasket-backend.onrender.com
DB_NAME=...
DB_USER=...
DB_PASSWORD=...
DB_HOST=...
DB_PORT=6543
REDIS_URL=redis://...
SUPABASE_URL=https://xxxxx.supabase.co
SUPABASE_KEY=...
FCM_SERVER_KEY=...
CORS_ALLOWED_ORIGINS=...
```

---

## 📜 License & Academic Info

### Project Details
| Field | Value |
|-------|-------|
| **Project Name** | BulkBasket |
| **Course** | Mobile Application Development |
| **Academic Year** | 2025/2026 |
| **Project Type** | Group Project (5 members) |
| **Submitted By** | The BulkBasket Project Team |
| **Date** | June 2026 |

### License
This project is developed as part of an academic course requirement. All rights reserved by the BulkBasket Project Team. Not for commercial distribution.

### Acknowledgements
- Built on the shoulders of giants — thanks to the open-source communities behind Kotlin, Django, PostgreSQL, and Supabase
- Special thanks to our course instructors and peer reviewers
- Inspired by the real-world bulk goods trade in Nigerian markets

---

## 📞 Contact & Support

For team-internal questions, reach out via:
- **GitHub Issues** — Bug reports and feature discussions
- **Team Slack/Discord** — Day-to-day communication
- **Weekly Standups** — Every Friday afternoon

For academic queries, contact the project lead.

---

<div align="center">

**Built with ❤️ by the BulkBasket Team**

*From the market, to your kitchen door.*

[⬆ Back to top](#-bulkbasket)

</div>