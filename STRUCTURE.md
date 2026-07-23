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
├── 📄 docker-compose.dev.yml       ← Backend-specific dev compose
├── 📄 requirements.txt             ← Python dependencies (production)
├── 📄 requirements-dev.txt         ← Dev dependencies (testing, linting)
├── 📄 pyproject.toml               ← Python project config (black, isort)
├── 📄 .env.example                 ← Environment variables template
├── 📄 .python-version              ← Python version (3.11)
├── 📄 pytest.ini                   ← Pytest configuration
├── 📄 README.md                    ← Backend-specific setup
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
│   │   ├── permissions.py          ← Permission classes
│   │   ├── services.py             ← Business logic
│   │   ├── admin.py                ← Django admin config
│   │   ├── migrations/             ← DB migrations
│   │   └── tests/                  ← App-specific tests
│   │       ├── __init__.py
│   │       ├── test_models.py
│   │       ├── test_views.py
│   │       └── test_services.py
│   │
│   ├── 📁 products/                ← Product catalog
│   │   ├── models.py               ← Product, Category models
│   │   ├── serializers.py
│   │   ├── views.py
│   │   ├── urls.py
│   │   ├── filters.py              ← Query filters
│   │   ├── services.py
│   │   └── tests/
│   │
│   ├── 📁 sellers/                 ← Seller profiles
│   │   ├── models.py               ← Seller model
│   │   ├── serializers.py
│   │   ├── views.py
│   │   ├── urls.py
│   │   ├── services.py             ← Nearby search logic
│   │   └── tests/
│   │
│   ├── 📁 orders/                  ← Order processing
│   │   ├── models.py               ← Order, OrderItem models
│   │   ├── serializers.py
│   │   ├── views.py
│   │   ├── urls.py
│   │   ├── state_machine.py        ← Order status transitions
│   │   ├── services.py             ← Order creation, total calculation
│   │   ├── tasks.py                ← Celery tasks (email, notifications)
│   │   └── tests/
│   │
│   ├── 📁 delivery/                ← Dispatch & tracking
│   │   ├── models.py               ← Delivery model
│   │   ├── serializers.py
│   │   ├── views.py
│   │   ├── urls.py
│   │   ├── services.py             ← Rider assignment logic
│   │   ├── tasks.py                ← Background dispatch tasks
│   │   └── tests/
│   │
│   ├── 📁 notifications/           ← FCM integration
│   │   ├── models.py               ← Device, Notification models
│   │   ├── serializers.py
│   │   ├── views.py
│   │   ├── urls.py
│   │   ├── fcm.py                  ← FCM client wrapper
│   │   ├── tasks.py                ← Send notification tasks
│   │   └── tests/
│   │
│   └── 📁 common/                  ← Shared utilities
│       ├── __init__.py
│       ├── exceptions.py           ← Custom exceptions
│       ├── responses.py            ← Standardized response helpers
│       ├── pagination.py           ← Custom pagination classes
│       ├── permissions.py          ← Base permission classes
│       ├── middleware.py           ← Custom middleware (logging, JWT)
│       └── utils.py                ← Helper functions
│
├── 📁 supabase/                    ← Supabase configuration
│   ├── migrations/                 ← SQL migration files
│   │   ├── 001_initial_schema.sql
│   │   ├── 002_add_indexes.sql
│   │   └── 003_seed_categories.sql
│   ├── policies/                   ← Row Level Security policies
│   │   ├── users_rls.sql
│   │   ├── products_rls.sql
│   │   └── orders_rls.sql
│   └── functions/                  ← Database functions
│       └── search_nearby_sellers.sql
│
├── 📁 tests/                       ← Integration & E2E tests
│   ├── __init__.py
│   ├── conftest.py                 ← Pytest fixtures
│   ├── test_auth_flow.py
│   ├── test_order_flow.py
│   └── factories/                  ← Factory Boy factories
│       ├── user_factory.py
│       └── product_factory.py
│
└── 📁 static/                      ← Static files (admin assets)
    └── admin/
