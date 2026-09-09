# BulkBasket API Documentation

Complete reference for the BulkBasket REST API.

**Base URL:**
- Development: `http://localhost:8000/api/v1/`
- Staging: `https://staging-api.bulkbasket.app/api/v1/`
- Production: `https://api.bulkbasket.app/api/v1/`

**Authentication:** Bearer JWT (issued directly by this API via `djangorestframework-simplejwt`)  
**Content-Type:** `application/json`  
**API Version:** v1

---

## 📋 Table of Contents

1. [Authentication](#-authentication)
2. [Common Patterns](#-common-patterns)
3. [Error Responses](#-error-responses)
4. [Auth Endpoints](#-auth-endpoints)
5. [User Endpoints](#-user-endpoints)
6. [Seller Endpoints](#-seller-endpoints)
7. [Product Endpoints](#-product-endpoints)
8. [Order Endpoints](#-order-endpoints)
9. [Delivery Endpoints](#-delivery-endpoints)
10. [Notification Endpoints](#-notification-endpoints)
11. [Webhook Endpoints](#-webhook-endpoints)
12. [Rate Limiting](#-rate-limiting)
13. [Pagination](#-pagination)
14. [Filtering & Sorting](#-filtering--sorting)

---

## 🔐 Authentication

All endpoints except `/users/register/`, `/users/login/`, and `/users/token/refresh/` require a valid JWT access token in the `Authorization` header:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Tokens are issued by this API itself (`djangorestframework-simplejwt`) — there is **no** Supabase Auth integration. Supabase is used only to host the Postgres database in staging/production.

### Token Lifetime

- **Access token:** 1 hour
- **Refresh token:** 7 days

There is no server-side token blacklist/revocation configured, so there is no `/logout/` endpoint — clients simply discard the tokens they hold.

### Token Refresh Flow

When the access token expires:

1. Send the refresh token to `/users/token/refresh/`
2. Receive a new access token
3. Retry the original request with the new token

```bash
# Example: Refresh an expired token
curl -X POST https://api.bulkbasket.app/api/v1/users/token/refresh/ \
  -H "Content-Type: application/json" \
  -d '{"refresh": "eyJhbGc..."}'
```

---

## 🔄 Common Patterns

### Request Headers

All requests should include:

```http
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

### Response Format

The API does **not** use one global response envelope. Two shapes occur, and each endpoint below notes which one it uses:

**1. Plain DRF representation** — most `GET`, and several `POST`/`PATCH`, endpoints (products, categories, addresses, seller/rider profiles, deliveries, notifications) return the serialized object or list with no wrapper at all:

```json
{
  "id": 1,
  "name": "Long Grain Parboiled Rice",
  "price": "42000.00"
}
```

List endpoints return DRF's standard paginated envelope instead of a bare array — see [Pagination](#-pagination):

```json
{
  "count": 87,
  "next": "http://localhost:8000/api/v1/products/?page=2",
  "previous": null,
  "results": [ ... ]
}
```

A successful `DELETE` returns `204 No Content` with an empty body.

**2. `status` / `message` / `data` envelope** — endpoints that do more than plain CRUD (registration, seller/rider profile creation, placing an order, updating an order or delivery status, notification-read actions, FCM token registration, nearby-sellers search) return:

```json
{
  "status": "success",
  "message": "Order placed successfully.",
  "data": { ... }
}
```

---

## ❌ Error Responses

There is no `error.code`, `details`, or top-level `success` field anywhere in the API. Two shapes occur:

**1. Business-rule / not-found errors** raised by view code reuse the envelope above with `status: "error"`:

```json
{
  "status": "error",
  "message": "Insufficient stock for 'Long Grain Rice'. Available: 5."
}
```

**2. DRF's default validation / auth / permission errors** pass straight through DRF's built-in exception handling, unwrapped:

```json
{
  "email": ["A user with this email already exists."],
  "password": ["Ensure this field has at least 8 characters."]
}
```

```json
{ "detail": "Authentication credentials were not provided." }
```

One custom case is layered on top of DRF's default handler (`apps/common/exceptions.py`, wired via `EXCEPTION_HANDLER` in settings): deleting a row that's still referenced through an `on_delete=PROTECT` foreign key (e.g. a `Product` still referenced by an `OrderItem`) is converted from an unhandled `500` into a clean `400`:

```json
{
  "status": "error",
  "message": "This item cannot be deleted because it is still referenced by other records."
}
```

In practice this path isn't reachable through `DELETE /products/<id>/` today — that endpoint soft-deletes the product (see below) instead of issuing a real row delete — but the handler stays in place as a safety net for any future hard-delete path.

### HTTP Status Codes

| Code | Meaning | When Used |
|------|---------|-----------|
| `200` | OK | Successful GET, PATCH, PUT |
| `201` | Created | Successful POST creating a resource |
| `204` | No Content | Successful DELETE |
| `400` | Bad Request | Invalid request body, failed validation, or a business-rule violation (e.g. out of stock, illegal status transition, duplicate email) |
| `401` | Unauthorized | Missing or invalid JWT |
| `403` | Forbidden | Authenticated but lacking permission (wrong role, or not the resource owner) |
| `404` | Not Found | Resource doesn't exist (or isn't visible to the caller) |
| `500` | Internal Server Error | Server-side issue |

`409`, `422`, and `429` are **not** used anywhere in this codebase — validation and business-rule failures come back as `400`, and there is currently no rate limiting (see [Rate Limiting](#-rate-limiting)).

### Example Error Response

```json
{
  "email": ["A user with this email already exists."],
  "password": ["Ensure this field has at least 8 characters."]
}
```

---

## 🔑 Auth Endpoints

These endpoints live under the `/users/` prefix, not `/auth/`.

### POST `/users/register/`

Register a new user account.

**Authentication:** None required
**Response shape:** `status`/`message`/`data` envelope

**Request Body:**

```json
{
  "username": "janedoe",
  "email": "user@example.com",
  "password": "SecurePass123!",
  "role": "buyer",
  "phone_number": "+2348012345678"
}
```

**Field Validations:**
- `username` — Required, unique (inherited from Django's `AbstractUser`)
- `email` — Required, unique
- `password` — Required, min 8 characters
- `role` — Optional, one of: `buyer`, `seller`, `rider` (defaults to `buyer`)
- `phone_number` — Optional

**Success Response (201):**

```json
{
  "status": "success",
  "message": "Registration successful",
  "data": {
    "id": 42,
    "username": "janedoe",
    "email": "user@example.com",
    "role": "buyer",
    "phone_number": "+2348012345678",
    "avatar_url": "",
    "is_verified": false,
    "created_at": "2026-06-15T10:00:00Z"
  }
}
```

Registration does **not** return an access/refresh token pair — call `/users/login/` afterwards to authenticate.

**Possible Errors:**
- `400` — Invalid request body / validation failed (e.g. duplicate email or username, password too short)

---

### POST `/users/login/`

Authenticate with username and password. This is `djangorestframework-simplejwt`'s stock `TokenObtainPairView` — the response is **not** wrapped in the `status`/`message`/`data` envelope.

**Authentication:** None required

**Request Body:**

```json
{
  "username": "janedoe",
  "password": "SecurePass123!"
}
```

Note: login is by `username`, not `email` — the user model doesn't override `USERNAME_FIELD`.

**Success Response (200):**

```json
{
  "refresh": "eyJhbGc...",
  "access": "eyJhbGc..."
}
```

**Possible Errors:**
- `401` — Invalid credentials (`{"detail": "No active account found with the given credentials"}`)

---

### POST `/users/token/refresh/`

Refresh an expired access token. Stock `TokenRefreshView`, also unwrapped.

**Authentication:** None (uses refresh token)

**Request Body:**

```json
{
  "refresh": "eyJhbGc..."
}
```

**Success Response (200):**

```json
{
  "access": "eyJhbGc..."
}
```

---

There is no logout, forgot-password, or reset-password endpoint implemented in the current backend.

---

## 👤 User Endpoints

### GET `/users/profile/`

Get the authenticated user's profile.

**Authentication:** Required
**Response shape:** Plain DRF representation (unwrapped)

**Success Response (200):**

```json
{
  "id": 42,
  "username": "janedoe",
  "email": "user@example.com",
  "role": "buyer",
  "phone_number": "+2348012345678",
  "avatar_url": "https://...",
  "is_verified": false,
  "created_at": "2026-06-15T10:00:00Z"
}
```

Delivery addresses are **not** nested here — fetch them separately from `/users/addresses/`.

---

### PATCH `/users/profile/`

Update authenticated user's profile.

**Authentication:** Required

**Request Body:** (any subset of these writable fields)

```json
{
  "username": "janedoe2",
  "email": "new@example.com",
  "phone_number": "+2348012345678",
  "avatar_url": "https://..."
}
```

`role`, `is_verified`, and `created_at` are read-only. There is no `full_name` field on the user model.

**Success Response (200):** Updated user object (same shape as GET, unwrapped)

---

### GET `/users/addresses/`

List the authenticated user's delivery addresses.

**Authentication:** Required
**Response shape:** Paginated (`count`/`next`/`previous`/`results`)

---

### POST `/users/addresses/`

Add a delivery address.

**Authentication:** Required

**Request Body:**

```json
{
  "label": "Office",
  "street": "10 Marina",
  "city": "Lagos",
  "state": "Lagos State",
  "latitude": 6.4500,
  "longitude": 3.3800,
  "is_default": false
}
```

Field names are `latitude`/`longitude`, not `lat`/`lng`. There is no `country` field on the `Address` model.

**Success Response (201):** Created address object, unwrapped

---

Removing a delivery address is **not currently supported** by the API — there is no `DELETE` route for an individual address (the `addresses/` URL only wires up list + create).

---

## 🏪 Seller Endpoints

Seller endpoints live under `/sellers/`, but the actual paths differ from a flat `/sellers/<id>/` shape.

### POST `/sellers/profile/`

Seller sets up their business profile (one-time, after registering with `role: "seller"`).

**Authentication:** Required (seller)
**Response shape:** `status`/`message`/`data` envelope

**Request Body:**

```json
{
  "business_name": "Eze Bulk Traders",
  "market_name": "Lagos Island Market",
  "description": "Family-owned bulk food store since 1985",
  "latitude": 6.4550,
  "longitude": 3.3950,
  "opening_time": "08:00:00",
  "closing_time": "18:00:00"
}
```

**Success Response (201):** Created seller profile (see shape below)

**Possible Errors:**
- `400` — Seller profile already exists for this user

---

### GET `/sellers/profile/me/`

Get the authenticated seller's own profile.

**Authentication:** Required (seller)
**Response shape:** Plain DRF representation (unwrapped)

**Success Response (200):**

```json
{
  "id": 7,
  "username": "eze_traders",
  "email": "eze@example.com",
  "business_name": "Eze Bulk Traders",
  "market_name": "Lagos Island Market",
  "description": "Family-owned bulk food store since 1985",
  "latitude": "6.455000",
  "longitude": "3.395000",
  "rating": "4.80",
  "total_ratings": 142,
  "is_open": true,
  "opening_time": "08:00:00",
  "closing_time": "18:00:00",
  "products": [ ... ],
  "created_at": "2026-01-15T10:00:00Z"
}
```

`products` embeds the seller's **full** product list inline (every `Product`, not paginated) — there is no separate `/sellers/<id>/products/` endpoint.

---

### PATCH `/sellers/profile/me/`

Update authenticated seller's profile. (This is the real path for what used to be documented as `PATCH /sellers/me/`.)

**Authentication:** Required (seller)

**Request Body:** Any subset of the seller-profile fields above (`rating`, `total_ratings`, `created_at` are read-only)
**Success Response (200):** Updated seller profile, unwrapped

---

### GET `/sellers/<id>/`

Get a seller's public profile. Same representation as `GET /sellers/profile/me/` above (including the embedded `products` list).

**Authentication:** None required (`AllowAny`) — contrary to earlier docs, this endpoint is public.

---

### GET `/sellers/nearby/`

List sellers near a location.

**Authentication:** None required (`AllowAny`)
**Response shape:** `status`/`message`/`data` envelope; **not paginated** — always returns the full matching list

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `lat` | float | Yes | Buyer's latitude |
| `lng` | float | Yes | Buyer's longitude |
| `radius` | float | No | Search radius in km (default: 10) |

There is no `category`, `page`, or `page_size` parameter — filtering by category and pagination are **not implemented** on this endpoint.

**Example Request:**

```http
GET /api/v1/sellers/nearby/?lat=6.4541&lng=3.3947&radius=5
```

**Success Response (200):**

```json
{
  "status": "success",
  "message": "Success",
  "data": [
    {
      "id": 7,
      "username": "eze_traders",
      "email": "eze@example.com",
      "business_name": "Eze Bulk Traders",
      "market_name": "Lagos Island Market",
      "description": "Family-owned bulk food store since 1985",
      "latitude": "6.455000",
      "longitude": "3.395000",
      "rating": "4.80",
      "total_ratings": 142,
      "is_open": true,
      "opening_time": "08:00:00",
      "closing_time": "18:00:00",
      "products": [ ... ],
      "created_at": "2026-01-15T10:00:00Z"
    }
  ]
}
```

`nearby_sellers` only returns sellers with `is_open=True` and both coordinates set; there's no `is_verified`, `review_count`, `address`, `phone`, `image_url`, or per-weekday `opening_hours` field on the seller model.

---

## 📦 Product Endpoints

### GET `/products/`

List all products with filtering.

**Authentication:** None required (`AllowAny`) for reads
**Response shape:** Paginated (`count`/`next`/`previous`/`results`)

**Query Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `category` | string | Filter by category **slug** |
| `unit` | string | Filter by exact unit (`kg`, `bag`, `basket`, `piece`, `bundle`) |
| `is_available` | boolean | Filter by availability |
| `min_price` | decimal | Minimum price filter |
| `max_price` | decimal | Maximum price filter |
| `search` | string | Free-text search across `name` and `description` (DRF `SearchFilter`) |
| `ordering` | string | `price`, `created_at`, or `-price`/`-created_at` for descending (DRF `OrderingFilter`) |
| `seller` | string | Pass `seller=me` while authenticated to see **your own** listings, including unavailable ones |
| `page` | int | Page number |
| `page_size` | int | Results per page (max 100) |

There is no `in_stock`, `seller_id`, `sort_by`, or `order` parameter — use `is_available`, `seller=me`, and `ordering` instead. Anonymous and buyer/rider requests only ever see `is_available=True` products; `seller=me` is the only way to include a seller's own unavailable products in a list.

**Success Response (200):**

```json
{
  "count": 87,
  "next": "http://localhost:8000/api/v1/products/?page=2",
  "previous": null,
  "results": [
    {
      "id": 12,
      "name": "Long Grain Parboiled Rice",
      "description": "Premium quality rice, 50kg bag",
      "price": "42000.00",
      "unit": "bag",
      "min_order_qty": 1,
      "stock_quantity": 18,
      "image_url": "https://...",
      "is_available": true,
      "in_stock": true,
      "category": 3,
      "category_name": "Grains & Cereals",
      "seller": 7,
      "seller_name": "eze_traders",
      "created_at": "2026-06-10T10:00:00Z"
    }
  ]
}
```

`seller` and `category` are plain integer IDs (not nested objects), and there is no `currency` field.

---

There is no `GET /products/search/` endpoint — use `?search=` on `GET /products/` instead (see above).

---

### GET `/products/<id>/`

Get product details.

**Authentication:** None required for the product itself; visibility depends on who's asking
**Response shape:** Plain DRF representation (unwrapped)

**Visibility rules** (`ProductViewSet.get_queryset`, branch on `self.action == 'retrieve'`):
- Anonymous or non-owning users can only retrieve a product where `is_available=True`.
- An **authenticated seller can retrieve their own product even if it has been soft-deleted** (`is_available=False`) — not only via `?seller=me` on the list endpoint, but on this single-item endpoint too, without needing that query parameter.
- Any other product owned by a different seller and currently unavailable returns `404`.

**Success Response (200):** Product object with full details (same shape as a list item above)

---

### POST `/products/`

Create a new product (seller only).

**Authentication:** Required (seller)
**Response shape:** Plain DRF representation (unwrapped), `201`

**Request Body:**

```json
{
  "name": "Long Grain Rice",
  "description": "Premium quality, harvested 2026",
  "price": 42000.00,
  "unit": "bag",
  "min_order_qty": 1,
  "stock_quantity": 20,
  "category": 3,
  "image_url": "https://storage.supabase.co/..."
}
```

`category` is the category's integer ID. `seller` is set automatically from the authenticated user and cannot be passed in the body.

**Success Response (201):** Created product object

---

### PATCH `/products/<id>/`

Update a product (seller only, must own the product — enforced by `get_queryset` restricting write actions to `seller=request.user`, so a non-owner gets `404`, not `403`).

**Authentication:** Required (seller)

**Request Body:** Any subset of product fields
**Success Response (200):** Updated product object, unwrapped

---

### DELETE `/products/<id>/`

Delete a product (seller only, must own the product).

**Authentication:** Required (seller)
**Success Response (204):** No content

This is a **soft delete**: `perform_destroy` sets `is_available=False` and saves — the row is never actually removed, so past orders that reference the product stay intact. Because of that, the FK-`PROTECT`-to-`400` error case described in [Error Responses](#-error-responses) is not triggered by this endpoint today; the delete always succeeds with `204`. The seller can still fetch the soft-deleted product afterwards via `GET /products/<id>/` or `?seller=me` (see the retrieve rules above).

---

### GET `/products/categories/`

List all product categories.

**Authentication:** None required (`AllowAny`)
**Response shape:** Paginated (`count`/`next`/`previous`/`results`) — `CategoryViewSet` inherits the project's global `DEFAULT_PAGINATION_CLASS` (`apps/common/pagination.py`), so this is no longer a bare array.

**Success Response (200):**

```json
{
  "count": 12,
  "next": null,
  "previous": null,
  "results": [
    {
      "id": 3,
      "name": "Grains & Cereals",
      "slug": "grains-cereals",
      "icon_url": "https://..."
    },
    {
      "id": 4,
      "name": "Fresh Produce",
      "slug": "fresh-produce",
      "icon_url": "https://..."
    }
  ]
}
```

There is no `product_count` field on `Category` — it isn't annotated onto the queryset.

`GET /products/categories/<id>/` (retrieve a single category) also exists, since `CategoryViewSet` is a `ReadOnlyModelViewSet`.

---

## 🛒 Order Endpoints

### POST `/orders/`

Place a new order (buyer only).

**Authentication:** Required (buyer)
**Response shape:** `status`/`message`/`data` envelope

**Request Body:**

```json
{
  "seller_id": 7,
  "delivery_address_id": 3,
  "items": [
    {
      "product_id": 12,
      "quantity": 2
    },
    {
      "product_id": 15,
      "quantity": 1
    }
  ],
  "notes": "Please call upon arrival"
}
```

`seller_id`, `delivery_address_id`, and `product_id` are integer IDs, not UUIDs (only the `Order` row itself uses a UUID primary key). The request field is `notes`, not `delivery_notes`, and there is no `payment_method` field anywhere in the order model — payment method isn't tracked by this API.

**Success Response (201):**

```json
{
  "status": "success",
  "message": "Order placed successfully.",
  "data": {
    "id": "9d3f7b1e-...-uuid",
    "buyer": 42,
    "buyer_name": "janedoe",
    "seller": 7,
    "seller_name": "eze_traders",
    "status": "pending",
    "subtotal": "84000.00",
    "delivery_fee": "500.00",
    "total": "84500.00",
    "notes": "Please call upon arrival",
    "items": [
      {
        "id": 101,
        "product": 12,
        "product_name": "Long Grain Parboiled Rice",
        "product_unit": "bag",
        "quantity": 2,
        "unit_price": "42000.00",
        "total_price": "84000.00"
      }
    ],
    "created_at": "2026-06-15T10:32:00Z",
    "updated_at": "2026-06-15T10:32:00Z"
  }
}
```

`delivery_fee` is currently a flat ₦500 (`DELIVERY_FEE` constant in `apps/orders/services.py`), not dynamically calculated. There is no `order_number`, `delivery_address` (nested), `payment_method`, or `estimated_delivery_time` field in the response.

**Possible Errors:**
- `400` — Delivery address not found, product doesn't exist, product doesn't belong to the given seller, product unavailable, insufficient stock, or quantity below `min_order_qty` (all as `{"status": "error", "message": "..."}`)

---

### GET `/orders/`

List the authenticated **buyer's** own orders.

**Authentication:** Required (buyer)
**Response shape:** Paginated (`count`/`next`/`previous`/`results`)

There is **no filtering or sorting support** on this endpoint — `status`, `start_date`, `end_date`, `sort_by`/`order` are not wired up (the view doesn't declare `filterset_fields`, `search_fields`, or `ordering_fields`, so the globally-enabled filter backends are no-ops here). Only `page`/`page_size` work. Results are ordered by `-created_at` (the model's default ordering).

---

### GET `/orders/<id>/`

Get detailed order information.

**Authentication:** Required — **buyer only**, and only for orders they placed (`get_queryset` filters `buyer=request.user`). Sellers and riders do **not** have access to this endpoint for an order they're involved in — sellers use `GET /orders/seller/` (list only, no single-order detail view), and riders work against the separate `Delivery` object via the delivery endpoints below.

**Success Response (200):** Order object with full details (same shape as the `data` in the create response), unwrapped

---

### GET `/orders/seller/`

List all orders placed with the authenticated seller.

**Authentication:** Required (seller)
**Response shape:** Paginated (`count`/`next`/`previous`/`results`)

No filtering/sorting is wired up here either.

---

### PATCH `/orders/seller/<id>/status/`

Update the status of an order. This single endpoint replaces separate accept/reject/ready/cancel actions — there is **no** `PATCH /orders/<id>/accept/`, `/reject/`, `/ready/`, or `/cancel/` endpoint.

**Authentication:** Required (seller, must own the order)
**Response shape:** `status`/`message`/`data` envelope

**Request Body:**

```json
{
  "status": "confirmed"
}
```

`status` must be a legal next state for the order's current status — see [Order Status Lifecycle](#order-status-lifecycle) below. An illegal transition returns `400` with a message describing the allowed next states.

**Success Response (200):**

```json
{
  "status": "success",
  "message": "Order status updated to 'confirmed'.",
  "data": { ... }
}
```

**Side Effects:**
- A notification (and, if the buyer has an FCM token registered, a push notification) is sent to the buyer synchronously, in the same request — via `apps.notifications.services.notify_order_status_change`, called directly from the view. There is **no background/async task** involved; the `apps/orders/tasks.py` Celery task some older docs referenced no longer exists.
- When status becomes `ready`, a `Delivery` row is created automatically so riders can see and claim the job (`get_or_create` in `apps/orders/state_machine.py`).

There is currently **no endpoint for a buyer to cancel their own order** — cancellation (`status: "cancelled"`) can only be performed by the seller through this same endpoint.

---

### Order Status Lifecycle

```
pending → confirmed → preparing → ready → in_transit → delivered
   ↓          ↓            ↓
cancelled  cancelled   cancelled
```

| Status | Description | Who Can Trigger |
|--------|-------------|-----------------|
| `pending` | Order placed, awaiting seller response | Initial state |
| `confirmed` | Seller has accepted the order | Seller |
| `preparing` | Seller is preparing the goods | Seller |
| `ready` | Ready for rider pickup; creates a `Delivery` | Seller |
| `in_transit` | Rider has picked up (also set automatically when the linked delivery is marked `picked_up`) | Seller, or automatically via delivery pickup |
| `delivered` | Goods delivered to buyer (also set automatically when the linked delivery is marked `delivered`) | Seller, or automatically via delivery completion |
| `cancelled` | Order cancelled | Seller only (no buyer-initiated cancellation exists) |

There is no `rejected`, `accepted`, or `dispatched` status — the actual choices are exactly the seven above (`apps/orders/models.py`, `Order.Status`).

---

## 🛵 Delivery Endpoints

All delivery/rider endpoints live under `/delivery/`. The paths differ substantially from `/delivery/jobs/...`.

### POST `/delivery/profile/`

Rider sets up their profile (one-time, after registering with `role: "rider"`).

**Authentication:** Required (rider)
**Response shape:** `status`/`message`/`data` envelope

**Request Body:**

```json
{
  "is_available": true,
  "current_latitude": 6.4541,
  "current_longitude": 3.3947
}
```

---

### GET `/delivery/profile/me/`

Get the authenticated rider's own profile.

**Authentication:** Required (rider)
**Response shape:** Plain DRF representation (unwrapped)

**Success Response (200):**

```json
{
  "id": 3,
  "username": "chuka_rider",
  "phone_number": "+2348012345678",
  "is_available": true,
  "current_latitude": "6.454100",
  "current_longitude": "3.394700",
  "total_deliveries": 118,
  "rating": "4.90",
  "created_at": "2026-01-10T10:00:00Z"
}
```

---

### PATCH `/delivery/profile/me/`

Update the rider's own profile — this is also how a rider goes online/offline: set `is_available` here.

**Authentication:** Required (rider)

**Request Body:** Any subset of `is_available`, `current_latitude`, `current_longitude`
**Success Response (200):** Updated rider profile, unwrapped

There is no separate `POST /delivery/availability/` endpoint.

---

### GET `/delivery/available/`

List deliveries that are pending assignment (available for any rider to claim).

**Authentication:** Required (rider)
**Response shape:** Paginated (`count`/`next`/`previous`/`results`)

There are **no** `lat`/`lng`/`radius_km` query parameters — this list is simply every `Delivery` with `status='pending'`, with no proximity filtering.

**Success Response (200):**

```json
{
  "count": 4,
  "next": null,
  "previous": null,
  "results": [
    {
      "id": 19,
      "order": "9d3f7b1e-...-uuid",
      "order_total": "84500.00",
      "rider": null,
      "rider_name": null,
      "status": "pending",
      "current_latitude": null,
      "current_longitude": null,
      "delivery_address": {
        "street": "14 Akin Street",
        "city": "Lagos",
        "state": "Lagos State"
      },
      "assigned_at": null,
      "picked_up_at": null,
      "delivered_at": null,
      "created_at": "2026-06-15T10:40:00Z"
    }
  ]
}
```

There is no `pickup`/business-name, `estimated_earnings`, `estimated_duration_min`, or `package_count` field.

---

### GET `/delivery/active/`

List the authenticated rider's currently active deliveries (`status` in `assigned` or `picked_up`).

**Authentication:** Required (rider)
**Response shape:** Paginated

---

### GET `/delivery/<id>/`

Get details of a specific delivery, restricted to the rider currently assigned to it.

**Authentication:** Required (rider, must be the assigned rider — otherwise `404`)

---

### POST `/delivery/<id>/accept/`

Accept/claim an available delivery job.

**Authentication:** Required (rider)
**Response shape:** `status`/`message`/`data` envelope

**Request Body:** None

**Success Response (200):**

```json
{
  "status": "success",
  "message": "Delivery accepted.",
  "data": { ... }
}
```

**Possible Errors:**
- `404` — Delivery not found, or already claimed by someone else
- `400` — Rider has no rider profile, or the rider's `is_available` is `false`

---

### PATCH `/delivery/<id>/status/`

Update a delivery's status. This replaces the separately-documented `picked-up`/`delivered` endpoints — there is a single generic status route.

**Authentication:** Required (assigned rider only)
**Response shape:** `status`/`message`/`data` envelope

**Request Body:**

```json
{
  "status": "picked_up"
}
```

Valid `Delivery.Status` values are `pending`, `assigned`, `picked_up`, `delivered`, `failed`. There is **no 4-digit buyer confirmation code** anywhere in the codebase — marking a delivery `delivered` requires no code from the buyer.

**Side Effects:**
- Marking a delivery `picked_up` (while the order is `ready`) also moves the linked `Order` to `in_transit`.
- Marking a delivery `delivered` (while the order is `in_transit`) also moves the linked `Order` to `delivered`, and increments the rider's `total_deliveries`.
- Marking `assigned → failed` resets the delivery back to `pending` (unassigning the rider) so another rider can claim it, rather than leaving it permanently stuck.

**Success Response (200):**

```json
{
  "status": "success",
  "message": "Delivery status updated to 'picked_up'.",
  "data": { ... }
}
```

---

### PATCH `/delivery/location/`

Update the rider's live GPS location.

**Authentication:** Required (rider)
**Response shape:** `status`/`message` envelope (no `data`)

**Request Body:**

```json
{
  "latitude": 6.4541,
  "longitude": 3.3947
}
```

This updates both the rider's profile coordinates and, if the rider has a delivery currently `picked_up`, that delivery's live coordinates too.

**Success Response (200):**

```json
{
  "status": "success",
  "message": "Location updated."
}
```

---

There is currently **no rider earnings endpoint** (`GET /delivery/earnings/` does not exist — there's no earnings/payout tracking anywhere in the `delivery` app).

---

## 🔔 Notification Endpoints

### POST `/users/fcm-token/`

Register (or overwrite) the caller's FCM device token for push notifications. This lives under `/users/`, not `/notifications/`.

**Authentication:** Required
**Response shape:** `status`/`message` envelope (no `data`)

**Request Body:**

```json
{
  "fcm_token": "fcm-device-token..."
}
```

There is only a single `fcm_token` field per user (`User.fcm_token`) — there's no `device_type` or `device_id`, no multi-device support, and no corresponding "unregister" endpoint.

**Success Response (200):**

```json
{
  "status": "success",
  "message": "Device registered for notifications."
}
```

---

### GET `/notifications/`

Get the user's notification history.

**Authentication:** Required
**Response shape:** Paginated (`count`/`next`/`previous`/`results`)

**Success Response (200):**

```json
{
  "count": 5,
  "next": null,
  "previous": null,
  "results": [
    {
      "id": 88,
      "title": "Order Update",
      "body": "Eze Bulk Traders has confirmed your order.",
      "notification_type": "order_confirmed",
      "data": {
        "order_id": "9d3f7b1e-...-uuid"
      },
      "is_read": false,
      "created_at": "2026-06-15T10:32:00Z"
    }
  ]
}
```

Field names are `body` (not `message`) and `is_read` (not `read`).

---

### GET `/notifications/unread/`

Return the count of unread notifications for the authenticated user.

**Authentication:** Required
**Response shape:** `status`/`data` envelope

**Success Response (200):**

```json
{
  "status": "success",
  "data": { "unread_count": 3 }
}
```

---

### PATCH `/notifications/<id>/read/`

Mark a single notification as read.

**Authentication:** Required
**Response shape:** `status`/`message` envelope — the updated notification object is **not** returned in the response.

**Success Response (200):**

```json
{
  "status": "success",
  "message": "Notification marked as read."
}
```

---

### PATCH `/notifications/mark-all-read/`

Mark all of the user's notifications as read.

**Authentication:** Required
**Response shape:** `status`/`message` envelope — no count of how many were marked is returned.

**Success Response (200):**

```json
{
  "status": "success",
  "message": "All notifications marked as read."
}
```

---

## 🪝 Webhook Endpoints

**Not implemented.** There is no `apps.webhooks` app, and `config/urls.py` wires up no `/webhooks/` prefix at all. There is no Supabase webhook receiver and no FCM delivery-callback endpoint anywhere in the current backend.

---

## ⏱ Rate Limiting

**Not implemented.** `REST_FRAMEWORK` in `backend/config/settings/base.py` sets no `DEFAULT_THROTTLE_CLASSES`/`DEFAULT_THROTTLE_RATES`, and there is no other throttling middleware in the codebase. No endpoint currently returns `429`, and no `X-RateLimit-*` headers are sent. The tables below describe an intended/future policy, not current behavior.

### Default Limits (planned, not yet enforced)

| Endpoint Type | Limit |
|--------------|-------|
| Auth endpoints | 5 requests/minute |
| Read endpoints (GET) | 100 requests/minute |
| Write endpoints (POST, PATCH, DELETE) | 30 requests/minute |

---

## 📄 Pagination

All list endpoints are paginated via DRF's `PageNumberPagination` (`apps/common/pagination.py`, wired globally as `DEFAULT_PAGINATION_CLASS`).

### Request Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | 1 | Page number (1-indexed) |
| `page_size` | int | 20 | Items per page (max: 100) |

### Response Format

```json
{
  "count": 87,
  "next": "http://localhost:8000/api/v1/products/?page=2",
  "previous": null,
  "results": [ ... ]
}
```

There is no `success`, `data`, or separate `pagination` object — this **is** the whole response body for a list endpoint. `next`/`previous` are full URLs (or `null`), not page numbers.

### Example Request

```http
GET /api/v1/products/?page=2&page_size=50
```

---

## 🔍 Filtering & Sorting

Support varies per endpoint — only where a view explicitly declares `filterset_class`/`filterset_fields`, `search_fields`, or `ordering_fields` do the globally-enabled `DjangoFilterBackend`, `SearchFilter`, and `OrderingFilter` actually do anything. Today that's `GET /products/` (see [Product Endpoints](#-product-endpoints) for its exact filters); other list endpoints (orders, notifications, deliveries) only support `page`/`page_size`.

### Filtering (products)

```http
GET /api/v1/products/?category=grains-cereals&is_available=true&min_price=1000
```

### Sorting (products)

Use DRF's `ordering` parameter, not `sort_by`/`order`:

```http
GET /api/v1/products/?ordering=price
GET /api/v1/products/?ordering=-price
```

### Searching (products)

```http
GET /api/v1/products/?search=rice
```

---

## 📡 Real-Time Subscriptions

For real-time updates, use Supabase Realtime WebSocket subscriptions directly from the client (not through this API). This is independent of the Django REST endpoints documented above and can't be verified against `backend/apps/*`.

### Channels

| Channel | Subscribe For |
|---------|---------------|
| `order:<order_id>` | Order status changes |
| `delivery:<delivery_id>` | Delivery progress updates |
| `seller:<seller_id>:orders` | New incoming orders (seller) |
| `rider:<rider_id>:jobs` | Available jobs (rider) |

### Example (Android SDK)

```kotlin
supabaseClient.realtime.channel("order:order-uuid")
    .on(eventName = "UPDATE") { event ->
        // Handle order status change
    }
    .subscribe()
```

---

## 🧪 Testing the API

### Postman Collection

Import the Postman collection at:

```
docs/postman/BulkBasket.postman_collection.json
```

It includes:
- All endpoints with example requests
- Environment variables for dev/staging/prod
- Authentication helpers
- Test scripts for response validation

### Example cURL Requests

```bash
# Login
curl -X POST http://localhost:8000/api/v1/users/login/ \
  -H "Content-Type: application/json" \
  -d '{"username":"buyer_test","password":"Test123!"}'

# Get nearby sellers (public, no auth required)
curl -X GET "http://localhost:8000/api/v1/sellers/nearby/?lat=6.4541&lng=3.3947" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Create an order
curl -X POST http://localhost:8000/api/v1/orders/ \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "seller_id": 7,
    "delivery_address_id": 3,
    "items": [{"product_id": 12, "quantity": 2}]
  }'
```

---

## 📞 Support

For API questions or issues:

- **Internal:** Post in team chat or open a GitHub issue
- **Bug reports:** Use the bug report template in GitHub
- **Documentation issues:** Open a PR to update this file

---

## 📜 Versioning Policy

We use URL-based API versioning:

- Current version: `v1` (`/api/v1/`)
- Breaking changes will introduce a new version (`/api/v2/`)
- Old versions are supported for at least 6 months after a new version is released
- Deprecation notices appear in the `Sunset` response header

---

**API Version:** v1.0  
**Last Updated:** September 2026  
**Maintained by:** The BulkBasket Project Team
