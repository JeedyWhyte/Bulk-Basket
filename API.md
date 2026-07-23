# BulkBasket API Documentation

Complete reference for the BulkBasket REST API.

**Base URL:**
- Development: `http://localhost:8000/api/v1/`
- Staging: `https://staging-api.bulkbasket.app/api/v1/`
- Production: `https://api.bulkbasket.app/api/v1/`

**Authentication:** Bearer JWT (issued by Supabase Auth)  
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

All endpoints (except `/auth/signup/`, `/auth/login/`, `/auth/refresh/`) require a valid JWT token in the `Authorization` header:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token Lifetime

- **Access token:** 1 hour
- **Refresh token:** 30 days

### Token Refresh Flow

When the access token expires:

1. Send the refresh token to `/auth/refresh/`
2. Receive a new access token (and optionally a new refresh token)
3. Retry the original request with the new token

```bash
# Example: Refresh an expired token
curl -X POST https://api.bulkbasket.app/api/v1/auth/refresh/ \
  -H "Content-Type: application/json" \
  -d '{"refresh_token": "eyJhbGc..."}'
```

---

## 🔄 Common Patterns

### Request Headers

All requests should include:

```http
Content-Type: application/json
Authorization: Bearer <jwt_token>
X-Request-ID: <optional-uuid-for-tracing>
Accept-Language: en-NG
```

### Response Format

All responses follow this envelope:

```json
{
  "success": true,
  "data": { ... },
  "meta": {
    "request_id": "uuid",
    "timestamp": "2026-06-15T10:32:00Z"
  }
}
```

For paginated responses:

```json
{
  "success": true,
  "data": [...],
  "pagination": {
    "page": 1,
    "page_size": 20,
    "total_pages": 5,
    "total_count": 87,
    "has_next": true,
    "has_previous": false
  },
  "meta": { ... }
}
```

---

## ❌ Error Responses

### Error Schema

All errors follow this format:

```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Human-readable error message",
    "details": {
      "field_name": ["specific error about this field"]
    }
  },
  "meta": {
    "request_id": "uuid",
    "timestamp": "2026-06-15T10:32:00Z"
  }
}
```

### HTTP Status Codes

| Code | Meaning | When Used |
|------|---------|-----------|
| `200` | OK | Successful GET, PATCH, PUT |
| `201` | Created | Successful POST creating a resource |
| `204` | No Content | Successful DELETE |
| `400` | Bad Request | Invalid request body or parameters |
| `401` | Unauthorized | Missing or invalid JWT |
| `403` | Forbidden | Authenticated but lacking permission |
| `404` | Not Found | Resource doesn't exist |
| `409` | Conflict | Resource conflict (e.g., duplicate) |
| `422` | Unprocessable Entity | Validation failed |
| `429` | Too Many Requests | Rate limit exceeded |
| `500` | Internal Server Error | Server-side issue |
| `503` | Service Unavailable | Maintenance or overload |

### Common Error Codes

| Code | Message | HTTP Status |
|------|---------|-------------|
| `AUTH_INVALID_TOKEN` | Invalid or expired JWT | 401 |
| `AUTH_MISSING_TOKEN` | No authorization header | 401 |
| `PERMISSION_DENIED` | Not authorized for this resource | 403 |
| `RESOURCE_NOT_FOUND` | Resource does not exist | 404 |
| `VALIDATION_ERROR` | Field validation failed | 422 |
| `DUPLICATE_RESOURCE` | Resource already exists | 409 |
| `OUT_OF_STOCK` | Product insufficient stock | 422 |
| `INVALID_ORDER_STATUS` | Status transition not allowed | 422 |
| `RATE_LIMIT_EXCEEDED` | Too many requests | 429 |
| `INTERNAL_ERROR` | Unexpected server error | 500 |