```

### Backend Folder Purposes

| Folder | Purpose |
|--------|---------|
| `config/` | Django settings split by environment (dev, staging, prod) |
| `apps/` | Each business domain is its own Django app |
| `apps/<app>/models.py` | Database models for that domain |
| `apps/<app>/serializers.py` | Request/response data validation |
| `apps/<app>/views.py` | API endpoint handlers |
| `apps/<app>/services.py` | Business logic (kept out of views) |
| `apps/<app>/tasks.py` | Async background jobs (Celery) |
| `apps/<app>/tests/` | Unit tests for that app |
| `supabase/` | All Supabase-specific SQL and policies |
| `tests/` | Cross-app integration tests |

### Django App Pattern

Each app follows the **same structure** for consistency:

```
apps/orders/
├── models.py         ← What data looks like
├── serializers.py    ← How data enters/leaves the API
├── views.py          ← HTTP endpoint handlers (thin)
├── services.py       ← Business logic (fat)
├── urls.py           ← URL routing
├── permissions.py    ← Who can do what
├── tasks.py          ← Async jobs
└── tests/            ← Tests mirror the structure above
```

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
├── 📄 README.md                    ← Android-specific setup
│
├── 📁 gradle/                      ← Gradle wrapper
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
│       │   │   │   ├── NetworkModule.kt
│       │   │   │   ├── DatabaseModule.kt
│       │   │   │   ├── RepositoryModule.kt
│       │   │   │   └── SupabaseModule.kt
│       │   │   │
│       │   │   ├── 📁 data/               ← Data layer
│       │   │   │   ├── 📁 remote/         ← Network/API
│       │   │   │   │   ├── api/           ← Retrofit interfaces
│       │   │   │   │   │   ├── AuthApi.kt
│       │   │   │   │   │   ├── ProductsApi.kt
│       │   │   │   │   │   ├── OrdersApi.kt
│       │   │   │   │   │   └── DeliveryApi.kt
│       │   │   │   │   ├── dto/           ← Data Transfer Objects
│       │   │   │   │   │   ├── ProductDto.kt
│       │   │   │   │   │   └── OrderDto.kt
│       │   │   │   │   └── interceptors/  ← OkHttp interceptors
│       │   │   │   │       ├── AuthInterceptor.kt
│       │   │   │   │       └── LoggingInterceptor.kt
│       │   │   │   │
│       │   │   │   ├── 📁 local/          ← Room database
│       │   │   │   │   ├── BulkBasketDatabase.kt
│       │   │   │   │   ├── dao/           ← Data Access Objects
│       │   │   │   │   │   ├── ProductDao.kt
│       │   │   │   │   │   └── OrderDao.kt
│       │   │   │   │   ├── entity/        ← Room entities
│       │   │   │   │   │   ├── ProductEntity.kt
│       │   │   │   │   │   └── OrderEntity.kt
│       │   │   │   │   └── converters/    ← Type converters
│       │   │   │   │       └── DateConverter.kt
│       │   │   │   │
│       │   │   │   ├── 📁 repository/     ← Repositories (single source)
│       │   │   │   │   ├── AuthRepository.kt
│       │   │   │   │   ├── ProductRepository.kt
│       │   │   │   │   ├── OrderRepository.kt
│       │   │   │   │   └── DeliveryRepository.kt
│       │   │   │   │
│       │   │   │   └── 📁 mappers/        ← DTO ↔ Domain mappers
│       │   │   │       ├── ProductMapper.kt
│       │   │   │       └── OrderMapper.kt
│       │   │   │
│       │   │   ├── 📁 domain/             ← Domain layer
│       │   │   │   ├── 📁 model/          ← Domain models
│       │   │   │   │   ├── User.kt
│       │   │   │   │   ├── Product.kt
│       │   │   │   │   ├── Order.kt
│       │   │   │   │   ├── Seller.kt
│       │   │   │   │   └── Delivery.kt
│       │   │   │   │
│       │   │   │   ├── 📁 usecase/        ← Use cases (business logic)
│       │   │   │   │   ├── PlaceOrderUseCase.kt
│       │   │   │   │   ├── GetNearbySellersUseCase.kt
│       │   │   │   │   └── AcceptDeliveryUseCase.kt
│       │   │   │   │
│       │   │   │   └── 📁 repository/     ← Repository interfaces
│       │   │   │       ├── IAuthRepository.kt
│       │   │   │       └── IOrderRepository.kt
│       │   │   │
│       │   │   ├── 📁 ui/                 ← UI layer (Compose)
│       │   │   │   │
│       │   │   │   ├── 📁 theme/          ← Design system
│       │   │   │   │   ├── Theme.kt
│       │   │   │   │   ├── Colors.kt
│       │   │   │   │   ├── Typography.kt
│       │   │   │   │   ├── Shapes.kt
│       │   │   │   │   └── Spacing.kt
│       │   │   │   │
│       │   │   │   ├── 📁 common/         ← Shared composables
│       │   │   │   │   ├── components/    ← Reusable components
│       │   │   │   │   │   ├── BulkButton.kt
│       │   │   │   │   │   ├── BulkTextField.kt
│       │   │   │   │   │   ├── ProductCard.kt
│       │   │   │   │   │   ├── SellerCard.kt
│       │   │   │   │   │   ├── LoadingIndicator.kt
│       │   │   │   │   │   └── EmptyState.kt
│       │   │   │   │   ├── navigation/    ← App navigation
│       │   │   │   │   │   ├── BulkBasketNavHost.kt
│       │   │   │   │   │   ├── Routes.kt
│       │   │   │   │   │   └── BottomNav.kt
│       │   │   │   │   └── extensions/    ← Kotlin extensions
│       │   │   │   │       ├── ModifierExt.kt
│       │   │   │   │       └── FlowExt.kt
│       │   │   │   │
│       │   │   │   ├── 📁 auth/           ← Authentication flow
│       │   │   │   │   ├── login/
│       │   │   │   │   │   ├── LoginScreen.kt
│       │   │   │   │   │   └── LoginViewModel.kt
│       │   │   │   │   ├── signup/
│       │   │   │   │   │   ├── SignupScreen.kt
│       │   │   │   │   │   └── SignupViewModel.kt
│       │   │   │   │   └── splash/
│       │   │   │   │       └── SplashScreen.kt
│       │   │   │   │
│       │   │   │   ├── 📁 buyer/          ← Buyer-specific screens
│       │   │   │   │   ├── home/
│       │   │   │   │   │   ├── HomeScreen.kt
│       │   │   │   │   │   ├── HomeViewModel.kt
│       │   │   │   │   │   └── HomeState.kt
│       │   │   │   │   ├── search/
│       │   │   │   │   │   ├── SearchScreen.kt
│       │   │   │   │   │   └── SearchViewModel.kt
│       │   │   │   │   ├── seller_detail/
│       │   │   │   │   │   ├── SellerDetailScreen.kt
│       │   │   │   │   │   └── SellerDetailViewModel.kt
│       │   │   │   │   ├── cart/
│       │   │   │   │   │   ├── CartScreen.kt
│       │   │   │   │   │   └── CartViewModel.kt
│       │   │   │   │   ├── checkout/
│       │   │   │   │   │   ├── CheckoutScreen.kt
│       │   │   │   │   │   └── CheckoutViewModel.kt
│       │   │   │   │   ├── orders/
│       │   │   │   │   │   ├── OrdersListScreen.kt
│       │   │   │   │   │   ├── OrderDetailScreen.kt
│       │   │   │   │   │   └── OrderViewModel.kt
│       │   │   │   │   └── tracking/
│       │   │   │   │       ├── TrackingScreen.kt
│       │   │   │   │       └── TrackingViewModel.kt
│       │   │   │   │
│       │   │   │   ├── 📁 seller/         ← Seller-specific screens
│       │   │   │   │   ├── dashboard/
│       │   │   │   │   │   ├── SellerDashboardScreen.kt
│       │   │   │   │   │   └── DashboardViewModel.kt
│       │   │   │   │   ├── inventory/
│       │   │   │   │   │   ├── InventoryScreen.kt
│       │   │   │   │   │   ├── AddProductScreen.kt
│       │   │   │   │   │   └── InventoryViewModel.kt
│       │   │   │   │   └── orders/
│       │   │   │   │       ├── SellerOrdersScreen.kt
│       │   │   │   │       └── SellerOrdersViewModel.kt
│       │   │   │   │
│       │   │   │   └── 📁 rider/          ← Rider-specific screens
│       │   │   │       ├── jobs/
│       │   │   │       │   ├── JobsListScreen.kt
│       │   │   │       │   └── JobsViewModel.kt
│       │   │   │       ├── active_delivery/
│       │   │   │       │   ├── ActiveDeliveryScreen.kt
│       │   │   │       │   └── ActiveDeliveryViewModel.kt
│       │   │   │       └── earnings/
│       │   │   │           ├── EarningsScreen.kt
│       │   │   │           └── EarningsViewModel.kt
│       │   │   │
│       │   │   ├── 📁 utils/              ← Utilities
│       │   │   │   ├── Constants.kt
│       │   │   │   ├── DateFormatter.kt
│       │   │   │   ├── CurrencyFormatter.kt
│       │   │   │   ├── PreferencesManager.kt
│       │   │   │   └── NetworkResult.kt    ← Sealed class for results
│       │   │   │
│       │   │   └── 📁 service/            ← Background services
│       │   │       ├── FcmService.kt       ← Push notifications
│       │   │       └── LocationService.kt  ← GPS tracking (rider)
│       │   │
│       │   └── 📁 res/                    ← Android resources
│       │       ├── drawable/               ← Vector drawables
│       │       │   ├── ic_logo.xml
│       │       │   ├── ic_home.xml
│       │       │   └── btn_primary_bg.xml
│       │       ├── values/                 ← Resource values
│       │       │   ├── colors.xml
│       │       │   ├── strings.xml
│       │       │   ├── dimens.xml
│       │       │   ├── styles.xml
│       │       │   └── themes.xml
│       │       ├── values-night/           ← Dark theme overrides
│       │       │   └── colors.xml
│       │       ├── font/                   ← Custom fonts
│       │       │   ├── inter_regular.ttf
│       │       │   └── sora_bold.ttf
│       │       └── mipmap-*/               ← App icons (various densities)
│       │
│       ├── 📁 test/                       ← Unit tests
│       │   └── java/com/bulkbasket/
│       │       ├── data/
│       │       │   └── repository/
│       │       └── ui/
│       │           └── viewmodels/
│       │
│       └── 📁 androidTest/                ← Instrumented tests
│           └── java/com/bulkbasket/
│               ├── ui/                    ← Espresso UI tests
│               └── database/              ← Room tests
│
└── 📁 design/                              ← Design assets reference
    ├── figma_export/
    │   ├── icons/
    │   ├── illustrations/
    │   └── screenshots/
    └── README.md                          ← How to use design assets
```

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
| `data/local/` | Room database (entities, DAOs, type converters) |
| `data/repository/` | Repository implementations |
| `data/mappers/` | Convert between DTOs ↔ Entities ↔ Domain models |
| `domain/` | Pure business logic, no Android dependencies |
| `domain/model/` | Domain models used throughout the app |
| `domain/usecase/` | Single-responsibility business operations |
| `domain/repository/` | Repository interfaces (implementations in data/) |
| `ui/` | Compose screens, ViewModels, theme |
| `ui/theme/` | Design system: colors, typography, spacing |
| `ui/common/` | Reusable UI components shared across screens |
| `ui/auth/` | Login/signup screens |
| `ui/buyer/` | Buyer app screens |
| `ui/seller/` | Seller app screens |
| `ui/rider/` | Rider app screens |
| `utils/` | Helper classes (formatters, constants) |
| `service/` | Android services (FCM, location) |
| `res/` | Android resources (drawables, colors, strings) |

