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
│  │ Celery Tasks │    │   Redis     │    │ Supabase Auth  │      │
│  │ (Async Jobs) │◄──►│  (Cache &   │    │  (JWT Tokens)  │      │
│  │              │    │   Broker)   │    │                │      │
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
   Celery Task              ┌──────────────────────┐
   (FCM notify              │ WebSocket subscribers│
    seller)                 │  • Buyer (track)     │
                            │  • Seller (new order)│
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
| **Supabase Kotlin SDK** | Realtime subscriptions & auth |
| **Firebase Cloud Messaging** | Push notifications |

### Backend (Server)
| Technology | Purpose |
|-----------|---------|
| **Python 3.11+** | Backend programming language |
| **Django 4.2+** | Web framework |
| **Django REST Framework** | REST API framework |
| **Celery** | Async background task queue |
| **Redis** | Cache + Celery message broker |
| **PyJWT** | JWT token verification |
| **python-decouple** | Environment configuration |

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

# Edit .env with your Supabase credentials
# SUPABASE_URL=https://xxxxx.supabase.co
# SUPABASE_KEY=your-anon-key
# SUPABASE_JWT_SECRET=your-jwt-secret
# DATABASE_URL=postgresql://...
# REDIS_URL=redis://localhost:6379

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

# Verify backend is running
curl http://localhost:8000/api/v1/health/
```

### 3. Supabase Setup

1. Create a new project at [supabase.com](https://supabase.com)
2. Navigate to **Settings → API** and copy:
   - Project URL
   - `anon` public key
   - `service_role` secret key
3. Navigate to **Settings → Database** and copy the connection string
4. Run the SQL migrations in `backend/supabase/migrations/`
5. Enable Row Level Security on all tables
6. Add the credentials to your `.env` file

### 4. Android App Setup

```bash
# Open Android Studio
# File → Open → Select the `android/` folder

# Update local.properties with your config
echo "API_BASE_URL=http://10.0.2.2:8000/api/v1/" >> android/local.properties
echo "SUPABASE_URL=https://xxxxx.supabase.co" >> android/local.properties
echo "SUPABASE_KEY=your-anon-key" >> android/local.properties

# Sync Gradle
# Build → Make Project

# Run on emulator or device
# Run → Run 'app'
```

> **Note:** Android emulators use `10.0.2.2` to reach the host machine's localhost. Physical devices need your machine's LAN IP.

### 5. Firebase Cloud Messaging Setup

1. Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
2. Add an Android app with your package name
3. Download `google-services.json` and place in `android/app/`
4. In Firebase Console → Project Settings → Service Accounts, generate a new private key
5. Save the key file as `backend/firebase-credentials.json`

### 6. Verify the Setup

```bash
# Check backend
curl http://localhost:8000/api/v1/health/
# Expected: {"status": "ok"}

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
│   │   ├── migrations/             # SQL migration files
│   │   └── policies/               # RLS policies
│   └── tests/                      # Pytest test suite
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

All endpoints (except `/auth/`) require a `Authorization: Bearer <JWT>` header.

### Authentication

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/auth/signup/` | Register a new user | ❌ |
| `POST` | `/auth/login/` | Log in with email/password | ❌ |
| `POST` | `/auth/refresh/` | Refresh expired JWT | ❌ |
| `POST` | `/auth/logout/` | Invalidate session | ✅ |

**Signup Request:**
```json
POST /api/v1/auth/signup/
{
  "email": "user@example.com",
  "password": "SecurePass123",
  "user_type": "buyer",  // buyer | seller | rider
  "full_name": "Jane Doe",
  "phone": "+2348012345678"
}
```

**Login Response:**
```json
{
  "access_token": "eyJhbGciOi...",
  "refresh_token": "eyJhbGciOi...",
  "expires_in": 3600,
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "user_type": "buyer"
  }
}
```

### Products

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/products/` | List all products (paginated) | ✅ |
| `GET` | `/products/<id>/` | Get product details | ✅ |
| `GET` | `/products/search/?q=rice` | Search products | ✅ |
| `POST` | `/products/` | Create product (seller only) | ✅ |
| `PATCH` | `/products/<id>/` | Update product (seller only) | ✅ |
| `DELETE` | `/products/<id>/` | Delete product (seller only) | ✅ |

