# BulkBasket Repository Structure

A complete guide to the file and folder organization of the BulkBasket project. Use this document to navigate the codebase, understand where to place new files, and onboard new team members quickly.

---

## 📋 Table of Contents

1. [Top-Level Overview](#-top-level-overview)
2. [Root Files Explained](#-root-files-explained)
3. [Backend Structure](#-backend-structure)
4. [Android Structure](#-android-structure)
5. [Documentation Structure](#-documentation-structure)
6. [Configuration Files](#-configuration-files)
7. [Scripts & Tools](#-scripts--tools)
8. [Where Things Go](#-where-things-go)
9. [Naming Conventions](#-naming-conventions)
10. [Adding New Files](#-adding-new-files)

---

## 🗂 Top-Level Overview

```
bulkbasket/
│
├── 📄 README.md                    ← Project overview & getting started
├── 📄 CONTRIBUTING.md              ← Development workflow & standards
├── 📄 CHANGELOG.md                 ← Version history
├── 📄 API.md                       ← API documentation
├── 📄 Design.md                    ← Design system specification
├── 📄 STRUCTURE.md                 ← This file
├── 📄 SECURITY.md                  ← Security policy & vulnerability reporting
├── 📄 LICENSE                      ← License terms
├── 📄 .gitignore                   ← Files Git should ignore
├── 📄 .editorconfig                ← Editor formatting rules
├── 📄 docker-compose.yml           ← Local dev environment
│
├── 📁 .github/                     ← GitHub-specific config
│   ├── workflows/                  ← CI/CD pipelines
│   ├── ISSUE_TEMPLATE/             ← Issue templates
│   └── PULL_REQUEST_TEMPLATE.md    ← PR template
│
├── 📁 backend/                     ← Django backend (Python)
├── 📁 android/                     ← Android app (Kotlin)
├── 📁 docs/                        ← Detailed documentation
├── 📁 design/                      ← Design assets (Figma exports)
└── 📁 scripts/                     ← Utility scripts
```

### Why This Structure?

- **Monorepo approach** — keeps backend and Android code together for atomic commits and easier coordination across the 5-person team
- **Clear separation** — each top-level folder has a single responsibility
- **Documentation at root** — important docs are visible immediately on GitHub
- **Hidden configs** — dotfiles (`.github/`, `.gitignore`) follow industry standards

---

## 📄 Root Files Explained

### Markdown Documents

| File | Purpose | Who Maintains It |
|------|---------|------------------|
| `README.md` | Main project overview, getting started, links to other docs | Project Lead |
| `CONTRIBUTING.md` | How to contribute, code standards, workflow | Project Lead |
| `CHANGELOG.md` | Track of all changes per version | Project Lead |
| `API.md` | Complete REST API documentation | Backend Engineer |
| `Design.md` | Design system, components, tokens | UI/UX Designer |
| `STRUCTURE.md` | This file — explains repo structure | Project Lead |
| `SECURITY.md` | Security policy, supported versions, how to report a vulnerability | Project Lead |

### Configuration Files

| File | Purpose |
|------|---------|
| `LICENSE` | Defines usage rights (academic project) |
| `.gitignore` | Lists files/folders Git should not track |
| `.editorconfig` | Standardizes editor settings across team |
| `docker-compose.yml` | Orchestrates local dev services (backend, Redis, DB) |

---

## 🐍 Backend Structure

The Python/Django backend lives in `/backend/`.

```
backend/
│
├── 📄 manage.py                    ← Django management commands
├── 📄 Dockerfile                   ← Backend container definition
├── 📄 Procfile                     ← Process types for deployment (Render/Heroku-style)
├── 📄 render.yaml                  ← Render.com deployment config
├── 📄 runtime.txt                  ← Python runtime version pin
├── 📄 requirements.txt             ← Python dependencies (production)
├── 📄 requirements-dev.txt         ← Dev dependencies (testing, linting)
├── 📄 .env.example                 ← Environment variables template
├── 📄 pytest.ini                   ← Pytest configuration
├── 📄 bulkbasket-firebase-adminsdk.json ← Firebase Admin SDK credentials (gitignored)
│
├── 📁 config/                      ← Django project configuration
│   ├── __init__.py
│   ├── settings/                   ← Split settings by environment
│   │   ├── __init__.py
│   │   ├── base.py                 ← Shared settings
│   │   ├── development.py          ← Dev overrides
│   │   ├── staging.py              ← Staging overrides
│   │   └── production.py           ← Production overrides
│   ├── urls.py                     ← Root URL routing
│   ├── wsgi.py                     ← WSGI entry point
│   ├── asgi.py                     ← ASGI entry point (for async)
│   └── celery.py                   ← Celery configuration
│
├── 📁 apps/                        ← Django apps (one per domain)
│   │
│   ├── 📁 users/                   ← User management
│   │   ├── __init__.py
│   │   ├── apps.py
│   │   ├── models.py               ← User, Address models
│   │   ├── serializers.py          ← DRF serializers
│   │   ├── views.py                ← API views
│   │   ├── urls.py                 ← App-level URLs
│   │   ├── services.py             ← Business logic
│   │   ├── admin.py                ← Django admin config
│   │   ├── migrations/             ← DB migrations
│   │   └── tests/                  ← App-specific tests
│   │       ├── __init__.py
│   │       └── test_models.py
│   │
│   ├── 📁 products/                ← Product catalog
│   │   ├── models.py               ← Product, Category models
│   │   ├── serializers.py
│   │   ├── views.py
│   │   ├── urls.py
│   │   ├── filters.py              ← Query filters
│   │   ├── admin.py
│   │   ├── migrations/
│   │   └── tests/
│   │       ├── __init__.py
│   │       ├── test_models.py
│   │       └── test_views.py
│   │
│   ├── 📁 sellers/                 ← Seller profiles
│   │   ├── models.py               ← Seller model
│   │   ├── serializers.py
│   │   ├── views.py
│   │   ├── urls.py
│   │   ├── services.py             ← Nearby search logic
│   │   ├── migrations/             ← 0001–0003 (ratings, timestamps added)
│   │   └── tests.py                ← Still a flat file (not yet split into a package)
│   │
│   ├── 📁 orders/                  ← Order processing
│   │   ├── models.py               ← Order, OrderItem models
│   │   ├── serializers.py
│   │   ├── views.py
│   │   ├── urls.py
│   │   ├── state_machine.py        ← Order status transitions
│   │   ├── services.py             ← Order creation, total calculation
│   │   ├── migrations/
│   │   │   ├── 0001_initial.py
│   │   │   └── 0002_orderitem_product_protect.py  ← PROTECT FK on OrderItem.product
│   │   └── tests/
│   │       ├── __init__.py
│   │       ├── test_state_machine.py
│   │       └── test_services.py
│   │
│   ├── 📁 delivery/                ← Dispatch & tracking
│   │   ├── models.py               ← Delivery model
│   │   ├── serializers.py
│   │   ├── views.py
│   │   ├── urls.py
│   │   ├── services.py             ← Rider assignment logic
│   │   ├── admin.py
│   │   ├── migrations/
│   │   └── tests/
│   │       ├── __init__.py
│   │       └── test_services.py
│   │
│   ├── 📁 notifications/           ← FCM integration
│   │   ├── models.py               ← Device, Notification models
│   │   ├── serializers.py
│   │   ├── views.py
│   │   ├── urls.py
│   │   ├── fcm.py                  ← FCM client wrapper
│   │   ├── services.py
│   │   ├── admin.py
│   │   ├── migrations/
│   │   └── tests.py                ← Still a flat file (not yet split into a package)
│   │
│   └── 📁 common/                  ← Shared utilities
│       ├── __init__.py
│       ├── exceptions.py           ← Custom exceptions
│       ├── responses.py            ← Standardized response helpers
│       ├── pagination.py           ← Custom pagination classes
│       ├── permissions.py          ← Base permission classes
│       └── tests/
│           ├── __init__.py
│           └── test_exceptions.py
│
└── 📁 supabase/                    ← Supabase configuration
    └── policies/                   ← Row Level Security policies
        └── orders_rls.sql
```

> **Note:** No app currently has a `tasks.py`/Celery task file, `middleware.py`, or `utils.py` — `config/celery.py` exists for future use but nothing has been wired to it yet. There is also no top-level `backend/tests/` (integration tests) or `backend/static/` directory yet.

### Backend Folder Purposes

| Folder | Purpose |
|--------|---------|
| `config/` | Django settings split by environment (dev, staging, prod) |
| `apps/` | Each business domain is its own Django app |
| `apps/<app>/models.py` | Database models for that domain |
| `apps/<app>/serializers.py` | Request/response data validation |
| `apps/<app>/views.py` | API endpoint handlers |
| `apps/<app>/services.py` | Business logic (kept out of views) |
| `apps/<app>/tasks.py` | Async background jobs (Celery) — convention only; no app has adopted this yet |
| `apps/<app>/tests/` | Unit tests for that app (some apps still use a flat `tests.py`; see tree above) |
| `supabase/` | Supabase-specific SQL (currently just RLS policies) |

### Django App Pattern

Each app follows roughly the **same structure** for consistency (using `orders/` as the fullest example):

```
apps/orders/
├── models.py         ← What data looks like
├── serializers.py    ← How data enters/leaves the API
├── views.py          ← HTTP endpoint handlers (thin)
├── services.py       ← Business logic (fat)
├── urls.py           ← URL routing
├── state_machine.py  ← Order status transitions
├── admin.py          ← Django admin config
├── migrations/       ← DB migrations
└── tests/            ← Tests mirror the structure above
```

> Not every app has every file — e.g. `permissions.py` and `tasks.py` are conventions defined but not yet used by any app; `sellers/` and `notifications/` still keep tests in a single flat `tests.py` instead of a `tests/` package.

> **Rule of thumb:** Views should be thin. Move logic into `services.py`.

---

## 📱 Android Structure

The Kotlin Android app lives in `/android/`.

```
android/
│
├── 📄 build.gradle.kts             ← Root Gradle config
├── 📄 settings.gradle.kts          ← Module declarations
├── 📄 gradle.properties            ← Gradle properties
├── 📄 local.properties             ← Local config (gitignored)
├── 📄 gradlew / gradlew.bat        ← Gradle wrapper scripts
│
├── 📁 gradle/                      ← Gradle wrapper & version catalog
│   ├── libs.versions.toml          ← Centralized dependency versions
│   ├── gradle-daemon-jvm.properties
│   └── wrapper/
│
├── 📁 app/                         ← Main Android module
│   ├── 📄 build.gradle.kts         ← App-level Gradle
│   ├── 📄 proguard-rules.pro       ← R8/ProGuard rules
│   ├── 📄 google-services.json     ← Firebase config (gitignored)
│   │
│   └── 📁 src/
│       ├── 📁 main/
│       │   ├── 📄 AndroidManifest.xml
│       │   │
│       │   ├── 📁 java/com/bulkbasket/
│       │   │   │
│       │   │   ├── 📁 app/                ← Application setup
│       │   │   │   ├── BulkBasketApp.kt   ← @HiltAndroidApp
│       │   │   │   └── MainActivity.kt
│       │   │   │
│       │   │   ├── 📁 di/                 ← Hilt modules
│       │   │   │   ├── AppModule.kt
│       │   │   │   ├── DatabaseModule.kt
│       │   │   │   ├── NetworkModule.kt
│       │   │   │   └── RepositoryModule.kt
│       │   │   │
│       │   │   ├── 📁 data/               ← Data layer
│       │   │   │   ├── 📁 remote/         ← Network/API
│       │   │   │   │   ├── api/           ← Retrofit interfaces
│       │   │   │   │   │   ├── AuthApi.kt
│       │   │   │   │   │   ├── DeliveryApi.kt
│       │   │   │   │   │   ├── NotificationsApi.kt
│       │   │   │   │   │   ├── OrdersApi.kt
│       │   │   │   │   │   ├── ProductsApi.kt
│       │   │   │   │   │   └── SellersApi.kt
│       │   │   │   │   ├── dto/           ← Data Transfer Objects
│       │   │   │   │   │   ├── DeliveryDtos.kt
│       │   │   │   │   │   ├── NotificationDtos.kt
│       │   │   │   │   │   ├── OrderDtos.kt
│       │   │   │   │   │   ├── ProductDtos.kt
│       │   │   │   │   │   ├── SellerDtos.kt
│       │   │   │   │   │   └── UserDtos.kt
│       │   │   │   │   └── interceptors/  ← OkHttp interceptors
│       │   │   │   │       ├── AuthInterceptor.kt
│       │   │   │   │       └── TokenAuthenticator.kt   ← Refreshes/retries on 401
│       │   │   │   │
│       │   │   │   ├── 📁 local/          ← Room database
│       │   │   │   │   └── BulkBasketDatabase.kt   ← Scaffolded only; no DAOs/entities/converters yet
│       │   │   │   │
│       │   │   │   ├── 📁 repository/     ← Repositories (single source)
│       │   │   │   │   ├── AuthRepository.kt
│       │   │   │   │   ├── DeliveryRepository.kt
│       │   │   │   │   ├── NotificationRepository.kt
│       │   │   │   │   ├── OrderRepository.kt
│       │   │   │   │   └── ProductRepository.kt
│       │   │   │   │
│       │   │   │   └── 📁 mappers/        ← DTO ↔ Domain mappers
│       │   │   │       ├── AddressMapper.kt
│       │   │   │       ├── CategoryMapper.kt
│       │   │   │       ├── DeliveryMapper.kt
│       │   │   │       ├── NotificationMapper.kt
│       │   │   │       ├── OrderMapper.kt
│       │   │   │       ├── ProductMapper.kt
│       │   │   │       ├── RiderMapper.kt
│       │   │   │       ├── SellerMapper.kt
│       │   │   │       └── UserMapper.kt
│       │   │   │
│       │   │   ├── 📁 domain/             ← Domain layer
│       │   │   │   ├── 📁 model/          ← Domain models
│       │   │   │   │   ├── Address.kt
│       │   │   │   │   ├── CartItem.kt
│       │   │   │   │   ├── Category.kt
│       │   │   │   │   ├── Delivery.kt
│       │   │   │   │   ├── Notification.kt
│       │   │   │   │   ├── Order.kt
│       │   │   │   │   ├── Product.kt
│       │   │   │   │   ├── RiderProfile.kt
│       │   │   │   │   ├── Seller.kt
│       │   │   │   │   └── User.kt
│       │   │   │   │
│       │   │   │   └── 📁 repository/     ← Repository interfaces
│       │   │   │       ├── IAuthRepository.kt
│       │   │   │       ├── IDeliveryRepository.kt
│       │   │   │       ├── INotificationRepository.kt
│       │   │   │       ├── IOrderRepository.kt
│       │   │   │       └── IProductRepository.kt
│       │   │   │       (no domain/usecase/ yet — business logic still lives in repositories/ViewModels)
│       │   │   │
│       │   │   ├── 📁 ui/                 ← UI layer (Compose)
│       │   │   │   │
│       │   │   │   ├── 📁 theme/          ← Design system
│       │   │   │   │   ├── Colors.kt
│       │   │   │   │   ├── Dimensions.kt
│       │   │   │   │   ├── Fonts.kt
│       │   │   │   │   ├── Theme.kt
│       │   │   │   │   ├── ThemeViewModel.kt   ← Light/dark toggle state
│       │   │   │   │   ├── Type.kt
│       │   │   │   │   └── Typography.kt
│       │   │   │   │
│       │   │   │   ├── 📁 common/         ← Shared composables
│       │   │   │   │   ├── components/    ← Reusable components
│       │   │   │   │   │   ├── BulkBasketCard.kt
│       │   │   │   │   │   ├── CurvedTopAppBar.kt
│       │   │   │   │   │   └── LoadingScreen.kt
│       │   │   │   │   └── navigation/    ← App navigation
│       │   │   │   │       ├── BulkBasketNavHost.kt
│       │   │   │   │       └── Routes.kt
│       │   │   │   │
│       │   │   │   ├── 📁 auth/           ← Authentication flow
│       │   │   │   │   ├── login/
│       │   │   │   │   │   ├── LoginScreen.kt
│       │   │   │   │   │   └── LoginViewModel.kt
│       │   │   │   │   └── signup/
│       │   │   │   │       ├── SignupScreen.kt
│       │   │   │   │       └── SignupViewModel.kt
│       │   │   │   │
│       │   │   │   ├── 📁 splash/         ← Splash flow (sibling of auth/, not nested under it)
│       │   │   │   │   ├── SplashScreen.kt
│       │   │   │   │   └── SplashViewModel.kt
│       │   │   │   │
│       │   │   │   ├── 📁 buyer/          ← Buyer-specific screens
│       │   │   │   │   ├── BuyerShellScreen.kt    ← Bottom-nav host Scaffold for the buyer role
│       │   │   │   │   ├── home/
│       │   │   │   │   │   ├── HomeScreen.kt
│       │   │   │   │   │   └── HomeViewModel.kt
│       │   │   │   │   ├── search/
│       │   │   │   │   │   └── BrowseScreen.kt
│       │   │   │   │   ├── category/            ← Category browsing (new)
│       │   │   │   │   │   ├── CategoryEmojis.kt
│       │   │   │   │   │   ├── CategoryScreen.kt
│       │   │   │   │   │   └── CategoryViewModel.kt
│       │   │   │   │   ├── sellerdetail/
│       │   │   │   │   │   ├── SellerDetailScreen.kt
│       │   │   │   │   │   └── SellerDetailViewModel.kt
│       │   │   │   │   ├── productdetail/       ← Product detail (new)
│       │   │   │   │   │   ├── ProductDetailScreen.kt
│       │   │   │   │   │   └── ProductDetailViewModel.kt
│       │   │   │   │   ├── cart/
│       │   │   │   │   │   ├── CartScreen.kt
│       │   │   │   │   │   └── CartViewModel.kt
│       │   │   │   │   ├── checkout/
│       │   │   │   │   │   ├── CheckoutScreen.kt
│       │   │   │   │   │   └── CheckoutViewModel.kt
│       │   │   │   │   ├── orders/
│       │   │   │   │   │   ├── BuyerOrdersScreen.kt
│       │   │   │   │   │   └── BuyerOrdersViewModel.kt
│       │   │   │   │   ├── profile/             ← Buyer profile (new)
│       │   │   │   │   │   ├── ProfileScreen.kt
│       │   │   │   │   │   └── ProfileViewModel.kt
│       │   │   │   │   └── common/              ← Buyer-scoped shared composables
│       │   │   │   │       ├── BuyerBottomNav.kt
│       │   │   │   │       ├── ProductCard.kt
│       │   │   │   │       └── SellerCard.kt
│       │   │   │   │
│       │   │   │   ├── 📁 seller/         ← Seller-specific screens
│       │   │   │   │   ├── SellerShellScreen.kt   ← Bottom-nav host Scaffold for the seller role
│       │   │   │   │   ├── dashboard/
│       │   │   │   │   │   ├── SellerDashboardScreen.kt
│       │   │   │   │   │   └── SellerDashboardViewModel.kt
│       │   │   │   │   ├── inventory/
│       │   │   │   │   │   ├── InventoryScreen.kt
│       │   │   │   │   │   └── InventoryViewModel.kt
│       │   │   │   │   ├── orders/
│       │   │   │   │   │   └── SellerOrdersScreen.kt    ← No dedicated ViewModel yet
│       │   │   │   │   ├── profile/               ← Seller profile (new)
│       │   │   │   │   │   ├── SellerProfileScreen.kt
│       │   │   │   │   │   └── SellerProfileViewModel.kt
│       │   │   │   │   └── common/                ← Seller-scoped shared composables
│       │   │   │   │       ├── OrderCard.kt
│       │   │   │   │       ├── OrderStatusChip.kt
│       │   │   │   │       └── SellerBottomNav.kt
│       │   │   │   │
│       │   │   │   ├── 📁 rider/          ← Rider-specific screens
│       │   │   │   │   ├── jobs/
│       │   │   │   │   │   ├── RiderJobsScreen.kt
│       │   │   │   │   │   └── RiderJobsViewModel.kt
│       │   │   │   │   ├── activedelivery/
│       │   │   │   │   │   ├── ActiveDeliveryScreen.kt
│       │   │   │   │   │   └── ActiveDeliveryViewModel.kt
│       │   │   │   │   ├── profile/               ← Replaces the old "earnings/" concept
│       │   │   │   │   │   ├── RiderProfileScreen.kt
│       │   │   │   │   │   └── RiderProfileViewModel.kt
│       │   │   │   │   └── common/
│       │   │   │   │       └── DeliveryCard.kt
│       │   │   │   │
│       │   │   │   └── 📁 shared/         ← Screens shared across roles (new top-level ui category)
│       │   │   │       └── notifications/
│       │   │   │           ├── NotificationsScreen.kt
│       │   │   │           └── NotificationsViewModel.kt
│       │   │   │
│       │   │   ├── 📁 utils/              ← Utilities
│       │   │   │   ├── Constants.kt
│       │   │   │   ├── NetworkResult.kt    ← Sealed class for results
│       │   │   │   └── PreferencesManager.kt
│       │   │   │
│       │   │   └── 📁 service/            ← Background services
│       │   │       └── FcmService.kt       ← Push notifications (no LocationService.kt yet)
│       │   │
│       │   └── 📁 res/                    ← Android resources
│       │       ├── drawable/               ← Vector drawables
│       │       ├── values/                 ← Resource values (colors, strings, themes, etc.)
│       │       ├── xml/                    ← Backup/data-extraction rules
│       │       └── mipmap-*/               ← App icons (various densities; no values-night/ or font/ yet)
│       │
│       ├── 📁 test/                       ← Unit tests (directory scaffolded, currently empty)
│       │
│       └── 📁 androidTest/                ← Instrumented tests (directory scaffolded, currently empty)
```

> There is no `android/design/` folder — design assets reference lives only at the repo root (`/design/`, see Top-Level Overview).

### Android Layer Architecture

The Android app follows **Clean Architecture** with three layers:

```
┌───────────────────────────────────────┐
│         UI LAYER                      │
│  Screens + ViewModels (Compose)       │
│  Knows: Domain models, ViewModels     │
└──────────────┬────────────────────────┘
               │
               ▼
┌───────────────────────────────────────┐
│       DOMAIN LAYER                    │
│  Use Cases + Domain Models            │
│  Knows: Repository interfaces         │
│  Pure Kotlin — no Android imports     │
└──────────────┬────────────────────────┘
               │
               ▼
┌───────────────────────────────────────┐
│        DATA LAYER                     │
│  Repository Impls + Remote + Local    │
│  Knows: Retrofit, Room, DTOs          │
└───────────────────────────────────────┘
```

### Android Folder Purposes

| Folder | Purpose |
|--------|---------|
| `app/` | Application setup, MainActivity, DI initialization |
| `di/` | Hilt modules — dependency injection configuration |
| `data/` | Data layer: API, database, repositories |
| `data/remote/` | Network calls (Retrofit, DTOs, interceptors) |
| `data/local/` | Room database — currently just the `BulkBasketDatabase.kt` scaffold; no DAOs/entities/converters yet |
| `data/repository/` | Repository implementations |
| `data/mappers/` | Convert between DTOs ↔ Domain models |
| `domain/` | Pure business logic, no Android dependencies |
| `domain/model/` | Domain models used throughout the app |
| `domain/repository/` | Repository interfaces (implementations in data/) — no `domain/usecase/` layer yet |
| `ui/` | Compose screens, ViewModels, theme |
| `ui/theme/` | Design system: colors, typography, dimensions, fonts |
| `ui/common/` | Reusable UI components and navigation shared across screens |
| `ui/auth/` | Login/signup screens |
| `ui/splash/` | Splash screen (sibling of `ui/auth/`, not nested inside it) |
| `ui/buyer/` | Buyer app screens |
| `ui/seller/` | Seller app screens |
| `ui/rider/` | Rider app screens |
| `ui/shared/` | Screens shared across roles (e.g. notifications) |
| `utils/` | Helper classes (constants, network result wrapper, preferences) |
| `service/` | Android services (currently just FCM) |
| `res/` | Android resources (drawables, values, xml, mipmaps) |

### Screen Pattern

Each screen generally follows the **same structure** (state is often kept inline in the ViewModel rather than split into separate files):

```
ui/buyer/cart/
├── CartScreen.kt        ← @Composable function
├── CartViewModel.kt     ← Hilt @ViewModel
├── CartState.kt         ← UI state data class (optional — often inlined in the ViewModel)
└── CartEvent.kt         ← User events (optional)
```

> **Rule:** ViewModels never reference Android classes directly. They expose `StateFlow<UiState>` for the UI to observe.

---

## 📚 Documentation Structure

Detailed documentation lives in `/docs/`. In practice this folder is much flatter today than the aspirational layout below might suggest — it currently holds just three files:

```
docs/
│
├── 📄 API.md            ← API reference (mirrors/expands on the root API.md)
├── 📄 CONTRIBUTING.md   ← Contribution guidelines
└── 📄 SETUP.md          ← Environment & local setup instructions
```

The subdivided structure (`architecture/`, `api/`, `database/`, `deployment/`, `development/`, `design/`, `team/`, `academic/`, `user-guides/`, etc.) described in earlier drafts of this document has not been built out — treat it as a future target, not current state, until those folders actually exist.

---

## ⚙ Configuration Files

### `.github/` — GitHub Configuration

```
.github/
│
├── 📁 workflows/                    ← GitHub Actions CI/CD
│   ├── backend-ci.yml               ← Run on backend PRs
│   └── android-ci.yml               ← Run on Android PRs
│
└── 📁 ISSUE_TEMPLATE/               ← Issue templates
    ├── bug_report.md
    └── feature_request.md
```

> `deploy-staging.yml`/`deploy-production.yml` workflows, `PULL_REQUEST_TEMPLATE.md`, `CODEOWNERS`, and `dependabot.yml` don't exist yet — CI currently only runs backend and Android checks on PRs.

### Root Configuration Files

```
bulkbasket/
│
├── 📄 .gitignore                   ← What Git ignores
├── 📄 .editorconfig                ← Editor settings standardization
└── 📄 docker-compose.yml           ← Local dev orchestration
```

> `.pre-commit-config.yaml`, `docker-compose.test.yml`, a root-level `.env.example`, and `.nvmrc` don't exist in the repo today (there is a `.env.example` under `backend/`, documented in the Backend Structure section).

### Sample `.gitignore`

Critical patterns to ignore:

```gitignore
# Python
__pycache__/
*.py[cod]
venv/
.env
*.sqlite3

# Android
*.apk
*.iml
local.properties
.gradle/
build/
.idea/
google-services.json   # contains secrets

# Node
node_modules/
.next/

# IDE
.vscode/
.DS_Store

# Secrets
*.pem
*.key
firebase-credentials.json

# Logs
*.log
logs/
```

---

## 🛠 Scripts & Tools

Utility scripts live in `/scripts/`.

```
scripts/
│
├── 📁 dev/                          ← Development scripts
│   └── start.ps1                    ← PowerShell dev startup script (Windows-first tooling)
│
├── 📁 deploy/                       ← Deployment scripts
│   └── README.md                    ← Placeholder — no deploy scripts committed yet
│
└── 📁 admin/                        ← Admin/ops scripts
    └── create_test_data.py          ← Seeds sample data for local testing
```

> The scripts tree is much smaller than earlier drafts implied — there's no top-level `scripts/README.md`, and none of `reset-db.sh`, `seed-db.py`, `run-tests.sh`, `lint-all.sh`, `deploy-backend.sh`, `build-android-release.sh`, `upload-apk.sh`, `backup-db.sh`, `create-superuser.py`, or `send-test-notification.py` exist. `dev/` scripts are PowerShell (`.ps1`), not bash, matching the Windows-based dev setup.

---

## 🗺 Where Things Go

When adding new code, ask: **"What kind of file is this?"**

### Backend Decision Tree

```
Adding a new endpoint?
  ↓
  Is it a new business domain (e.g., "reviews")?
    → Create a new app: apps/reviews/
  Is it for an existing domain?
    → Add to existing app: apps/orders/views.py
       ↓
       Where do the pieces go?
       - Data structure → models.py
       - JSON shape → serializers.py
       - URL → urls.py
       - Endpoint logic → views.py
       - Complex business logic → services.py
       - Background job → tasks.py
       - Authorization rule → permissions.py
```

### Android Decision Tree

```
Adding a new screen?
  ↓
  Which user type is it for?
    → ui/buyer/, ui/seller/, ui/rider/, or ui/common/
       ↓
       Create folder: ui/buyer/wishlist/
       Add files:
       - WishlistScreen.kt
       - WishlistViewModel.kt
       - WishlistState.kt
       ↓
       Need data?
       - Add API method → data/remote/api/
       - Add local cache → data/local/dao/
       - Add repository method → data/repository/
       - Add use case (optional) → domain/usecase/
```

### Specific Examples

| What you're adding | Where it goes |
|-------------------|---------------|
| New API endpoint | `backend/apps/<domain>/views.py` |
| New DB table | `backend/apps/<domain>/models.py` + migration |
| New Android screen | `android/app/src/main/java/com/bulkbasket/ui/<user>/<feature>/` |
| New Composable component | `android/.../ui/common/components/` |
| Color or design token | `android/.../ui/theme/Colors.kt` + `Design.md` |
| API documentation | `API.md` (top-level) |
| User-facing guide | `docs/user-guides/` |
| Architecture decision | `docs/architecture/` |
| Test for a backend endpoint | `backend/apps/<domain>/tests/test_views.py` |
| Test for a ViewModel | `android/app/src/test/.../viewmodels/` |
| UI test (Espresso) | `android/app/src/androidTest/.../ui/` |
| Helper utility (backend) | `backend/apps/common/utils.py` |
| Helper utility (Android) | `android/.../utils/` |
| CI/CD workflow | `.github/workflows/` |
| Build script | `scripts/deploy/` or `scripts/dev/` |

---

## 📝 Naming Conventions

### Files

| Type | Convention | Example |
|------|-----------|---------|
| Python files | `snake_case.py` | `order_service.py` |
| Kotlin files | `PascalCase.kt` | `OrderViewModel.kt` |
| Markdown docs | `kebab-case.md` or `PascalCase.md` | `getting-started.md` |
| Config files | Standard naming | `docker-compose.yml` |
| Resources (Android) | `snake_case` | `ic_home.xml`, `bg_primary.xml` |

### Folders

- Use **lowercase** with **underscores** for Python: `apps/order_management/`
- Use **lowercase** with **underscores** for Android packages: `com.bulkbasket.ui.buyer`
- Use **lowercase** with **hyphens** for docs/scripts: `docs/user-guides/`

### Code Identifiers

#### Python
- Functions/variables: `snake_case`
- Classes: `PascalCase`
- Constants: `UPPER_SNAKE_CASE`
- Private: `_leading_underscore`

#### Kotlin
- Functions/variables: `camelCase`
- Classes: `PascalCase`
- Constants (companion): `UPPER_SNAKE_CASE`
- Composables: `PascalCase`

#### Resource IDs (Android XML)
- `snake_case` always: `btn_login`, `tv_username`

---

## 🆕 Adding New Files

### Checklist Before Creating Files

- [ ] Does a similar file already exist? (Avoid duplication)
- [ ] Am I placing it in the right folder per this guide?
- [ ] Does the filename follow naming conventions?
- [ ] If creating a new folder, does it need a `README.md`?
- [ ] Do I need to add tests alongside?
- [ ] Will I need to update documentation?

### Example: Adding a "Favorites" Feature

**Backend:**

```
1. Create new app:
   $ cd backend
   $ python manage.py startapp favorites
   $ mv favorites apps/

2. Files to add/edit:
   - apps/favorites/models.py          (Favorite model)
   - apps/favorites/serializers.py     (FavoriteSerializer)
   - apps/favorites/views.py           (FavoriteViewSet)
   - apps/favorites/urls.py            (router)
   - apps/favorites/permissions.py     (IsOwner)
   - apps/favorites/tests/             (test file)
   - config/urls.py                    (include new urls)
   - config/settings/base.py           (add to INSTALLED_APPS)
```

**Android:**

```
1. Add API interface:
   - data/remote/api/FavoritesApi.kt

2. Add Room support (if caching):
   - data/local/dao/FavoriteDao.kt
   - data/local/entity/FavoriteEntity.kt

3. Add repository:
   - data/repository/FavoriteRepository.kt
   - domain/repository/IFavoriteRepository.kt

4. Add domain model:
   - domain/model/Favorite.kt

5. Add use case:
   - domain/usecase/ToggleFavoriteUseCase.kt

6. Add UI:
   - ui/buyer/favorites/FavoritesScreen.kt
   - ui/buyer/favorites/FavoritesViewModel.kt
   - ui/buyer/favorites/FavoritesState.kt

7. Wire up Hilt:
   - di/RepositoryModule.kt (add binding)
```

**Documentation:**

```
8. Update relevant docs:
   - API.md                            (add favorites endpoints)
   - CHANGELOG.md                      (add to [Unreleased])
   - docs/architecture/data-flow.md    (if relevant)
```

**Tests:**

```
9. Add tests:
   - backend/apps/favorites/tests/test_views.py
   - android/app/src/test/.../FavoritesViewModelTest.kt
   - android/app/src/androidTest/.../FavoritesScreenTest.kt
```

---

## 🎓 Best Practices

### General

- **One responsibility per file** — keep files focused
- **Don't put logic in the wrong layer** — repositories don't render UI, views don't query DB directly
- **Mirror test structure to source structure** — `apps/orders/views.py` → `apps/orders/tests/test_views.py`
- **Document complex folders** — add `README.md` to non-obvious directories
- **Avoid deep nesting** — if you're 5+ levels deep, consider refactoring

### Backend Specifics

- **Thin views, fat services** — endpoint handlers stay short; logic in `services.py`
- **One model per file** if models get large (use `models/` package)
- **Group migrations sequentially** — never edit existing migrations
- **Keep `common/`** for truly shared utilities — not as a dumping ground

### Android Specifics

- **Feature folders over type folders** — group by feature, not by file type at the UI level
- **Use sealed classes for UI state** — clear state representation
- **Don't expose data layer classes to UI** — always map to domain models
- **One Composable per file** unless they're tightly coupled

---

## 🔄 Keeping This Document Updated

When the structure changes significantly:

1. Update this file
2. Update the README if top-level changes
3. Add an entry to CHANGELOG.md
4. Notify the team in standup

---

**Repository Structure Version:** 1.1  
**Last Updated:** September 2026  
**Maintained by:** Project Lead

---

[⬆ Back to top](#bulkbasket-repository-structure)