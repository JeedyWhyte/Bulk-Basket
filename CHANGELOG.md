# Changelog

All notable changes to the **BulkBasket** project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## Version Numbering

We use **Semantic Versioning** with the format `MAJOR.MINOR.PATCH`:

- **MAJOR** version: Incompatible API changes or major redesigns
- **MINOR** version: New functionality, backward-compatible
- **PATCH** version: Bug fixes, backward-compatible

For academic milestones, we use suffixes:
- `-alpha` — Early development, unstable
- `-beta` — Feature-complete but under testing
- `-rc` — Release candidate
- `-dev` — Active development build

---

## [Unreleased]

### Added
- Initial project setup and structure
- Comprehensive documentation suite (README, CONTRIBUTING, API, Design)
- 5-person team structure with role assignments
- Three UI design directions (Sleek, Bold, Dashboard)
- Architecture diagrams and database schema design
- Seller-side navigation shell and bottom navigation (`SellerShellScreen`, `SellerBottomNav`), mirroring the buyer experience
- Buyer category browsing (`Category` domain model, `CategoryMapper`, dedicated category screen)
- Centralized token-refresh handling for the Android app (`TokenAuthenticator`)
- `orders.0002_orderitem_product_protect` migration — deleting a product referenced by past orders is now rejected instead of cascading
- A global DRF exception handler that turns a blocked deletion (`ProtectedError`) into a clean 400 response
- Backend test coverage for order creation, delivery status transitions, product visibility, and the new exception handler
- `apps.reviews` backend app — buyers can rate a seller on a delivered order (`GET`/`POST /reviews/`, `GET /reviews/seller/`); submitting a review recalculates the seller's aggregate `rating`/`total_ratings`
- `apps.payments` backend app — a **demo** payment gateway (`GET`/`POST /payments/methods/`, `DELETE /payments/methods/<id>/`, `POST /payments/charge/`) that Luhn-validates a demo card, declines the well-known test PAN `4000000000000002`, and always authorizes Cash on Delivery; no real card data is ever stored or transmitted
- `POST /users/close-account/` — self-service account deactivation (soft delete via `is_active=False`)
- Android: Ratings & Reviews screen now shows real reviews, and delivered orders in My Orders get a "Rate Seller" action
- Android: Checkout now has a Payment Method step (Cash on Delivery or a demo card), Payment Settings manages saved demo cards, and Close Account performs a real (confirmed) deactivation instead of only linking to support
- Crossfade transitions between buyer/seller bottom-nav tabs, and fade transitions between navigation destinations app-wide

### Changed
- `create_order()` now locks all line-item products in a single query instead of one per item
- Android login no longer waits on FCM token registration before completing
- Screens that used the green `CurvedTopAppBar` (Rider Profile, Seller Detail, Checkout, Rider Jobs, Notifications, My Orders) now use the same flat, background-matching header as the rest of the app, so their top bar no longer clashes with the bottom navigation bar's color

### Deprecated
- N/A

### Removed
- Unused Celery task for order-status notifications, superseded by the synchronous notification call already used in `orders/views.py`
- Empty Django test stub files (`orders/tests.py`, `products/tests.py`, `users/tests.py`) that the test runner never collected, replaced by real `tests/` packages with actual coverage
- `CurvedTopAppBar` composable and the `Dimensions.topBarCurve` token it was the only user of, now that every screen shares one flat header style

### Fixed
- Android `BASE_URL` no longer points at a developer's local network address
- The buyer's Cart screen no longer renders blank when reached via "View Cart" from a seller or product detail page
- The category list no longer fails to load due to a pagination mismatch with the backend
- A delivery that fails before pickup no longer permanently strands its order — it's now reclaimable by another rider
- Delivery and order status updates are now applied atomically
- A seller can now open their own soft-deleted product for editing instead of getting a 404
- My Orders screen had no back button at all in its header — it's now consistent with every other screen

### Security
- Deleting a product still referenced by past orders is now rejected at the application level (`PROTECT` + a custom exception handler) instead of cascading or raising an unhandled 500

---

## [0.1.0-alpha] — 2026-06-15 (Project Kickoff)