### Sellers

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/sellers/nearby/?lat=&lng=` | List nearby sellers | ✅ |
| `GET` | `/sellers/<id>/` | Get seller profile | ✅ |
| `GET` | `/sellers/<id>/products/` | List seller's products | ✅ |
| `PATCH` | `/sellers/me/` | Update own profile | ✅ |

### Orders

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/orders/` | Place a new order (buyer) | ✅ |
| `GET` | `/orders/` | List my orders | ✅ |
| `GET` | `/orders/<id>/` | Get order details | ✅ |
| `PATCH` | `/orders/<id>/accept/` | Accept order (seller) | ✅ |
| `PATCH` | `/orders/<id>/reject/` | Reject order (seller) | ✅ |
| `PATCH` | `/orders/<id>/ready/` | Mark ready for pickup | ✅ |

**Place Order Request:**
```json
POST /api/v1/orders/
{
  "seller_id": "uuid",
  "delivery_address": {
    "street": "14 Akin Street",
    "city": "Lagos",
    "lat": 6.4541,
    "lng": 3.3947
  },
  "items": [
    { "product_id": "uuid", "quantity": 2 },
    { "product_id": "uuid", "quantity": 1 }
  ],
  "notes": "Please call on arrival"
}
```

### Delivery

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/delivery/jobs/` | Available jobs (rider) | ✅ |
| `POST` | `/delivery/jobs/<id>/accept/` | Accept delivery job | ✅ |
| `PATCH` | `/delivery/<id>/picked-up/` | Mark picked up | ✅ |
| `PATCH` | `/delivery/<id>/delivered/` | Mark delivered | ✅ |
| `GET` | `/delivery/earnings/` | Earnings summary | ✅ |

### Error Response Format

All errors follow this schema:

```json
{
  "error": {
    "code": "PRODUCT_NOT_FOUND",
    "message": "Product with id 'xyz' does not exist",
    "details": {}
  }
}
```

**Common HTTP Status Codes:**
- `200` — Success
- `201` — Resource created
- `400` — Bad request (validation error)
- `401` — Unauthorized (missing/invalid JWT)
- `403` — Forbidden (insufficient permissions)
- `404` — Not found
- `429` — Rate limited
- `500` — Server error

> 📌 **Full Postman collection:** `docs/postman/BulkBasket.postman_collection.json`

---

## 🗄 Database Schema

### Entity Relationship Diagram

```
┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│    users     │         │   sellers    │         │   products   │
├──────────────┤         ├──────────────┤         ├──────────────┤
│ id (PK)      │◄────────┤ user_id (FK) │◄────────┤ seller_id    │
│ email        │         │ business_name│         │ name         │
│ user_type    │         │ address      │         │ price        │
│ full_name    │         │ lat, lng     │         │ unit         │
│ phone        │         │ rating       │         │ stock_qty    │
│ created_at   │         │ verified     │         │ category     │
└──────┬───────┘         └──────────────┘         │ image_url    │
       │                                          └──────┬───────┘
       │                                                 │
       │         ┌──────────────┐                        │
       │         │   orders     │                        │
       │         ├──────────────┤                        │
       └────────►│ buyer_id (FK)│                        │
                 │ seller_id    │                        │
                 │ status       │      ┌──────────────┐  │
                 │ total_amount │      │ order_items  │  │
                 │ delivery_addr├─────►├──────────────┤  │
                 │ created_at   │      │ order_id (FK)│  │
                 └──────┬───────┘      │ product_id   │──┘
                        │              │ quantity     │
                        │              │ price_at_buy │
                        ▼              └──────────────┘
                 ┌──────────────┐
                 │  deliveries  │
                 ├──────────────┤
                 │ order_id (FK)│
                 │ rider_id (FK)│
                 │ status       │
                 │ pickup_time  │
                 │ delivered_at │
                 └──────────────┘