### Screen Pattern

Each screen follows the **same structure**:

```
ui/buyer/cart/
├── CartScreen.kt        ← @Composable function
├── CartViewModel.kt     ← Hilt @ViewModel
├── CartState.kt         ← UI state data class
└── CartEvent.kt         ← User events (optional)
```

> **Rule:** ViewModels never reference Android classes directly. They expose `StateFlow<UiState>` for the UI to observe.

---

## 📚 Documentation Structure

Detailed documentation lives in `/docs/`.

```
docs/
│
├── 📄 README.md                     ← Documentation index
│
├── 📁 architecture/                 ← Architecture docs
│   ├── overview.md                  ← High-level architecture
│   ├── android-architecture.md      ← Clean Architecture details
│   ├── backend-architecture.md      ← Django structure
│   ├── data-flow.md                 ← How data moves through the system
│   └── diagrams/                    ← Architecture diagrams
│       ├── system-overview.png
│       ├── android-layers.png
│       └── order-flow.png
│
├── 📁 api/                          ← API documentation
│   ├── README.md                    ← API overview
│   ├── authentication.md            ← Auth flow details
│   ├── endpoints.md                 ← Endpoint catalog
│   └── postman/                     ← Postman collection
│       └── BulkBasket.postman_collection.json
│
├── 📁 database/                     ← Database documentation
│   ├── schema.md                    ← Schema documentation
│   ├── erd.png                      ← Entity Relationship Diagram
│   ├── rls-policies.md              ← Row Level Security explained
│   └── migrations.md                ← How to write migrations
│
├── 📁 deployment/                   ← Deployment guides
│   ├── backend-deployment.md        ← Deploy Django to cloud
│   ├── android-release.md           ← Build and sign APK
│   ├── ci-cd.md                     ← GitHub Actions workflows
│   └── environment-setup.md         ← Env vars reference
│
├── 📁 development/                  ← Developer guides
│   ├── getting-started.md           ← First-day setup
│   ├── local-development.md         ← Running the project locally
│   ├── debugging.md                 ← How to debug issues
│   ├── testing.md                   ← How to write/run tests
│   └── troubleshooting.md           ← Common issues + solutions
│
├── 📁 design/                       ← Design documentation
│   ├── design-system.md             ← Component specs (links to Design.md)
│   ├── user-flows.md                ← User journey diagrams
│   ├── wireframes/                  ← Sketch/mockup exports
│   └── screenshots/                 ← App screenshots
│
├── 📁 team/                         ← Team documentation
│   ├── roles.md                     ← Detailed role descriptions
│   ├── workflow.md                  ← How we work together
│   ├── meetings.md                  ← Meeting templates
│   └── retrospectives/              ← Sprint retros
│       ├── 2026-06-week-1.md
│       └── 2026-06-week-2.md
│
├── 📁 academic/                     ← Academic submission docs
│   ├── proposal.md                  ← Project proposal
│   ├── progress-reports/            ← Weekly progress
│   ├── final-report.md              ← Final academic report
│   └── presentation.pdf             ← Defense slides
│
└── 📁 user-guides/                  ← End-user documentation
    ├── buyer-guide.md               ← How to use as a buyer
    ├── seller-guide.md              ← How to use as a seller
    └── rider-guide.md               ← How to use as a rider
```