The official kickoff of the BulkBasket project. This release establishes the foundation for the academic group project.

### Added

#### Project Foundation
- Project concept and three-sided marketplace vision
- Product name finalized: **BulkBasket**
- Tagline: "From the Market, to Your Kitchen Door"
- Academic context: Mobile Application Development (2025/2026)

#### Documentation
- `README.md` — Comprehensive project overview
- `CONTRIBUTING.md` — Development workflow and standards
- `CHANGELOG.md` — This document
- `API.md` — Full API specification
- `Design.md` — UI design system specification
- Project proposal document (DOCX format)
- Team structure document (DOCX format)
- Investor pitch deck (DOCX format)
- School project proposal (DOCX format)

#### Architecture
- Three-tier client-server architecture defined
- Mobile app: Native Android with Kotlin
- Backend: Python + Django REST Framework
- Database: Supabase (PostgreSQL)
- Cache & broker: Redis
- Authentication: Supabase Auth (JWT-based)
- Real-time: Supabase Realtime (WebSockets)

#### Design
- Three UI design directions explored:
  - **Sleek Design**: Minimal, Apple-inspired
  - **Bold Design**: Vibrant, high-contrast
  - **Dashboard Design**: Desktop-first layout
- Complete design system with color palette, typography, spacing
- Component library specifications
- Screen mockups for Buyer, Seller, and Rider flows

#### Team Structure
- 5 defined roles:
  - Role 1: Project Lead & Backend Engineer
  - Role 2: Android Developer (Buyer App)
  - Role 3: Android Developer (Seller & Rider Apps)
  - Role 4: UI/UX Designer & Brand Lead
  - Role 5: DevOps, Testing & Documentation Lead
- 14-week project timeline across 6 phases
- Git workflow and code review standards defined

#### Tech Stack Decisions
- Android libraries: Retrofit 2, OkHttp, Coroutines, Flow, Room, Hilt, Coil
- Backend libraries: Django 4.2+, DRF, Celery, Redis
- DevOps: Docker, Docker Compose, GitHub Actions
- Testing: Pytest (backend), JUnit + Espresso (Android)
- Notifications: Firebase Cloud Messaging (FCM)

### Notes
- This is a planning and documentation release
- No functional code yet
- Project officially enters Phase 1 (Planning & Setup)

---

## Planned Releases

### [0.2.0-alpha] — Target: 2026-07-15 (End of Phase 2: Design)

**Goal:** Complete UI/UX design and prototypes

- [ ] Finalize design direction (Sleek / Bold / Dashboard)
- [ ] Complete Figma design system
- [ ] All Buyer screens mocked up
- [ ] All Seller screens mocked up
- [ ] All Rider screens mocked up
- [ ] Interactive prototype
- [ ] Design handoff document for developers
- [ ] Android drawable asset pack

### [0.3.0-alpha] — Target: 2026-08-05 (End of Phase 3: Backend)

**Goal:** Working backend API ready for Android integration

- [ ] Django project initialized
- [ ] Supabase project configured with RLS policies
- [ ] PostgreSQL schema deployed
- [ ] Authentication endpoints (signup, login, logout)
- [ ] Products CRUD endpoints
- [ ] Sellers endpoints (profile, nearby)
- [ ] Orders endpoints (create, list, status transitions)
- [ ] Delivery endpoints (jobs, accept, status)
- [ ] Celery background tasks setup
- [ ] Redis caching for hot paths
- [ ] Backend unit & integration tests (>80% coverage)
- [ ] Postman collection published
- [ ] CI/CD pipeline (GitHub Actions) configured

### [0.4.0-beta] — Target: 2026-08-26 (End of Phase 4: Android)

**Goal:** All three apps functional end-to-end

#### Buyer App
- [ ] Authentication screens (login/signup)
- [ ] Home & discovery screen with nearby sellers
- [ ] Seller profile & product listing screens
- [ ] Product search with filters
- [ ] Shopping cart functionality
- [ ] Checkout flow (simulated payment)
- [ ] Real-time order tracking
- [ ] Order history screen