```

### Core Tables

#### `users`
| Column | Type | Description |
|--------|------|-------------|
| `id` | UUID (PK) | Unique user identifier (matches Supabase Auth UID) |
| `email` | VARCHAR | User email (unique) |
| `user_type` | ENUM | `buyer`, `seller`, `rider`, `admin` |
| `full_name` | VARCHAR | Display name |
| `phone` | VARCHAR | Phone number |
| `created_at` | TIMESTAMP | Account creation timestamp |

#### `sellers`
| Column | Type | Description |
|--------|------|-------------|
| `id` | UUID (PK) | Seller profile ID |
| `user_id` | UUID (FK) | References `users.id` |
| `business_name` | VARCHAR | Business display name |
| `address` | TEXT | Business location |
| `lat`, `lng` | DECIMAL | GPS coordinates |
| `rating` | DECIMAL | Average rating (0-5) |
| `verified` | BOOLEAN | Admin-verified status |

#### `products`
| Column | Type | Description |
|--------|------|-------------|
| `id` | UUID (PK) | Product ID |
| `seller_id` | UUID (FK) | References `sellers.id` |
| `name` | VARCHAR | Product name |
| `price` | DECIMAL | Bulk price |
| `unit` | VARCHAR | e.g., "50kg bag", "½ crate" |
| `stock_qty` | INTEGER | Available stock |
| `category` | VARCHAR | e.g., "grains", "produce" |
| `image_url` | TEXT | Supabase Storage URL |

#### `orders`
| Column | Type | Description |
|--------|------|-------------|
| `id` | UUID (PK) | Order ID |
| `buyer_id` | UUID (FK) | References `users.id` |
| `seller_id` | UUID (FK) | References `sellers.id` |
| `status` | ENUM | `pending`, `accepted`, `preparing`, `ready`, `dispatched`, `delivered`, `cancelled` |
| `total_amount` | DECIMAL | Total order amount |
| `delivery_address` | JSONB | Full address object |
| `created_at` | TIMESTAMP | Order placement time |

#### `order_items`
| Column | Type | Description |
|--------|------|-------------|
| `id` | UUID (PK) | Line item ID |
| `order_id` | UUID (FK) | References `orders.id` |
| `product_id` | UUID (FK) | References `products.id` |
| `quantity` | INTEGER | Number of units |
| `price_at_purchase` | DECIMAL | Snapshot of price at order time |

#### `deliveries`
| Column | Type | Description |
|--------|------|-------------|
| `id` | UUID (PK) | Delivery ID |
| `order_id` | UUID (FK) | References `orders.id` |
| `rider_id` | UUID (FK) | References `users.id` |
| `status` | ENUM | `assigned`, `picked_up`, `in_transit`, `delivered` |
| `pickup_time` | TIMESTAMP | When rider picked up goods |
| `delivered_at` | TIMESTAMP | When delivery completed |

### Row Level Security (RLS) Policies

```sql
-- Users can only see their own data
CREATE POLICY users_select_own ON users
    FOR SELECT USING (auth.uid() = id);

-- Buyers can see all active products
CREATE POLICY products_select_active ON products
    FOR SELECT USING (stock_qty > 0);

-- Sellers can only modify their own products
CREATE POLICY products_modify_own ON products
    FOR UPDATE USING (seller_id IN (
        SELECT id FROM sellers WHERE user_id = auth.uid()
    ));

-- Buyers see only their own orders, sellers see orders for them
CREATE POLICY orders_select_relevant ON orders
    FOR SELECT USING (
        buyer_id = auth.uid() OR
        seller_id IN (SELECT id FROM sellers WHERE user_id = auth.uid())
    );
```

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
**Owner:** Lead + DevOps | **Status:** ⚪ Not Started

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
**Owner:** Android Devs | **Status:** ⚪ Not Started

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
docker-compose exec backend pytest apps/orders/tests/test_views.py

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

The backend is deployed via **GitHub Actions** to a cloud host (Render / Fly.io / Railway):

```yaml
# Triggered on push to main branch
on:
  push:
    branches: [main]

steps:
  - Run tests
  - Build Docker image
  - Push to container registry
  - Deploy to cloud host
  - Run database migrations
  - Health check
```

### Android APK Distribution

- **Development builds:** Distributed via Firebase App Distribution
- **Final demo build:** Signed APK provided in the submission

### Environment Variables (Production)

```bash
# Required environment variables
DJANGO_SECRET_KEY=<strong-random-key>
DJANGO_DEBUG=False
DJANGO_ALLOWED_HOSTS=api.bulkbasket.com
DATABASE_URL=postgresql://...
SUPABASE_URL=https://xxxxx.supabase.co
SUPABASE_KEY=...
SUPABASE_JWT_SECRET=...
REDIS_URL=redis://...
CELERY_BROKER_URL=redis://...
FCM_CREDENTIALS_PATH=/app/firebase-credentials.json
SENTRY_DSN=https://...
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