---

## ⚙ Configuration Files

### `.github/` — GitHub Configuration

```
.github/
│
├── 📁 workflows/                    ← GitHub Actions CI/CD
│   ├── backend-ci.yml               ← Run on backend PRs
│   ├── android-ci.yml               ← Run on Android PRs
│   ├── deploy-staging.yml           ← Auto-deploy to staging
│   └── deploy-production.yml        ← Deploy to production
│
├── 📁 ISSUE_TEMPLATE/               ← Issue templates
│   ├── bug_report.md
│   ├── feature_request.md
│   └── config.yml
│
├── 📄 PULL_REQUEST_TEMPLATE.md      ← PR template
├── 📄 CODEOWNERS                    ← Auto-assign reviewers
└── 📄 dependabot.yml                ← Dependency updates
```

### Root Configuration Files

```
bulkbasket/
│
├── 📄 .gitignore                   ← What Git ignores
├── 📄 .editorconfig                ← Editor settings standardization
├── 📄 .pre-commit-config.yaml      ← Pre-commit hooks (linting)
├── 📄 docker-compose.yml           ← Local dev orchestration
├── 📄 docker-compose.test.yml      ← Test environment
├── 📄 .env.example                 ← Environment variables template
└── 📄 .nvmrc                       ← Node version (for tooling)
```

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
├── 📄 README.md                     ← Scripts documentation
│
├── 📁 dev/                          ← Development scripts
│   ├── setup.sh                     ← Full project setup
│   ├── reset-db.sh                  ← Reset local DB
│   ├── seed-db.py                   ← Seed sample data
│   ├── run-tests.sh                 ← Run all tests
│   └── lint-all.sh                  ← Lint all code
│
├── 📁 deploy/                       ← Deployment scripts
│   ├── deploy-backend.sh
│   ├── build-android-release.sh
│   └── upload-apk.sh
│
└── 📁 admin/                        ← Admin/ops scripts
    ├── backup-db.sh
    ├── create-superuser.py
    └── send-test-notification.py
```

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

**Repository Structure Version:** 1.0  
**Last Updated:** June 2026  
**Maintained by:** Project Lead

---

[⬆ Back to top](#bulkbasket-repository-structure)