#### Seller App
- [ ] Seller registration & profile setup
- [ ] Product management (add, edit, remove)
- [ ] Incoming order notifications via FCM
- [ ] Order acceptance/rejection flow
- [ ] Order status management
- [ ] Daily sales dashboard

#### Rider App
- [ ] Rider registration & availability toggle
- [ ] Job queue with delivery details
- [ ] Active delivery tracking
- [ ] Pickup & delivery confirmation
- [ ] Earnings summary

### [0.5.0-beta] — Target: 2026-09-09 (End of Phase 5: Testing)

**Goal:** Integrated, tested, and polished application

- [ ] End-to-end integration testing complete
- [ ] All Android unit tests passing
- [ ] All Espresso UI tests passing
- [ ] All known bugs from QA fixed
- [ ] Performance optimization (cold start, scroll performance)
- [ ] Real-time event testing validated
- [ ] Push notification delivery reliable
- [ ] Offline mode validated
- [ ] Memory leaks addressed
- [ ] Crash rate < 1%

### [1.0.0-rc] — Target: 2026-09-23 (End of Phase 6: Final Delivery)

**Goal:** Academic submission and final demo

- [ ] Final technical documentation
- [ ] Final test report with coverage metrics
- [ ] Backend deployed to production environment
- [ ] Signed APK built and tested on physical devices
- [ ] Presentation slides finalized
- [ ] Demo video recorded
- [ ] Final report submitted
- [ ] Live demo presentation delivered

### [1.0.0] — Target: 2026-10-01 (Public Release)

**Goal:** Stable public release of the academic project

- [ ] All academic deliverables submitted
- [ ] Final grade received
- [ ] Project archived as portfolio piece
- [ ] Public repository made available
- [ ] Demo APK published

---

## Future Roadmap (Post-1.0)

These versions represent potential future work beyond the academic scope:

### [1.1.0] — Payment Integration (Future)
- Paystack integration for Nigerian payments
- Flutterwave as alternative
- Payment receipt generation
- Refund flow

### [1.2.0] — Advanced Features (Future)
- Rating & review system for sellers and riders
- Promo codes and discount management
- Wishlist functionality
- Push notification preferences

### [1.3.0] — Analytics & Insights (Future)
- Advanced seller analytics dashboard
- Buyer recommendation engine
- Sales reporting and exports
- Heatmap of high-demand areas

### [2.0.0] — Platform Expansion (Future)
- iOS application (Swift/SwiftUI)
- Web admin panel
- B2B channel for restaurants and caterers
- Multi-language support (Yoruba, Hausa, Igbo, Pidgin)

---

## Migration Guides

### From 0.x to 1.0 (When Released)

This section will document any breaking changes when transitioning from beta versions to the stable 1.0 release. As of now, no migrations are required since we're still in early development.

---

## Acknowledgements

### Contributors

This project is built by a 5-person team. All contributors and their roles are documented in the [team structure document](./BulkBasket_Team_Structure.docx).

### Tools & Technologies

We're grateful to the open-source communities behind:

- Kotlin & Android Jetpack
- Django & Django REST Framework
- PostgreSQL & Supabase
- Docker & GitHub Actions
- Figma & related design tools

---

## How to Update This Changelog

When making significant changes:

1. **Always add new entries to the `[Unreleased]` section** at the top
2. **Use the established categories:**
   - `Added` — New features
   - `Changed` — Changes to existing functionality
   - `Deprecated` — Features that will be removed
   - `Removed` — Removed features
   - `Fixed` — Bug fixes
   - `Security` — Security-related fixes
3. **When releasing a new version**, move `[Unreleased]` entries under a new version heading
4. **Include the release date** in `YYYY-MM-DD` format
5. **Link to GitHub releases** when possible

### Example Entry

```markdown
## [Unreleased]

### Added
- Buyer can now filter products by price range
- Sellers can mark products as out of stock

### Fixed
- Resolved crash when network connection drops during checkout (#45)

### Changed
- Improved performance of product listing on home screen
```

---

**Maintained by:** The BulkBasket Project Team  
**Last updated:** June 2026