### Example Error Response

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed for the request",
    "details": {
      "email": ["This field is required"],
      "password": ["Password must be at least 8 characters"]
    }
  },
  "meta": {
    "request_id": "abc-123",
    "timestamp": "2026-06-15T10:32:00Z"
  }
}
```

---

## 🔑 Auth Endpoints

### POST `/auth/signup/`

Register a new user account.

**Authentication:** None required

**Request Body:**

```json
{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "user_type": "buyer",
  "full_name": "Jane Doe",
  "phone": "+2348012345678"
}
```

**Field Validations:**
- `email` — Required, valid email format, unique
- `password` — Required, min 8 chars, must include letter and number
- `user_type` — Required, one of: `buyer`, `seller`, `rider`
- `full_name` — Required, 2-100 characters
- `phone` — Optional, valid international format

**Success Response (201):**

```json
{
  "success": true,
  "data": {
    "user_id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "user_type": "buyer",
    "access_token": "eyJhbGc...",
    "refresh_token": "eyJhbGc...",
    "expires_in": 3600,
    "verification_required": true
  }
}
```

**Possible Errors:**
- `400` — Invalid request body
- `409` — Email already registered
- `422` — Validation failed

---

### POST `/auth/login/`

Authenticate with email and password.

**Authentication:** None required

**Request Body:**

```json
{
  "email": "user@example.com",
  "password": "SecurePass123!"
}
```

**Success Response (200):**

```json
{
  "success": true,
  "data": {
    "user_id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "user_type": "buyer",
    "full_name": "Jane Doe",
    "access_token": "eyJhbGc...",
    "refresh_token": "eyJhbGc...",
    "expires_in": 3600
  }
}
```

**Possible Errors:**
- `401` — Invalid credentials
- `403` — Account suspended
- `429` — Too many login attempts

---

### POST `/auth/refresh/`

Refresh an expired access token.

**Authentication:** None (uses refresh token)

**Request Body:**

```json
{
  "refresh_token": "eyJhbGc..."
}
```

**Success Response (200):**

```json
{
  "success": true,
  "data": {
    "access_token": "eyJhbGc...",
    "refresh_token": "eyJhbGc...",
    "expires_in": 3600
  }
}
```

---

### POST `/auth/logout/`

Invalidate the current session.

**Authentication:** Required

**Request Body:** None

**Success Response (204):** No content

---

### POST `/auth/forgot-password/`

Request a password reset email.

**Authentication:** None required

**Request Body:**

```json
{
  "email": "user@example.com"
}
```

**Success Response (200):**

```json
{
  "success": true,
  "data": {
    "message": "Password reset email sent"
  }
}
```

---

### POST `/auth/reset-password/`

Reset password using a reset token from email.

**Authentication:** None required

**Request Body:**

```json
{
  "reset_token": "abc123...",
  "new_password": "NewSecurePass456!"
}
```

**Success Response (200):**

```json
{
  "success": true,
  "data": {
    "message": "Password reset successful"
  }
}
```

---

## 👤 User Endpoints

### GET `/users/me/`

Get the authenticated user's profile.

**Authentication:** Required

**Success Response (200):**

```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "user_type": "buyer",
    "full_name": "Jane Doe",
    "phone": "+2348012345678",
    "avatar_url": "https://...",
    "addresses": [
      {
        "id": "addr-uuid",
        "label": "Home",
        "street": "14 Akin Street",
        "city": "Lagos",
        "lat": 6.4541,
        "lng": 3.3947,
        "is_default": true
      }
    ],
    "created_at": "2026-06-15T10:00:00Z"
  }
}
```

---

### PATCH `/users/me/`

Update authenticated user's profile.

**Authentication:** Required

**Request Body:** (any subset of these fields)

```json
{
  "full_name": "Jane Smith",
  "phone": "+2348012345678",
  "avatar_url": "https://..."
}
```

**Success Response (200):** Updated user object

---

### POST `/users/me/addresses/`

Add a delivery address.

**Authentication:** Required

**Request Body:**

```json
{
  "label": "Office",
  "street": "10 Marina",
  "city": "Lagos",
  "state": "Lagos State",
  "country": "Nigeria",
  "lat": 6.4500,
  "lng": 3.3800,
  "is_default": false
}
```

**Success Response (201):** Created address object

---

### DELETE `/users/me/addresses/<id>/`

Remove a delivery address.

**Authentication:** Required  
**Success Response (204):** No content

---

## 🏪 Seller Endpoints

### GET `/sellers/nearby/`

List sellers near a location.

**Authentication:** Required

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `lat` | float | Yes | Buyer's latitude |
| `lng` | float | Yes | Buyer's longitude |
| `radius_km` | float | No | Search radius (default: 5km) |
| `category` | string | No | Filter by product category |
| `page` | int | No | Page number (default: 1) |
| `page_size` | int | No | Results per page (default: 20, max: 50) |

**Example Request:**

```http
GET /api/v1/sellers/nearby/?lat=6.4541&lng=3.3947&radius_km=5
```

**Success Response (200):**

```json
{
  "success": true,
  "data": [
    {
      "id": "seller-uuid",
      "business_name": "Mama Chuka's Store",
      "address": "Lagos Island Market",
      "lat": 6.4550,
      "lng": 3.3950,
      "distance_km": 0.8,
      "rating": 4.8,
      "review_count": 142,
      "is_verified": true,
      "categories": ["grains", "produce"],
      "image_url": "https://...",
      "is_open": true
    }
  ],
  "pagination": { ... }
}
```

---

### GET `/sellers/<id>/`

Get detailed seller information.

**Authentication:** Required

**Success Response (200):**

```json
{
  "success": true,
  "data": {
    "id": "seller-uuid",
    "business_name": "Mama Chuka's Store",
    "description": "Family-owned bulk food store since 1985",
    "address": "Lagos Island Market, Lagos",
    "phone": "+2348012345678",
    "lat": 6.4550,
    "lng": 3.3950,
    "rating": 4.8,
    "review_count": 142,
    "is_verified": true,
    "is_open": true,
    "opening_hours": {
      "monday": "08:00-18:00",
      "tuesday": "08:00-18:00",
      "sunday": "closed"
    },
    "categories": ["grains", "produce", "spices"],
    "image_url": "https://...",
    "product_count": 62,
    "joined_date": "2026-01-15"
  }
}
```

---

### GET `/sellers/<id>/products/`

List a seller's products.

**Authentication:** Required

**Query Parameters:** Standard pagination + `category`, `sort_by`

**Success Response (200):** Paginated list of products

---

### PATCH `/sellers/me/`

Update authenticated seller's profile.

**Authentication:** Required (seller only)

**Request Body:** Same fields as seller object  
**Success Response (200):** Updated seller object

---

## 📦 Product Endpoints

### GET `/products/`

List all products with filtering.

**Authentication:** Required

**Query Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `category` | string | Filter by category |
| `min_price` | decimal | Minimum price filter |
| `max_price` | decimal | Maximum price filter |
| `in_stock` | boolean | Only show in-stock products |
| `seller_id` | UUID | Filter by specific seller |
| `sort_by` | string | `price`, `name`, `created_at` |
| `order` | string | `asc` or `desc` (default: `desc`) |
| `page` | int | Page number |
| `page_size` | int | Results per page |

**Success Response (200):**

```json
{
  "success": true,
  "data": [
    {
      "id": "prod-uuid",
      "name": "Long Grain Parboiled Rice",
      "description": "Premium quality rice, 50kg bag",
      "price": 42000.00,
      "currency": "NGN",
      "unit": "50kg bag",
      "stock_qty": 18,
      "category": "grains",
      "image_url": "https://...",
      "seller": {
        "id": "seller-uuid",
        "business_name": "Eze Bulk Traders",
        "is_verified": true
      },
      "created_at": "2026-06-10T10:00:00Z"
    }
  ],
  "pagination": { ... }
}
```

---

### GET `/products/search/`

Search products by keyword.

**Authentication:** Required

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `q` | string | Yes | Search query |
| `lat` | float | No | Filter by proximity |
| `lng` | float | No | Filter by proximity |
| `category` | string | No | Filter by category |

**Example Request:**

```http
GET /api/v1/products/search/?q=rice&lat=6.4541&lng=3.3947
```

**Success Response (200):** Same format as GET `/products/`

---

### GET `/products/<id>/`

Get product details.

**Authentication:** Required

**Success Response (200):** Product object with full details

---

### POST `/products/`

Create a new product (seller only).

**Authentication:** Required (seller)

**Request Body:**

```json
{
  "name": "Long Grain Rice",
  "description": "Premium quality, harvested 2026",
  "price": 42000.00,
  "unit": "50kg bag",
  "stock_qty": 20,
  "category": "grains",
  "image_url": "https://storage.supabase.co/..."
}
```

**Success Response (201):** Created product object

---

### PATCH `/products/<id>/`

Update a product (seller only, must own the product).

**Authentication:** Required

**Request Body:** Any subset of product fields  
**Success Response (200):** Updated product object

---

### DELETE `/products/<id>/`

Delete a product (seller only).

**Authentication:** Required  
**Success Response (204):** No content

---

### GET `/products/categories/`

List all product categories.

**Authentication:** Required

**Success Response (200):**

```json
{
  "success": true,
  "data": [
    {
      "id": "grains",
      "name": "Grains & Cereals",
      "icon": "wheat",
      "product_count": 142
    },
    {
      "id": "produce",
      "name": "Fresh Produce",
      "icon": "plant",
      "product_count": 89
    }
  ]
}
```

---

## 🛒 Order Endpoints

### POST `/orders/`

Place a new order (buyer only).

**Authentication:** Required (buyer)

**Request Body:**

```json
{
  "seller_id": "seller-uuid",
  "delivery_address_id": "addr-uuid",
  "items": [
    {
      "product_id": "prod-uuid",
      "quantity": 2
    },
    {
      "product_id": "prod-uuid-2",
      "quantity": 1
    }
  ],
  "delivery_notes": "Please call upon arrival",
  "payment_method": "cash_on_delivery"
}
```

**Success Response (201):**

```json
{
  "success": true,
  "data": {
    "id": "order-uuid",
    "order_number": "BK-20840",
    "status": "pending",
    "buyer": { ... },
    "seller": { ... },
    "items": [
      {
        "product": { ... },
        "quantity": 2,
        "price_at_purchase": 42000.00,
        "subtotal": 84000.00
      }
    ],
    "subtotal": 90500.00,
    "delivery_fee": 1500.00,
    "total_amount": 92000.00,
    "delivery_address": { ... },
    "delivery_notes": "Please call upon arrival",
    "payment_method": "cash_on_delivery",
    "created_at": "2026-06-15T10:32:00Z",
    "estimated_delivery_time": "2026-06-15T11:30:00Z"
  }
}
```

**Possible Errors:**
- `400` — Invalid request
- `404` — Product/seller not found
- `409` — One or more products out of stock
- `422` — Validation error

---

### GET `/orders/`

List the authenticated user's orders.

**Authentication:** Required

**Query Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `status` | string | Filter by order status |
| `start_date` | date | Orders after this date |
| `end_date` | date | Orders before this date |
| `page` | int | Page number |
| `page_size` | int | Results per page |

**Success Response (200):** Paginated list of orders

---

### GET `/orders/<id>/`

Get detailed order information.

**Authentication:** Required (buyer, seller, or assigned rider)

**Success Response (200):** Order object with full details

---

### PATCH `/orders/<id>/accept/`

Accept an incoming order (seller only).

**Authentication:** Required (seller)

**Request Body:** None  
**Success Response (200):** Updated order with `status: "accepted"`

**Side Effects:**
- FCM notification sent to buyer
- Realtime event broadcast to all subscribers

---

### PATCH `/orders/<id>/reject/`

Reject an incoming order (seller only).

**Authentication:** Required (seller)

**Request Body:**

```json
{
  "reason": "Out of stock"
}
```

**Success Response (200):** Updated order with `status: "rejected"`

---

### PATCH `/orders/<id>/ready/`

Mark order as ready for pickup.

**Authentication:** Required (seller)

**Success Response (200):** Updated order with `status: "ready"`

**Side Effects:**
- Dispatch system searches for available rider
- FCM notification to buyer

---

### PATCH `/orders/<id>/cancel/`

Cancel an order (buyer only, only if status is `pending` or `accepted`).

**Authentication:** Required (buyer who placed order)

**Request Body:**

```json
{
  "reason": "Changed my mind"
}
```

**Success Response (200):** Updated order with `status: "cancelled"`

---

### Order Status Lifecycle

```
pending → accepted → preparing → ready → dispatched → delivered
   ↓         ↓
