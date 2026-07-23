# BulkBasket Security Documentation

Security is a first-class concern in BulkBasket. This document outlines our security policies, threat model, implementation details, and procedures for handling vulnerabilities.

> **Last Security Review:** June 2026  
> **Next Scheduled Review:** Before v1.0 production release

---

## 📋 Table of Contents

1. [Reporting a Vulnerability](#-reporting-a-vulnerability)
2. [Supported Versions](#-supported-versions)
3. [Security Principles](#-security-principles)
4. [Threat Model](#-threat-model)
5. [Authentication & Authorization](#-authentication--authorization)
6. [Data Protection](#-data-protection)
7. [API Security](#-api-security)
8. [Android App Security](#-android-app-security)
9. [Backend Security](#-backend-security)
10. [Database Security](#-database-security)
11. [Infrastructure Security](#-infrastructure-security)
12. [Secrets Management](#-secrets-management)
13. [Third-Party Dependencies](#-third-party-dependencies)
14. [Incident Response](#-incident-response)
15. [Compliance & Privacy](#-compliance--privacy)
16. [Security Checklist](#-security-checklist)

---

## 🚨 Reporting a Vulnerability

**We take all security reports seriously.** If you discover a vulnerability, please follow the responsible disclosure process below.

### How to Report

**⚠️ DO NOT open a public GitHub issue for security vulnerabilities.**

Instead, please report vulnerabilities through one of these channels:

1. **Email:** `security@bulkbasket.app` (preferred)
2. **Private GitHub Security Advisory:** [Create advisory](https://github.com/<your-org>/bulkbasket/security/advisories/new)
3. **Direct message to Project Lead** (for team members)

### What to Include

Please provide as much detail as possible:

- **Type of vulnerability** (e.g., SQL injection, XSS, authentication bypass)
- **Location** (specific endpoint, screen, or file)
- **Steps to reproduce** with exact commands or actions
- **Impact assessment** (what could an attacker do?)
- **Suggested fix** (if you have one)
- **Your contact information** for follow-up
- **CVE reference** (if applicable)

### Response Timeline

| Timeframe | Action |
|-----------|--------|
| **24 hours** | Acknowledgement of report received |
| **72 hours** | Initial assessment and severity rating |
| **7 days** | Detailed response with mitigation plan |
| **30 days** | Fix deployed for critical issues |
| **90 days** | Fix deployed for medium/low issues |

### Recognition

Security researchers who report valid vulnerabilities will be:
- Credited in `CHANGELOG.md` (unless anonymity is requested)
- Listed in our **Security Hall of Fame** (below)
- Given priority response for future reports

### Security Hall of Fame

*Researchers who have helped improve BulkBasket security will be listed here.*

---

## ✅ Supported Versions

Security updates are provided for the following versions:

| Version | Supported | Status |
|---------|-----------|--------|
| 1.0.x   | ✅ Yes     | Active |
| 0.5.x   | ✅ Yes     | Active |
| 0.4.x   | ⚠️ Limited | Security fixes only |
| < 0.4   | ❌ No      | End of life |

**Note:** As BulkBasket is currently in early academic development, all pre-1.0 versions are considered pre-release. Once v1.0 ships, we will maintain the last 2 major versions.

---

## 🎯 Security Principles

BulkBasket security is built on these core principles:

### 1. **Defense in Depth**
Multiple layers of security controls. If one fails, others catch the issue.

### 2. **Least Privilege**
Users, services, and processes have only the permissions they need — nothing more.

### 3. **Fail Securely**
When errors occur, the system fails in a secure state (denied access, no data leak).

### 4. **Zero Trust**
No user, device, or network is trusted by default. Every request is authenticated and authorized.

### 5. **Privacy by Design**
User data protection is built into the architecture, not added later.

### 6. **Secure by Default**
Default configurations are the most secure. Security requires no action from users.

### 7. **Transparency**
Users know what data we collect, how it's used, and their rights.

---

## 🎯 Threat Model

### Assets We Protect

| Asset | Sensitivity | Impact if Compromised |
|-------|-------------|----------------------|
| User authentication credentials | Critical | Account takeover |
| Personal information (name, phone, address) | High | Privacy violation, harassment |
| Payment information (future) | Critical | Financial fraud |
| Order history | Medium | Privacy violation |
| Seller business data | Medium | Competitive disadvantage |
| Location data (riders) | High | Physical safety risk |
| JWT tokens | Critical | Session hijacking |
| Database credentials | Critical | Full system compromise |

### Threat Actors

We consider the following potential attackers:

| Actor | Motivation | Capability |
|-------|-----------|-----------|
| **Curious User** | Explore what they shouldn't see | Basic technical skills |
| **Malicious User** | Steal data, disrupt service | Moderate skills |
| **Competitor** | Damage business, steal insights | Well-resourced |
| **Cybercriminal** | Financial gain | Advanced skills |
| **Insider Threat** | Various motivations | Full system access |
| **Nation State** | Not applicable (out of scope) | Not defended against |

### Attack Vectors

Common attack surfaces we defend against:

1. **Network attacks** — Man-in-the-middle, packet sniffing
2. **API abuse** — Injection, unauthorized access, rate limit bypass
3. **Client-side attacks** — Reverse engineering, tampering
4. **Social engineering** — Phishing, credential theft
5. **Supply chain** — Malicious dependencies
6. **Physical device access** — Lost/stolen devices
7. **Database attacks** — SQL injection, data exfiltration

---

## 🔐 Authentication & Authorization

### Authentication Flow

BulkBasket uses **Supabase Auth** for identity management, issuing signed JWTs for all authenticated requests.

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │ 1. POST /auth/login/ (email + password)
       ▼
┌─────────────────┐
│  Supabase Auth  │──── Verifies credentials
└──────┬──────────┘
       │ 2. Returns signed JWT + refresh token
       ▼
┌─────────────┐
│   Client    │──── Stores tokens securely
└──────┬──────┘
       │ 3. API request with `Authorization: Bearer <JWT>`
       ▼
┌─────────────────┐
│  Django API     │──── Verifies JWT signature
└──────┬──────────┘     Checks expiry & claims
       │ 4. Processes request
       ▼
    Response
```

### Password Requirements

- **Minimum length:** 8 characters
- **Must contain:** At least 1 letter, 1 number
- **Recommended:** Symbols, mixed case, 12+ characters
- **Prohibited:** Common passwords (checked against known breach lists)
- **Storage:** Bcrypt hashing with salt (handled by Supabase)

**Enforcement:**

```python
# backend/apps/users/validators.py
def validate_password_strength(password: str) -> None:
    if len(password) < 8:
        raise ValidationError("Password must be at least 8 characters")
    if not any(c.isdigit() for c in password):
        raise ValidationError("Password must contain at least 1 number")
    if not any(c.isalpha() for c in password):
        raise ValidationError("Password must contain at least 1 letter")
    if password.lower() in COMMON_PASSWORDS:
        raise ValidationError("This password is too common")
```

### JWT Token Management

**Token Structure:**

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "user-uuid",
    "email": "user@example.com",
    "user_type": "buyer",
    "iat": 1718442000,
    "exp": 1718445600,
    "iss": "supabase"
  },
  "signature": "..."
}
```

**Token Lifetimes:**
- **Access token:** 1 hour (short-lived to limit damage if leaked)
- **Refresh token:** 30 days (long-lived, stored securely)

**Verification (Backend):**

```python
# Every request verifies the JWT
def verify_jwt(token: str) -> dict:
    try:
        payload = jwt.decode(
            token,
            settings.SUPABASE_JWT_SECRET,
            algorithms=["HS256"],
            options={"require": ["exp", "sub", "user_type"]}
        )
        return payload
    except jwt.ExpiredSignatureError:
        raise AuthenticationFailed("Token expired")
    except jwt.InvalidTokenError:
        raise AuthenticationFailed("Invalid token")
```

### Multi-Factor Authentication (MFA)

**Current status:** Not implemented in v1.0  
**Planned:** v1.2 (post-academic)

**Planned methods:**
- SMS OTP for high-value actions (order > ₦100,000)
- TOTP (Google Authenticator) for seller accounts
- Email verification for password reset

### Role-Based Access Control (RBAC)

Three primary user roles with distinct permissions:

| Role | Capabilities |
|------|-------------|
| **Buyer** | Browse, order, track, review |
| **Seller** | List products, manage orders, view sales |
| **Rider** | Accept jobs, update delivery status |
| **Admin** | Full access (internal only) |

**Enforcement in Django:**

```python
# backend/apps/orders/permissions.py
class IsOrderOwner(BasePermission):
    def has_object_permission(self, request, view, order):
        user = request.user
        return (
            order.buyer_id == user.id or
            (user.user_type == "seller" and order.seller.user_id == user.id) or
            (user.user_type == "rider" and order.delivery.rider_id == user.id)
        )
```

**Enforcement in Database (RLS):**

```sql
-- Users can only see their own orders
CREATE POLICY "buyers_see_own_orders" ON orders
    FOR SELECT USING (buyer_id = auth.uid());

-- Sellers can see orders for their business
CREATE POLICY "sellers_see_business_orders" ON orders
    FOR SELECT USING (
        seller_id IN (
            SELECT id FROM sellers WHERE user_id = auth.uid()
        )
    );
```

---

## 🔒 Data Protection

### Data Classification

We classify data into three tiers:

| Tier | Description | Examples |
|------|-------------|----------|
| **Public** | Non-sensitive, publicly visible | Product listings, seller names |
| **Private** | User-specific, requires authentication | Order history, addresses |
| **Sensitive** | Highly protected, encrypted | Passwords, payment info (future) |

### Encryption

#### In Transit
All data in transit is encrypted using **TLS 1.3**:
- Client ↔ Backend: HTTPS only
- Backend ↔ Database: SSL/TLS required
- Backend ↔ Supabase: HTTPS with certificate pinning
- Backend ↔ Redis: TLS-enabled Redis
- Backend ↔ FCM: HTTPS

**Enforcement:**

```python
# backend/config/settings/production.py
SECURE_SSL_REDIRECT = True
SECURE_HSTS_SECONDS = 31536000  # 1 year
SECURE_HSTS_INCLUDE_SUBDOMAINS = True
SECURE_HSTS_PRELOAD = True
SESSION_COOKIE_SECURE = True
CSRF_COOKIE_SECURE = True
```

#### At Rest
Sensitive data is encrypted at rest:
- **Database:** PostgreSQL encryption (handled by Supabase)
- **File storage:** Supabase Storage with server-side encryption
- **Backups:** Encrypted with AES-256
- **Local Android storage:** EncryptedSharedPreferences

### Personally Identifiable Information (PII)

**PII we collect:**
- Full name
- Email address
- Phone number
- Physical address
- GPS location (during delivery)
- Device information

**PII protection measures:**
- ✅ Never logged in application logs
- ✅ Never exposed in URLs or query parameters
- ✅ Encrypted in transit and at rest
- ✅ Access restricted via RLS policies
- ✅ Excluded from analytics events
- ✅ Redacted in error reports

**Example — safe logging:**

```python
# ❌ BAD — logs PII
logger.info(f"Order placed by {user.email} at {user.address}")

# ✅ GOOD — logs only IDs
logger.info(f"Order placed by user_id={user.id}", extra={"order_id": order.id})
```

### Data Retention

| Data Type | Retention Period | Deletion Method |
|-----------|-----------------|-----------------|
| Active user accounts | Indefinite | User-initiated deletion |
| Deleted user accounts | 30 days (soft delete), then purged | Hard delete after grace period |
| Order history | 7 years (regulatory) | Archived after 2 years |
| Session tokens | Expires per token lifetime | Automatic |
| Application logs | 90 days | Automatic rotation |
| Security logs | 1 year | Automatic rotation |
| Backups | 30 days rolling | Automatic |

### Right to Deletion

Users can request account deletion via:
1. In-app: **Settings → Delete Account**
2. Email: `privacy@bulkbasket.app`

**Deletion process:**
1. Immediate: Account marked as deleted, login disabled
2. 30 days: Grace period for restoration
3. Day 30: Personal data purged; order records anonymized for regulatory compliance

---

## 🌐 API Security

### Transport Security

- **HTTPS only** — HTTP requests are redirected to HTTPS
- **HSTS enabled** — Browsers refuse HTTP after first visit
- **Certificate pinning** — Android app pins backend certificate in production
- **TLS 1.3 minimum** — Older TLS versions are rejected

### Authentication

Every API endpoint (except public ones) requires:
- Valid JWT in `Authorization: Bearer <token>` header
- Non-expired token
- Signed with the correct secret

### Input Validation

All input is validated at multiple layers:

**Layer 1: Serializers**

```python
class OrderCreateSerializer(serializers.Serializer):
    seller_id = serializers.UUIDField(required=True)
    items = serializers.ListField(
        child=OrderItemSerializer(),
        min_length=1,
        max_length=100  # Prevent DoS via huge orders
    )
    
    def validate_seller_id(self, value):
        if not Seller.objects.filter(id=value, verified=True).exists():
            raise ValidationError("Invalid seller")
        return value
```

**Layer 2: Business Logic**

```python
# Even after serializer validation, verify permissions
def create_order(user, seller_id, items):
    seller = Seller.objects.get(id=seller_id)
    if not seller.is_open:
        raise ValidationError("Seller is not currently accepting orders")
    # ... more checks
```

**Layer 3: Database Constraints**

```sql
-- Enforce at DB level as last defense
ALTER TABLE orders ADD CONSTRAINT positive_amount
    CHECK (total_amount > 0);

ALTER TABLE orders ADD CONSTRAINT valid_status
    CHECK (status IN ('pending', 'accepted', 'preparing', 'ready', 'dispatched', 'delivered', 'cancelled'));
```

### Rate Limiting

Rate limits protect against abuse and DoS:

| Endpoint Category | Limit | Window |
|-------------------|-------|--------|
| Authentication (login, signup) | 5 requests | 1 minute |
| Password reset | 3 requests | 1 hour |
| Read endpoints (GET) | 100 requests | 1 minute |
| Write endpoints (POST, PATCH) | 30 requests | 1 minute |
| Search endpoints | 30 requests | 1 minute |
| File uploads | 10 requests | 1 minute |

**Implementation:**

```python
# backend/apps/common/rate_limits.py
from rest_framework.throttling import UserRateThrottle

class LoginRateThrottle(UserRateThrottle):
    scope = 'login'
    rate = '5/min'

class PasswordResetThrottle(UserRateThrottle):
    scope = 'password_reset'
    rate = '3/hour'
```

### CORS Policy

Strict CORS to prevent unauthorized cross-origin requests:

```python
# backend/config/settings/production.py
CORS_ALLOWED_ORIGINS = [
    "https://app.bulkbasket.app",
    "https://admin.bulkbasket.app",
]
CORS_ALLOW_CREDENTIALS = True
CORS_ALLOWED_HEADERS = [
    "authorization",
    "content-type",
    "x-request-id",
]
```

### Common Vulnerability Protection

#### SQL Injection
✅ **Protected** — Django ORM uses parameterized queries. Never write raw SQL from user input.

```python
# ❌ NEVER
cursor.execute(f"SELECT * FROM users WHERE email = '{email}'")

# ✅ ALWAYS
User.objects.filter(email=email)
```

#### Cross-Site Scripting (XSS)
✅ **Protected** — API returns JSON only; no HTML rendering. Django templates auto-escape.

#### Cross-Site Request Forgery (CSRF)
✅ **Protected** — CSRF middleware enabled. JWT auth doesn't rely on cookies for API.

#### Server-Side Request Forgery (SSRF)
✅ **Protected** — Outbound requests use allowlists for URLs.

```python
ALLOWED_WEBHOOK_HOSTS = ["hooks.slack.com", "api.stripe.com"]

def send_webhook(url):
    host = urlparse(url).hostname
    if host not in ALLOWED_WEBHOOK_HOSTS:
        raise SecurityError("Webhook URL not allowed")
    requests.post(url, timeout=5)
```

#### Insecure Direct Object Reference (IDOR)
✅ **Protected** — All object access checked via permissions AND RLS.

```python
# ❌ Vulnerable to IDOR
def get_order(order_id):
    return Order.objects.get(id=order_id)  # Any user can access any order!

# ✅ Protected
def get_order(order_id, user):
    order = Order.objects.get(id=order_id)
    if order.buyer_id != user.id and order.seller.user_id != user.id:
        raise PermissionDenied()
    return order
```

---

## 📱 Android App Security

### Secure Storage

Sensitive data on device is stored using **EncryptedSharedPreferences**:

```kotlin
// android/.../utils/SecureStorage.kt
class SecureStorage(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "bulkbasket_secure",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    fun saveAuthToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }
    
    fun getAuthToken(): String? = prefs.getString("auth_token", null)
    
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
```

### Certificate Pinning

In production builds, the app pins the backend's certificate:

```kotlin
// android/.../data/remote/NetworkModule.kt
val certificatePinner = CertificatePinner.Builder()
    .add("api.bulkbasket.app", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
    .add("api.bulkbasket.app", "sha256/BACKUPPINBACKUPPINBACKUPPINBACKUPPINBACKUPP=")
    .build()

val okHttpClient = OkHttpClient.Builder()
    .certificatePinner(certificatePinner)
    .build()
```

### Network Security Config

Enforce HTTPS-only for production:

```xml
<!-- android/app/src/main/res/xml/network_security_config.xml -->
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system"/>
        </trust-anchors>
    </base-config>
    <domain-config>
        <domain includeSubdomains="true">api.bulkbasket.app</domain>
        <pin-set>
            <pin digest="SHA-256">AAAA...</pin>
        </pin-set>
    </domain-config>
</network-security-config>
```

### Code Obfuscation

ProGuard/R8 is enabled for release builds:

```gradle
// android/app/build.gradle.kts
buildTypes {
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

### Screenshot & Screen Recording Protection

Sensitive screens prevent screenshots:

```kotlin
// For screens showing sensitive data
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window.setFlags(
        WindowManager.LayoutParams.FLAG_SECURE,
        WindowManager.LayoutParams.FLAG_SECURE
    )
}
```

### Root/Jailbreak Detection

Warn users on rooted devices (informational, not blocking):

```kotlin
class RootDetector {
    fun isDeviceRooted(): Boolean {
        return checkSuBinary() ||
               checkRootPackages() ||
               checkWritableSystemPaths()
    }
    
    private fun checkSuBinary(): Boolean {
        val paths = arrayOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su"
        )
        return paths.any { File(it).exists() }
    }
    // ... more checks
}
```

### Deep Link Validation

Validate deep links to prevent hijacking:

```kotlin
override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    intent.data?.let { uri ->
        if (uri.host != "bulkbasket.app" && uri.host != "app.bulkbasket.app") {
            // Reject untrusted deep link
            return
        }
        // Process safe deep link
    }
}
```

### Permissions

Request only the minimum permissions needed:

```xml
<!-- AndroidManifest.xml — minimal permissions -->
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>

<!-- Runtime permissions (request when needed) -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
```

**Request at usage time, not startup:**

```kotlin
// Only request when user needs to take a photo
private val cameraPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
) { granted ->
    if (granted) openCamera() else showPermissionRationale()
}
```

---

## 🖥 Backend Security

### Django Security Middleware

Standard security middleware enabled:

```python
# backend/config/settings/base.py
MIDDLEWARE = [
    'django.middleware.security.SecurityMiddleware',
    'django.contrib.sessions.middleware.SessionMiddleware',
    'corsheaders.middleware.CorsMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
    'apps.common.middleware.SecurityHeadersMiddleware',
    'apps.common.middleware.RequestLoggingMiddleware',
]
```

### Security Headers

Every response includes security headers:

```python
# backend/apps/common/middleware.py
class SecurityHeadersMiddleware:
    def process_response(self, request, response):
        response['X-Content-Type-Options'] = 'nosniff'
        response['X-Frame-Options'] = 'DENY'
        response['X-XSS-Protection'] = '1; mode=block'
        response['Referrer-Policy'] = 'strict-origin-when-cross-origin'
        response['Permissions-Policy'] = 'geolocation=(), microphone=(), camera=()'
        response['Content-Security-Policy'] = "default-src 'self'"
        return response
```

### Debug Mode

**NEVER** enable DEBUG in production:

```python
# backend/config/settings/production.py
DEBUG = False
ALLOWED_HOSTS = ["api.bulkbasket.app"]
```

Detailed errors are logged server-side, but users see generic error pages.

### Admin Panel Protection

Django admin is protected by:
- Separate URL prefix (not `/admin/`)
- IP allowlist (VPN only)
- MFA required for admin accounts
- Audit logging of all admin actions

```python
# backend/apps/common/middleware.py
class AdminIPRestrictionMiddleware:
    ALLOWED_IPS = ["10.0.0.0/8", "192.168.0.0/16"]
    
    def process_request(self, request):
        if request.path.startswith('/internal-admin/'):
            client_ip = self.get_client_ip(request)
            if not self.ip_in_allowlist(client_ip):
                return HttpResponseForbidden()
```

### Logging & Monitoring

Security-relevant events are logged:

```python
# What we log
SECURITY_EVENTS = [
    "login_success",
    "login_failure",
    "password_change",
    "password_reset_requested",
    "account_deleted",
    "admin_action",
    "permission_denied",
    "rate_limit_exceeded",
    "suspicious_activity",
]
```

**Example log format:**

```python
logger.warning("security_event", extra={
    "event": "login_failure",
    "user_id": user.id,
    "ip": get_client_ip(request),
    "user_agent": request.META.get("HTTP_USER_AGENT"),
    "timestamp": timezone.now().isoformat(),
})
```

**Never log:**
- ❌ Passwords or password hashes
- ❌ Full JWT tokens
- ❌ Credit card numbers
- ❌ Full addresses or phone numbers
- ❌ API secrets

### Background Task Security

Celery tasks are protected:

```python
# Verify JWT before processing sensitive tasks
@shared_task
def send_order_notification(user_id: str, order_id: str, jwt_token: str):
    try:
        payload = verify_jwt(jwt_token)
        if payload["sub"] != user_id:
            logger.error("JWT/user_id mismatch in task")
            return
        # Process task
    except AuthenticationFailed:
        logger.error("Invalid JWT in background task")
```

---

## 🗄 Database Security

### Row Level Security (RLS)

All tables have RLS policies enforced at the PostgreSQL level:

```sql
-- Enable RLS on all user tables
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE products ENABLE ROW LEVEL SECURITY;
ALTER TABLE sellers ENABLE ROW LEVEL SECURITY;

-- Example policies
CREATE POLICY "users_read_own" ON users
    FOR SELECT USING (id = auth.uid());

CREATE POLICY "users_update_own" ON users
    FOR UPDATE USING (id = auth.uid());

CREATE POLICY "products_public_read" ON products
    FOR SELECT USING (stock_qty > 0 AND is_active = true);

CREATE POLICY "products_seller_write" ON products
    FOR ALL USING (
        seller_id IN (
            SELECT id FROM sellers WHERE user_id = auth.uid()
        )
    );
```

### Database Access

- **Application user:** Limited privileges (SELECT, INSERT, UPDATE on specific tables)
- **Migration user:** Elevated privileges (used only during deployments)
- **Read-only user:** For analytics and reporting
- **Superuser:** Restricted to emergency use, credentials in secure vault

```sql
-- Example: create limited app user
CREATE USER bulkbasket_app WITH PASSWORD 'strong-password';
GRANT USAGE ON SCHEMA public TO bulkbasket_app;
GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA public TO bulkbasket_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO bulkbasket_app;
-- No DELETE permission — soft deletes only
```

### Backup Security

- **Encrypted backups** with AES-256
- **Off-site storage** with different provider
- **Retention:** 30 days rolling
- **Access logs** for all backup restores
- **Regular restore tests** (monthly)

### Sensitive Data Handling

Some fields are extra sensitive and get additional protection:

```sql
-- Phone numbers are indexed but stored hashed for lookup, plaintext for display
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email TEXT UNIQUE NOT NULL,
    phone TEXT,                    -- Plaintext (for display)
    phone_hash TEXT UNIQUE,        -- Hashed (for search)
    ...
);

-- Future: payment tokens (never store raw card numbers)
CREATE TABLE payment_methods (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    payment_token TEXT NOT NULL,   -- Tokenized by payment provider
    last_four TEXT,                -- Only last 4 digits stored
    ...
);
```

---

## ☁ Infrastructure Security

### Network Architecture

```
                   Internet
                       │
                       ▼
          ┌────────────────────┐
          │   Cloudflare WAF   │  ← DDoS + WAF protection
          └──────────┬─────────┘
                     │
                     ▼
          ┌────────────────────┐
          │   Load Balancer    │  ← TLS termination
          └──────────┬─────────┘
                     │
        ┌────────────┼────────────┐
        ▼            ▼            ▼
   ┌────────┐  ┌────────┐  ┌────────┐
   │ App 1  │  │ App 2  │  │ App 3  │  ← Django backend replicas
   └────┬───┘  └────┬───┘  └────┬───┘
        └───────────┼───────────┘
                    ▼
        ┌───────────────────────┐
        │   Private Network     │  ← No public access
        └───────────┬───────────┘
                    │
        ┌───────────┼───────────┐
        ▼           ▼           ▼
  ┌──────────┐ ┌────────┐ ┌──────────┐
  │ Supabase │ │ Redis  │ │  Celery  │
  │   (DB)   │ │(Cache) │ │(Workers) │
  └──────────┘ └────────┘ └──────────┘
```

### Firewall Rules

- **Ingress:** Only ports 80 (redirect to HTTPS) and 443 open publicly
- **Egress:** Restricted to approved external services (Supabase, FCM, payment providers)
- **Internal:** Services communicate over private network only

### DDoS Protection

- **Cloudflare** for edge protection
- **Rate limiting** at multiple layers
- **Auto-scaling** to absorb traffic spikes
- **Traffic monitoring** with alerts on anomalies

### Container Security

Docker images are hardened:

```dockerfile
# Backend Dockerfile
FROM python:3.11-slim AS base

# Non-root user
RUN useradd -m -u 1000 bulkbasket
USER bulkbasket

# Minimal packages
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy only what's needed
COPY --chown=bulkbasket:bulkbasket requirements.txt .
RUN pip install --user --no-cache-dir -r requirements.txt

COPY --chown=bulkbasket:bulkbasket . .

# No shell in production image
ENTRYPOINT ["python", "manage.py"]
CMD ["runserver", "0.0.0.0:8000"]
```

### CI/CD Security

- **Secrets:** Never in source code, injected via environment
- **Signed commits:** Required for main branch
- **Branch protection:** No force pushes to main
- **Dependency scanning:** Every PR
- **Container scanning:** Every image build

```yaml
# .github/workflows/security.yml
name: Security Scan
on: [push, pull_request]
jobs:
  scan:
    steps:
      - uses: actions/checkout@v4
      - name: Run Snyk security scan
        uses: snyk/actions/python@master
        with:
          args: --severity-threshold=high
      - name: Run Bandit (Python security linter)
        run: |
          pip install bandit
          bandit -r backend/ -f json -o bandit-report.json
      - name: Run Semgrep
        uses: returntocorp/semgrep-action@v1
        with:
          config: >-
            p/security-audit
            p/owasp-top-ten
```

---

## 🔑 Secrets Management

### The Rules

1. **NEVER** commit secrets to Git — not even in private repos
2. **NEVER** log secrets, even accidentally
3. **NEVER** put secrets in URL parameters or query strings
4. **NEVER** share secrets over unencrypted channels
5. **ALWAYS** rotate secrets after a team member leaves
6. **ALWAYS** use unique secrets per environment

### Storage

| Environment | Secret Storage |
|-------------|----------------|
| Local development | `.env` file (gitignored) |
| CI/CD | GitHub Actions Secrets |
| Staging | Cloud provider secrets manager |
| Production | HashiCorp Vault or AWS Secrets Manager |

### `.env.example` Pattern

Provide a template without values:

```bash
# .env.example - copy to .env and fill in values

# Django
DJANGO_SECRET_KEY=<generate-with-django-admin-shell>
DJANGO_DEBUG=False
DJANGO_ALLOWED_HOSTS=localhost,127.0.0.1

# Database
DATABASE_URL=postgresql://user:pass@localhost:5432/bulkbasket

# Supabase
SUPABASE_URL=https://xxxxx.supabase.co
SUPABASE_ANON_KEY=<from-supabase-dashboard>
SUPABASE_SERVICE_KEY=<from-supabase-dashboard>
SUPABASE_JWT_SECRET=<from-supabase-dashboard>

# Redis
REDIS_URL=redis://localhost:6379/0

# Celery
CELERY_BROKER_URL=redis://localhost:6379/1
CELERY_RESULT_BACKEND=redis://localhost:6379/2

# Firebase
FCM_CREDENTIALS_PATH=/path/to/firebase-credentials.json

# Monitoring
SENTRY_DSN=<from-sentry-dashboard>
```

### Rotation Schedule

| Secret Type | Rotation Frequency |
|-------------|-------------------|
| Database passwords | Every 90 days |
| API keys (external services) | Every 6 months |
| JWT signing keys | On team member departure |
| SSH keys | Every 6 months |
| TLS certificates | Auto-renewed (Let's Encrypt) |

### Detecting Committed Secrets

Pre-commit hook to catch secrets:

```yaml
# .pre-commit-config.yaml
repos:
  - repo: https://github.com/Yelp/detect-secrets
    rev: v1.4.0
    hooks:
      - id: detect-secrets
        args: ['--baseline', '.secrets.baseline']
```

**If a secret is accidentally committed:**

1. **Immediately** rotate the secret (invalidate the old one)
2. Remove from Git history:
   ```bash
   git filter-repo --path <file-with-secret> --invert-paths
   git push --force
   ```
3. Notify all team members
4. Add to `.gitignore` to prevent recurrence
5. Consider using `git-secrets` or similar tools

---

## 📦 Third-Party Dependencies

### Dependency Management

- **Backend:** Pin exact versions in `requirements.txt`
- **Android:** Use version catalog in `libs.versions.toml`
- **Review updates** before applying
- **Automated scanning** with Dependabot

### Vulnerability Scanning

Regular scans across all dependencies:

```yaml
# .github/dependabot.yml
version: 2
updates:
  - package-ecosystem: "pip"
    directory: "/backend"
    schedule:
      interval: "weekly"
    open-pull-requests-limit: 10
    
  - package-ecosystem: "gradle"
    directory: "/android"
    schedule:
      interval: "weekly"
```

### Dependency Update Process

1. **Automated PR** from Dependabot
2. **CI runs tests** with new version
3. **Review by team** — check breaking changes
4. **Merge if tests pass** and no critical issues
5. **Monitor** for issues after deployment

### License Compliance

We track licenses of all dependencies:

- ✅ **Allowed:** MIT, Apache 2.0, BSD, ISC
- ⚠️ **Requires review:** LGPL, MPL
- ❌ **Prohibited:** GPL (copyleft), commercial-only

---

## 🚨 Incident Response

### Severity Levels

| Level | Description | Response Time |
|-------|-------------|---------------|
| **P0 — Critical** | Active data breach, service down | Immediate |
| **P1 — High** | Vulnerability exploitable, no exploitation yet | 4 hours |
| **P2 — Medium** | Vulnerability requires special conditions | 24 hours |
| **P3 — Low** | Minor issue, low impact | 1 week |

### Incident Response Plan

#### 1. Detect
Sources of incident detection:
- Monitoring alerts (Sentry, uptime monitors)
- User reports
- Security researcher disclosure
- Internal discovery

#### 2. Assess
Within the first hour:
- **What happened?** — Nature of the incident
- **When?** — Timeline and duration
- **Who's affected?** — User impact
- **What data?** — Data compromised
- **Ongoing?** — Is the attack still active?

#### 3. Contain
Immediate actions to limit damage:
- Isolate affected systems
- Revoke compromised credentials
- Block malicious IPs
- Disable affected accounts
- Preserve evidence for investigation

#### 4. Eradicate
Remove the threat:
- Patch vulnerabilities
- Remove malware
- Reset compromised passwords
- Update dependencies

#### 5. Recover
Restore services:
- Verify systems are clean
- Restore from clean backups if needed
- Gradually restore user access
- Monitor for recurrence

#### 6. Learn
Post-incident review:
- **What worked?** — Effective controls
- **What failed?** — Gaps in defense
- **What next?** — Improvements to make
- **Document** — Write incident report

### Communication Plan

| Audience | When | How |
|----------|------|-----|
| **Internal team** | Immediately | Slack #security channel |
| **Users (if affected)** | Within 72 hours | Email + in-app notification |
| **Regulators** | Per legal requirements | Formal notification |
| **Public** | After investigation | Blog post / status page |

### Post-Incident Report Template

```markdown
# Incident Report: [Title]

**Date:** YYYY-MM-DD
**Severity:** P0 / P1 / P2 / P3
**Status:** Resolved

## Summary
Brief description of what happened.

## Timeline
- HH:MM — Event 1
- HH:MM — Event 2

## Root Cause
Technical explanation of the cause.

## Impact
- Users affected: X
- Data compromised: Y
- Financial impact: Z

## Resolution
Steps taken to resolve.

## Lessons Learned
What we learned.

## Action Items
- [ ] Improvement 1
- [ ] Improvement 2
```

---

## 🌍 Compliance & Privacy

### Regulatory Frameworks

BulkBasket is designed with these frameworks in mind:

- **Nigeria Data Protection Regulation (NDPR)** — Primary
- **GDPR** — For expansion to European users
- **PCI DSS** — For payment processing (future)

### Privacy Policy Requirements

Users must be informed about:
1. **What data we collect**
2. **Why we collect it**
3. **How long we keep it**
4. **Who we share it with**
5. **Their rights** (access, deletion, portability)
6. **How to contact us**

### User Rights

Users can:
- ✅ **Access** — Download their data (Settings → Export Data)
- ✅ **Rectify** — Update incorrect information
- ✅ **Delete** — Remove their account
- ✅ **Restrict** — Limit processing of their data
- ✅ **Object** — Refuse marketing communications
- ✅ **Portability** — Get data in machine-readable format

### Data Processing Records

We maintain records of:
- What personal data we process
- Categories of data subjects
- Categories of recipients
- Data retention periods
- Security measures in place

### International Data Transfers

If we ever transfer data internationally:
- Use Standard Contractual Clauses
- Only to countries with adequate protection
- Notify users in advance

---

## ✅ Security Checklist

### For Developers

Before every PR:

- [ ] No secrets or credentials committed
- [ ] Input validation on all user data
- [ ] Authorization checks on all endpoints
- [ ] SQL queries use parameterization (or ORM)
- [ ] No sensitive data in logs
- [ ] Error messages don't leak system info
- [ ] Dependencies updated to latest secure versions
- [ ] Tests cover security-relevant paths
- [ ] Documentation updated if security-relevant

### For Deployments

Before every production release:

- [ ] All security tests pass
- [ ] Dependency vulnerabilities addressed
- [ ] Secrets are properly configured
- [ ] Monitoring/alerting is active
- [ ] Backup verified
- [ ] Rollback plan ready
- [ ] Change documented

### Quarterly Security Review

Every 3 months:

- [ ] Review access permissions (remove ex-members)
- [ ] Rotate secrets per policy
- [ ] Update dependencies
- [ ] Review security logs for anomalies
- [ ] Test incident response plan
- [ ] Review and update this document
- [ ] Verify backups are working

### Annual Security Audit

Every year:

- [ ] External penetration test
- [ ] Full security review of architecture
- [ ] Update threat model
- [ ] Review compliance requirements
- [ ] Team security training
- [ ] Disaster recovery drill

---

## 📚 Security Resources

### For Team Members

**Training resources:**
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [OWASP Mobile Top 10](https://owasp.org/www-project-mobile-top-10/)
- [Django Security Documentation](https://docs.djangoproject.com/en/stable/topics/security/)
- [Android Security Best Practices](https://developer.android.com/topic/security/best-practices)
- [Supabase Security Guide](https://supabase.com/docs/guides/database/postgres/row-level-security)

**Tools:**
- **Bandit** — Python security linter
- **Semgrep** — Static analysis
- **Snyk** — Dependency scanning
- **Trivy** — Container scanning
- **OWASP ZAP** — Web app vulnerability scanner

### For Users

**Educational resources for our users:**
- How to create strong passwords
- How to spot phishing attempts
- What to do if account is compromised
- How to enable security features

---

## 🔒 Security Contacts

| Role | Contact | When to Contact |
|------|---------|-----------------|
| **Security Team** | security@bulkbasket.app | General security inquiries |
| **Project Lead** | lead@bulkbasket.app | Escalations, incidents |
| **Privacy Officer** | privacy@bulkbasket.app | Data rights, GDPR/NDPR requests |
| **Legal** | legal@bulkbasket.app | Legal notices, subpoenas |

**For emergencies:** Use `emergency@bulkbasket.app` — monitored 24/7

---

## 📝 Document History

| Version | Date | Changes | Author |
|---------|------|---------|--------|
| 1.0 | June 2026 | Initial security documentation | Project Lead |

---

**Security Documentation Version:** 1.0  
**Last Updated:** June 2026  
**Next Review:** September 2026  
**Owned by:** Project Lead + DevOps Lead

---

> Security is everyone's responsibility. When in doubt, ask. When you find something concerning, report it. Better safe than sorry.

[⬆ Back to top](#bulkbasket-security-documentation)