rejected  cancelled
```

| Status | Description | Who Can Trigger |
|--------|-------------|-----------------|
| `pending` | Order placed, awaiting seller response | Initial state |
| `accepted` | Seller has accepted the order | Seller |
| `rejected` | Seller declined the order | Seller |
| `preparing` | Seller is preparing the goods | Seller |
| `ready` | Ready for rider pickup | Seller |
| `dispatched` | Rider has picked up | Rider |
| `delivered` | Goods delivered to buyer | Rider |
| `cancelled` | Buyer cancelled before fulfillment | Buyer |

---

## 🛵 Delivery Endpoints

### GET `/delivery/jobs/`

List available delivery jobs (rider only).

**Authentication:** Required (rider)

**Query Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `lat` | float | Rider's current latitude |
| `lng` | float | Rider's current longitude |
| `radius_km` | float | Max distance for jobs |

**Success Response (200):**

```json
{
  "success": true,
  "data": [
    {
      "id": "delivery-uuid",
      "order_number": "BK-20840",
      "pickup": {
        "address": "Lagos Island Market",
        "lat": 6.4550,
        "lng": 3.3950,
        "business_name": "Eze Bulk Traders"
      },
      "dropoff": {
        "address": "14 Akin Street, Lagos",
        "lat": 6.4541,
        "lng": 3.3947
      },
      "distance_km": 2.3,
      "estimated_earnings": 1500.00,
      "estimated_duration_min": 25,
      "package_count": 3
    }
  ]
}
```

---

### POST `/delivery/jobs/<id>/accept/`

Accept a delivery job.

**Authentication:** Required (rider)

**Request Body:** None  
**Success Response (200):**

```json
{
  "success": true,
  "data": {
    "delivery_id": "delivery-uuid",
    "order_number": "BK-20840",
    "status": "assigned",
    "pickup_address": { ... },
    "dropoff_address": { ... },
    "buyer_phone": "+2348012345678",
    "seller_phone": "+2348087654321"
  }
}
```

---

### PATCH `/delivery/<id>/picked-up/`

Mark goods picked up from seller.

**Authentication:** Required (assigned rider)

**Success Response (200):** Updated delivery with `status: "in_transit"`

---

### PATCH `/delivery/<id>/delivered/`

Mark goods delivered to buyer.

**Authentication:** Required (assigned rider)

**Request Body:**

```json
{
  "confirmation_code": "1234"
}
```

The buyer confirms delivery with a 4-digit code shown in their app.

**Success Response (200):** Updated delivery with `status: "delivered"`

---

### GET `/delivery/earnings/`

Get rider earnings summary.

**Authentication:** Required (rider)

**Query Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `period` | string | `today`, `week`, `month`, `all` |
| `start_date` | date | Custom start date |
| `end_date` | date | Custom end date |

**Success Response (200):**

```json
{
  "success": true,
  "data": {
    "period": "today",
    "total_earnings": 8400.00,
    "total_deliveries": 12,
    "average_per_delivery": 700.00,
    "deliveries": [
      {
        "delivery_id": "uuid",
        "order_number": "BK-20840",
        "earnings": 1500.00,
        "completed_at": "2026-06-15T10:32:00Z"
      }
    ]
  }
}
```

---

### POST `/delivery/availability/`

Set rider availability (online/offline).

**Authentication:** Required (rider)

**Request Body:**

```json
{
  "is_online": true,
  "current_location": {
    "lat": 6.4541,
    "lng": 3.3947
  }
}
```

**Success Response (200):**

```json
{
  "success": true,
  "data": {
    "is_online": true,
    "available_for_jobs": true
  }
}
```

---

## 🔔 Notification Endpoints

### POST `/notifications/register-device/`

Register an FCM device token for push notifications.

**Authentication:** Required

**Request Body:**

```json
{
  "fcm_token": "fcm-device-token...",
  "device_type": "android",
  "device_id": "unique-device-id"
}
```

**Success Response (201):** Device registered

---

### DELETE `/notifications/unregister-device/`

Unregister an FCM device token.

**Authentication:** Required

**Request Body:**

```json
{
  "device_id": "unique-device-id"
}
```

**Success Response (204):** No content

---

### GET `/notifications/`

Get the user's notification history.

**Authentication:** Required

**Success Response (200):**

```json
{
  "success": true,
  "data": [
    {
      "id": "notif-uuid",
      "type": "order_accepted",
      "title": "Order Accepted",
      "message": "Eze Bulk Traders has accepted your order #BK-20840",
      "data": {
        "order_id": "order-uuid"
      },
      "read": false,
      "created_at": "2026-06-15T10:32:00Z"
    }
  ],
  "pagination": { ... }
}
```

---

### PATCH `/notifications/<id>/read/`

Mark a notification as read.

**Authentication:** Required  
**Success Response (200):** Updated notification

---

### PATCH `/notifications/mark-all-read/`

Mark all notifications as read.

**Authentication:** Required  
**Success Response (200):** Count of marked notifications

---

## 🪝 Webhook Endpoints

### POST `/webhooks/supabase/`

Receive webhooks from Supabase (internal use).

**Authentication:** Webhook secret in `X-Webhook-Secret` header

**Request Body:** Supabase webhook payload

---

### POST `/webhooks/fcm-callback/`

Handle FCM delivery callbacks.

**Authentication:** FCM signature verification

---

## ⏱ Rate Limiting

API requests are rate-limited per user.

### Default Limits

| Endpoint Type | Limit |
|--------------|-------|
| Auth endpoints | 5 requests/minute |
| Read endpoints (GET) | 100 requests/minute |
| Write endpoints (POST, PATCH, DELETE) | 30 requests/minute |
| Search endpoints | 30 requests/minute |

### Rate Limit Headers

Every response includes:

```http
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 87
X-RateLimit-Reset: 1718442000
```

### Exceeded Response

When rate limit is exceeded (HTTP 429):

```json
{
  "success": false,
  "error": {
    "code": "RATE_LIMIT_EXCEEDED",
    "message": "Too many requests. Try again in 60 seconds.",
    "details": {
      "retry_after": 60
    }
  }
}
```

---

## 📄 Pagination

All list endpoints support cursor-based or offset-based pagination.

### Request Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | 1 | Page number (1-indexed) |
| `page_size` | int | 20 | Items per page (max: 100) |

### Response Format

```json
{
  "success": true,
  "data": [ ... ],
  "pagination": {
    "page": 1,
    "page_size": 20,
    "total_pages": 5,
    "total_count": 87,
    "has_next": true,
    "has_previous": false,
    "next_page": 2,
    "previous_page": null
  }
}
```

### Example Request

```http
GET /api/v1/products/?page=2&page_size=50
```

---

## 🔍 Filtering & Sorting

### Filtering

Most list endpoints accept query parameters for filtering:

```http
GET /api/v1/products/?category=grains&in_stock=true&min_price=1000
```

### Sorting

Use `sort_by` and `order` parameters:

```http
GET /api/v1/products/?sort_by=price&order=asc
```

### Multiple Filters

Combine filters with `&`:

```http
GET /api/v1/orders/?status=delivered&start_date=2026-06-01&sort_by=created_at&order=desc
```

---

## 📡 Real-Time Subscriptions

For real-time updates, use Supabase Realtime WebSocket subscriptions directly from the client (not through this API).

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
curl -X POST http://localhost:8000/api/v1/auth/login/ \
  -H "Content-Type: application/json" \
  -d '{"email":"buyer@test.com","password":"Test123!"}'

# Get nearby sellers (with auth)
curl -X GET "http://localhost:8000/api/v1/sellers/nearby/?lat=6.4541&lng=3.3947" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Create an order
curl -X POST http://localhost:8000/api/v1/orders/ \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "seller_id": "seller-uuid",
    "delivery_address_id": "addr-uuid",
    "items": [{"product_id": "prod-uuid", "quantity": 2}]
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
**Last Updated:** June 2026  
**Maintained by:** The BulkBasket Project Team