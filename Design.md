# BulkBasket Design System

A complete design system specification ready for code implementation across Android (Jetpack Compose / XML) and web (React/CSS).

> This document provides **design tokens, components, and patterns** in a format that maps directly to code variables, theme files, and component libraries. Use it as the source of truth when implementing UI.

---

## 📋 Table of Contents

1. [Design Principles](#-design-principles)
2. [Color System](#-color-system)
3. [Typography](#-typography)
4. [Spacing & Sizing](#-spacing--sizing)
5. [Border Radius](#-border-radius)
6. [Shadows & Elevation](#-shadows--elevation)
7. [Iconography](#-iconography)
8. [Component Library](#-component-library)
9. [Layout Patterns](#-layout-patterns)
10. [Animation & Motion](#-animation--motion)
11. [Code Implementation](#-code-implementation)
12. [Accessibility](#-accessibility)

---

## 🎯 Design Principles

### 1. **Clarity First**
Every screen has a clear purpose. Users should understand what they can do within 3 seconds of seeing a screen.

### 2. **Familiar but Distinctive**
Use patterns users know from other apps, but with BulkBasket's unique visual identity.

### 3. **Trust Through Consistency**
Same patterns, colors, and behaviors across all three apps (Buyer, Seller, Rider).

### 4. **Mobile-First, Always**
Designed for small screens first; scaled up gracefully for tablets and larger devices.

### 5. **Performance is UX**
Beautiful design that loads slowly is bad design. Aim for 60fps interactions.

---

## 🎨 Color System

### Brand Colors

```css
/* CSS Custom Properties */
:root {
  /* Primary — Deep Forest Green */
  --color-primary-50:   #E8F1ED;
  --color-primary-100:  #C6DDD0;
  --color-primary-200:  #A1C7B0;
  --color-primary-300:  #7BB18F;
  --color-primary-400:  #5E9F77;
  --color-primary-500:  #2D6A4F;  /* Brand mid */
  --color-primary-600:  #245A42;
  --color-primary-700:  #1B4332;  /* Brand primary */
  --color-primary-800:  #133227;
  --color-primary-900:  #0F1D17;

  /* Accent — Warm Gold */
  --color-accent-50:    #FDF7E2;
  --color-accent-100:   #FBE9B0;
  --color-accent-200:   #F8DA7E;
  --color-accent-300:   #F5CB4C;
  --color-accent-400:   #ECC02A;
  --color-accent-500:   #D4A017;  /* Brand accent */
  --color-accent-600:   #B58A14;
  --color-accent-700:   #8B6A0F;
  --color-accent-800:   #5C4609;
  --color-accent-900:   #2E2305;
}
```

### Semantic Colors

```css
:root {
  /* Success */
  --color-success-light:  #D4EDDA;
  --color-success:        #28A745;
  --color-success-dark:   #1E7E34;

  /* Warning */
  --color-warning-light:  #FFF3CD;
  --color-warning:        #FFC107;
  --color-warning-dark:   #C39A03;

  /* Error / Danger */
  --color-error-light:    #F8D7DA;
  --color-error:          #DC3545;
  --color-error-dark:     #BD2130;

  /* Info */
  --color-info-light:     #D1ECF1;
  --color-info:           #17A2B8;
  --color-info-dark:      #117A8B;
}
```

### Neutral Colors

```css
:root {
  --color-white:          #FFFFFF;
  --color-gray-50:        #FAFAFA;
  --color-gray-100:       #F5F5F5;
  --color-gray-200:       #E5E5E5;
  --color-gray-300:       #D4D4D4;
  --color-gray-400:       #A3A3A3;
  --color-gray-500:       #737373;
  --color-gray-600:       #525252;
  --color-gray-700:       #404040;
  --color-gray-800:       #262626;
  --color-gray-900:       #171717;
  --color-black:          #000000;
}
```

### Background Colors

```css
:root {
  /* Light theme */
  --bg-primary:           #FFFFFF;
  --bg-secondary:         #FAFAFA;
  --bg-tertiary:          #F5F5F5;
  --bg-elevated:          #FFFFFF;
  --bg-overlay:           rgba(0, 0, 0, 0.5);
  
  /* Dark theme (Seller dashboard) */
  --bg-dark-primary:      #0F1D17;
  --bg-dark-secondary:    #1A2D24;
  --bg-dark-tertiary:     #243B30;
}
```

### Text Colors

```css
:root {
  --text-primary:         #1A1A1A;    /* Headings, body */
  --text-secondary:       #525252;    /* Subtitles, meta */
  --text-tertiary:        #A3A3A3;    /* Disabled, placeholders */
  --text-inverse:         #FFFFFF;    /* On dark backgrounds */
  --text-accent:          #D4A017;    /* Highlighted info */
  --text-link:            #2D6A4F;    /* Links */
}
```

### Border Colors

```css
:root {
  --border-subtle:        #E5E5E5;
  --border-default:       #D4D4D4;
  --border-emphasis:      #A3A3A3;
  --border-accent:        #D4A017;
}
```

### Status Colors (Order Lifecycle)

```css
:root {
  --status-pending:       #FFC107;    /* Yellow */
  --status-accepted:      #28A745;    /* Green */
  --status-preparing:     #FFC107;    /* Yellow */
  --status-ready:         #17A2B8;    /* Blue */
  --status-dispatched:    #6F42C1;    /* Purple */
  --status-delivered:     #28A745;    /* Green */
  --status-cancelled:     #DC3545;    /* Red */
  --status-rejected:      #DC3545;    /* Red */
}
```

---

## ✍ Typography

### Font Families

```css
:root {
  /* Primary — for body text and most UI */
  --font-sans: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  
  /* Display — for headings and numbers */
  --font-display: 'Sora', 'Inter', sans-serif;
  
  /* Monospace — for codes, IDs, numbers in tables */
  --font-mono: 'JetBrains Mono', 'Fira Code', Consolas, monospace;
}
```

### Type Scale

| Token | Size | Line Height | Weight | Use Case |
|-------|------|-------------|--------|----------|
| `display-xl` | 48px | 56px | 800 | Hero headlines (rare) |
| `display-lg` | 36px | 44px | 800 | Page headlines |
| `display-md` | 28px | 36px | 700 | Section titles |
| `display-sm` | 24px | 32px | 700 | Card titles |
| `heading-lg` | 20px | 28px | 700 | List headers |
| `heading-md` | 18px | 26px | 600 | Subsection headers |
| `heading-sm` | 16px | 24px | 600 | Field labels |
| `body-lg` | 16px | 24px | 400 | Default body |
| `body-md` | 14px | 20px | 400 | Secondary text |
| `body-sm` | 12px | 16px | 400 | Captions, meta |
| `caption` | 11px | 14px | 500 | Tags, badges |
| `overline` | 10px | 14px | 600 | Section labels |

### CSS Implementation

```css
:root {
  --text-display-xl: 800 48px/56px var(--font-display);
  --text-display-lg: 800 36px/44px var(--font-display);
  --text-display-md: 700 28px/36px var(--font-display);
  --text-display-sm: 700 24px/32px var(--font-display);
  --text-heading-lg: 700 20px/28px var(--font-sans);
  --text-heading-md: 600 18px/26px var(--font-sans);
  --text-heading-sm: 600 16px/24px var(--font-sans);
  --text-body-lg:    400 16px/24px var(--font-sans);
  --text-body-md:    400 14px/20px var(--font-sans);
  --text-body-sm:    400 12px/16px var(--font-sans);
  --text-caption:    500 11px/14px var(--font-sans);
  --text-overline:   600 10px/14px var(--font-sans);
}

/* Usage */
.page-title { font: var(--text-display-lg); }
.body-text { font: var(--text-body-lg); }
```

### Letter Spacing

```css
:root {
  --tracking-tight:   -0.02em;   /* For display */
  --tracking-normal:  0;
  --tracking-wide:    0.05em;    /* For overlines */
  --tracking-wider:   0.1em;     /* For caps */
}
```

---

## 📏 Spacing & Sizing

### Spacing Scale (Based on 4px grid)

```css
:root {
  --space-0:      0;
  --space-1:      4px;
  --space-2:      8px;
  --space-3:      12px;
  --space-4:      16px;
  --space-5:      20px;
  --space-6:      24px;
  --space-7:      28px;
  --space-8:      32px;
  --space-10:     40px;
  --space-12:     48px;
  --space-14:     56px;
  --space-16:     64px;
  --space-20:     80px;
  --space-24:     96px;
}
```

### Component Sizing

```css
:root {
  /* Touch targets — minimum 44px for accessibility */
  --size-touch-min:    44px;
  --size-touch-md:     48px;
  --size-touch-lg:     56px;

  /* Icons */
  --size-icon-xs:      12px;
  --size-icon-sm:      16px;
  --size-icon-md:      20px;
  --size-icon-lg:      24px;
  --size-icon-xl:      32px;
  --size-icon-2xl:     48px;

  /* Avatars */
  --size-avatar-xs:    24px;
  --size-avatar-sm:    32px;
  --size-avatar-md:    40px;
  --size-avatar-lg:    56px;
  --size-avatar-xl:    80px;
}
```

### Container Widths

```css
:root {
  --width-screen-sm:   360px;    /* Small phones */
  --width-screen-md:   414px;    /* Standard phones */
  --width-screen-lg:   600px;    /* Tablets */
  --width-content:     720px;    /* Reading width */
  --width-dashboard:   1280px;   /* Desktop dashboard */
}
```

---

## 🔘 Border Radius

```css
:root {
  --radius-none:       0;
  --radius-sm:         4px;       /* Tags, small chips */
  --radius-md:         8px;       /* Buttons, inputs */
  --radius-lg:         12px;      /* Cards */
  --radius-xl:         16px;      /* Large cards */
  --radius-2xl:        24px;      /* Modals */
  --radius-3xl:        32px;      /* Phone screen, hero */
  --radius-full:       9999px;    /* Pills, avatars */
}
```

---

## 🌗 Shadows & Elevation

```css
:root {
  /* Light shadows */
  --shadow-xs:     0 1px 2px rgba(0, 0, 0, 0.05);
  --shadow-sm:     0 1px 3px rgba(0, 0, 0, 0.1), 0 1px 2px rgba(0, 0, 0, 0.06);
  --shadow-md:     0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  --shadow-lg:     0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  --shadow-xl:     0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  --shadow-2xl:    0 25px 50px -12px rgba(0, 0, 0, 0.25);

  /* Focus ring */
  --shadow-focus:  0 0 0 3px rgba(45, 106, 79, 0.3);

  /* Inner shadow */
  --shadow-inner:  inset 0 2px 4px 0 rgba(0, 0, 0, 0.06);
}
```

### Android Elevation Mapping

| CSS Shadow | Android `elevation` |
|------------|---------------------|
| `--shadow-xs` | 1dp |
| `--shadow-sm` | 2dp |
| `--shadow-md` | 4dp |
| `--shadow-lg` | 8dp |
| `--shadow-xl` | 12dp |
| `--shadow-2xl` | 24dp |

---

## 🎯 Iconography

### Icon Library

Use **Tabler Icons** (24px stroke 2px) — open source, comprehensive, consistent style.

CDN: `https://tabler-icons.io/`

### Icon Sizes

| Token | Size | Use Case |
|-------|------|----------|
| `xs` | 12px | Inline with caption text |
| `sm` | 16px | Inline with body text |
| `md` | 20px | Buttons, list items |
| `lg` | 24px | Nav, primary actions |
| `xl` | 32px | Empty states, hero |
| `2xl` | 48px | Onboarding, illustrations |

### Common Icons by Context

#### Navigation
- `home` — Home tab
- `search` — Search/discover
- `package` — Orders
- `user` — Profile/account
- `bell` — Notifications

#### Actions
- `plus` — Add
- `pencil` — Edit
- `trash` — Delete
- `share` — Share
- `bookmark` — Save

#### Status
- `check` — Success/done
- `x` — Error/cancel
- `alert-circle` — Warning
- `info-circle` — Information
- `loader` — Loading

#### E-commerce
- `shopping-cart` — Cart
- `currency-naira` — Price
- `package` — Product
- `building-store` — Seller
- `truck` — Delivery
- `motorbike` — Rider

#### Categories
- `wheat` — Grains
- `plant` — Produce
- `droplet` — Oils
- `star` — Spices
- `fish` — Protein
- `bread` — Bakery

---

## 🧩 Component Library

### 1. Buttons

#### Primary Button

```css
.btn-primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  height: var(--size-touch-md);
  padding: 0 var(--space-6);
  background: var(--color-primary-700);
  color: var(--text-inverse);
  font: var(--text-heading-sm);
  border-radius: var(--radius-md);
  border: none;
  cursor: pointer;
  transition: background 150ms ease;
}

.btn-primary:hover {
  background: var(--color-primary-800);
}

.btn-primary:active {
  background: var(--color-primary-900);
}

.btn-primary:disabled {
  background: var(--color-gray-300);
  color: var(--color-gray-500);
  cursor: not-allowed;
}
```

#### Secondary Button

```css
.btn-secondary {
  /* same base */
  background: transparent;
  color: var(--color-primary-700);
  border: 1.5px solid var(--color-primary-700);
}

.btn-secondary:hover {
  background: var(--color-primary-50);
}
```

#### Accent Button

```css
.btn-accent {
  /* same base */
  background: var(--color-accent-500);
  color: var(--color-gray-900);
}
```

#### Size Variants

```css
.btn-sm { height: 36px; padding: 0 var(--space-4); font: var(--text-body-sm); }
.btn-md { height: 44px; padding: 0 var(--space-6); }
.btn-lg { height: 56px; padding: 0 var(--space-8); font: var(--text-heading-md); }
```

### 2. Input Fields

#### Text Input

```css
.input {
  width: 100%;
  height: var(--size-touch-md);
  padding: 0 var(--space-4);
  background: var(--bg-primary);
  color: var(--text-primary);
  font: var(--text-body-lg);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  outline: none;
  transition: border 150ms ease, box-shadow 150ms ease;
}

.input:focus {
  border-color: var(--color-primary-500);
  box-shadow: var(--shadow-focus);
}

.input:disabled {
  background: var(--color-gray-100);
  color: var(--text-tertiary);
}

.input.error {
  border-color: var(--color-error);
}
```

#### Search Input

```css
.search-input {
  /* same as .input */
  padding-left: var(--space-10);
  background-image: url('search-icon.svg');
  background-repeat: no-repeat;
  background-position: var(--space-4) center;
  background-size: var(--size-icon-md);
}
```

### 3. Cards

#### Basic Card

```css
.card {
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  box-shadow: var(--shadow-sm);
}

.card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-1px);
  transition: all 200ms ease;
}
```

#### Seller Card

```html
<div class="seller-card">
  <div class="seller-card__avatar">🏪</div>
  <div class="seller-card__content">
    <h3 class="seller-card__name">Mama Chuka's Store</h3>
    <p class="seller-card__meta">📍 0.8 km · 34 items</p>
  </div>
  <div class="seller-card__rating">
    <span class="rating">⭐ 4.8</span>
    <span class="badge badge--success">Open</span>
  </div>
</div>
```

```css
.seller-card {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  background: var(--bg-elevated);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-subtle);
}

.seller-card__avatar {
  width: var(--size-avatar-md);
  height: var(--size-avatar-md);
  border-radius: var(--radius-md);
  background: var(--color-primary-50);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--size-icon-lg);
  flex-shrink: 0;
}

.seller-card__name {
  font: var(--text-heading-sm);
  color: var(--text-primary);
}

.seller-card__meta {
  font: var(--text-body-sm);
  color: var(--text-secondary);
  margin-top: var(--space-1);
}
```

#### Product Card

```html
<div class="product-card">
  <div class="product-card__image">🍚</div>
  <div class="product-card__body">
    <h4 class="product-card__name">Long Grain Rice</h4>
    <p class="product-card__qty">50 kg bag · 18 in stock</p>
    <p class="product-card__price">₦42,000</p>
  </div>
  <button class="btn-icon">+</button>
</div>
```

### 4. Badges & Chips

```css
.badge {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-1) var(--space-2);
  font: var(--text-caption);
  border-radius: var(--radius-sm);
}

.badge--success {
  background: var(--color-success-light);
  color: var(--color-success-dark);
}

.badge--warning {
  background: var(--color-warning-light);
  color: var(--color-warning-dark);
}

.badge--error {
  background: var(--color-error-light);
  color: var(--color-error-dark);
}

.badge--info {
  background: var(--color-info-light);
  color: var(--color-info-dark);
}

/* Status badges */
.badge--pending     { background: #FFF3CD; color: #856404; }
.badge--accepted    { background: #D4EDDA; color: #155724; }
.badge--preparing   { background: #FFF3CD; color: #856404; }
.badge--ready       { background: #D1ECF1; color: #0C5460; }
.badge--dispatched  { background: #E2D9F3; color: #4B0082; }
.badge--delivered   { background: #D4EDDA; color: #155724; }
.badge--cancelled   { background: #F8D7DA; color: #721C24; }
```

### 5. Navigation

#### Bottom Tab Bar

```html
<nav class="bottom-nav">
  <a href="#home" class="bottom-nav__item bottom-nav__item--active">
    <i class="icon icon-home"></i>
    <span>Home</span>
  </a>
  <a href="#search" class="bottom-nav__item">
    <i class="icon icon-search"></i>
    <span>Browse</span>
  </a>
  <a href="#orders" class="bottom-nav__item">
    <i class="icon icon-package"></i>
    <span>Orders</span>
  </a>
  <a href="#profile" class="bottom-nav__item">
    <i class="icon icon-user"></i>
    <span>Account</span>
  </a>
</nav>
```

```css
.bottom-nav {
  display: flex;
  justify-content: space-around;
  padding: var(--space-2) 0;
  background: var(--bg-primary);
  border-top: 1px solid var(--border-subtle);
}

.bottom-nav__item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-2) var(--space-4);
  color: var(--text-tertiary);
  font: var(--text-overline);
  text-decoration: none;
  transition: color 150ms ease;
}

.bottom-nav__item--active {
  color: var(--color-primary-700);
}

.bottom-nav__item--active .icon {
  color: var(--color-primary-700);
}
```

#### App Bar

```css
.app-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 var(--space-4);
  background: var(--bg-primary);
  border-bottom: 1px solid var(--border-subtle);
}

.app-bar__title {
  font: var(--text-heading-md);
  color: var(--text-primary);
}

.app-bar__action {
  width: var(--size-touch-min);
  height: var(--size-touch-min);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-full);
}
```

### 6. List Items

```css
.list-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3) var(--space-4);
  border-bottom: 1px solid var(--border-subtle);
  cursor: pointer;
  transition: background 150ms ease;
}

.list-item:hover {
  background: var(--bg-secondary);
}

.list-item:last-child {
  border-bottom: none;
}

.list-item__leading {
  flex-shrink: 0;
}

.list-item__content {
  flex: 1;
  min-width: 0;
}

.list-item__title {
  font: var(--text-heading-sm);
  color: var(--text-primary);
}

.list-item__subtitle {
  font: var(--text-body-sm);
  color: var(--text-secondary);
  margin-top: var(--space-1);
}

.list-item__trailing {
  flex-shrink: 0;
}
```

### 7. Modals & Dialogs

```css
.modal-overlay {
  position: fixed;
  inset: 0;
  background: var(--bg-overlay);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 1000;
}

.modal {
  width: 100%;
  max-width: 480px;
  background: var(--bg-elevated);
  border-radius: var(--radius-2xl) var(--radius-2xl) 0 0;
  padding: var(--space-6);
  box-shadow: var(--shadow-2xl);
  animation: slideUp 250ms ease-out;
}

.modal__handle {
  width: 40px;
  height: 4px;
  background: var(--border-default);
  border-radius: var(--radius-full);
  margin: 0 auto var(--space-4);
}

.modal__title {
  font: var(--text-display-sm);
  color: var(--text-primary);
  margin-bottom: var(--space-4);
}

@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}
```

### 8. Loading States

#### Skeleton

```css
.skeleton {
  background: linear-gradient(
    90deg,
    var(--color-gray-200) 0%,
    var(--color-gray-100) 50%,
    var(--color-gray-200) 100%
  );
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite linear;
  border-radius: var(--radius-md);
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

.skeleton-text-line {
  height: 16px;
  margin: var(--space-2) 0;
  border-radius: var(--radius-sm);
}

.skeleton-avatar {
  width: var(--size-avatar-md);
  height: var(--size-avatar-md);
  border-radius: var(--radius-full);
}

.skeleton-card {
  height: 80px;
  border-radius: var(--radius-lg);
}
```

#### Spinner

```css
.spinner {
  width: 24px;
  height: 24px;
  border: 2.5px solid var(--color-gray-200);
  border-top-color: var(--color-primary-500);
  border-radius: var(--radius-full);
  animation: spin 800ms linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
```

### 9. Empty States

```html
<div class="empty-state">
  <div class="empty-state__icon">📦</div>
  <h3 class="empty-state__title">No orders yet</h3>
  <p class="empty-state__description">
    When you place your first order, it'll show up here.
  </p>
  <button class="btn-primary">Start shopping</button>
</div>
```

```css
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: var(--space-12) var(--space-6);
  gap: var(--space-3);
}

.empty-state__icon {
  font-size: 64px;
  margin-bottom: var(--space-2);
}

.empty-state__title {
  font: var(--text-display-sm);
  color: var(--text-primary);
}

.empty-state__description {
  font: var(--text-body-md);
  color: var(--text-secondary);
  max-width: 280px;
}
```

---

## 📐 Layout Patterns

### 1. Mobile Screen Structure

```
┌────────────────────────────────┐
│  Status Bar (24dp)             │  ← System
├────────────────────────────────┤
│  App Bar (56dp)                │  ← Header
├────────────────────────────────┤
│                                │
│  Scrollable Content            │  ← Main
│                                │
│                                │
│                                │
├────────────────────────────────┤
│  Bottom Nav (56dp)             │  ← Tabs
├────────────────────────────────┤
│  Home Indicator (16dp)         │  ← System
└────────────────────────────────┘
```

### 2. Safe Areas

Always respect safe areas (status bar, notch, home indicator):

```css
.screen {
  padding-top: env(safe-area-inset-top);
  padding-bottom: env(safe-area-inset-bottom);
  padding-left: env(safe-area-inset-left);
  padding-right: env(safe-area-inset-right);
}
```

### 3. Common Layouts

#### Two-Column Card Grid

```css
.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-3);
  padding: var(--space-3);
}
```

#### Horizontal Scroll

```css
.horizontal-scroll {
  display: flex;
  gap: var(--space-3);
  overflow-x: auto;
  padding: var(--space-3);
  scroll-snap-type: x mandatory;
  -webkit-overflow-scrolling: touch;
}

.horizontal-scroll > * {
  flex-shrink: 0;
  scroll-snap-align: start;
}

.horizontal-scroll::-webkit-scrollbar {
  display: none;
}
```

---

## 🎬 Animation & Motion

### Timing Functions

```css
:root {
  --ease-out:        cubic-bezier(0.0, 0.0, 0.2, 1.0);
  --ease-in:         cubic-bezier(0.4, 0.0, 1.0, 1.0);
  --ease-in-out:     cubic-bezier(0.4, 0.0, 0.2, 1.0);
  --ease-bounce:     cubic-bezier(0.68, -0.55, 0.265, 1.55);
}
```

### Duration Tokens

```css
:root {
  --duration-instant: 100ms;
  --duration-fast:    150ms;
  --duration-normal:  200ms;
  --duration-slow:    300ms;
  --duration-slower:  500ms;
}
```

### Common Animations

```css
/* Fade in */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* Slide up */
@keyframes slideUp {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

/* Scale in */
@keyframes scaleIn {
  from { transform: scale(0.95); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

/* Pulse */
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}
```

### Microinteractions

- **Button press:** `transform: scale(0.97)` over 100ms
- **Card hover:** `translateY(-1px)` + shadow upgrade over 200ms
- **Page transitions:** Slide horizontally over 300ms
- **Modal open:** Slide up from bottom over 250ms
- **Loading:** Skeleton shimmer continuous 1500ms

---

## 💻 Code Implementation

### React / Web (Tailwind CSS)

Tailwind config (`tailwind.config.js`):

```javascript
module.exports = {
  theme: {
    extend: {
      colors: {
        primary: {
          50:  '#E8F1ED',
          500: '#2D6A4F',
          700: '#1B4332',
          900: '#0F1D17',
        },
        accent: {
          500: '#D4A017',
          700: '#8B6A0F',
        },
        // ... rest of palette
      },
      fontFamily: {
        sans: ['Inter', 'sans-serif'],
        display: ['Sora', 'sans-serif'],
      },
      borderRadius: {
        'xl': '12px',
        '2xl': '16px',
        '3xl': '24px',
      },
      boxShadow: {
        'card': '0 1px 3px rgba(0,0,0,0.1)',
      }
    }
  }
}
```

### Android (Jetpack Compose)

`Theme.kt`:

```kotlin
// Colors.kt
val Primary50 = Color(0xFFE8F1ED)
val Primary500 = Color(0xFF2D6A4F)
val Primary700 = Color(0xFF1B4332)
val Accent500 = Color(0xFFD4A017)
val Gray100 = Color(0xFFF5F5F5)

// Type.kt
val DisplayLarge = TextStyle(
    fontFamily = SoraFontFamily,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 36.sp,
    lineHeight = 44.sp,
)

val BodyLarge = TextStyle(
    fontFamily = InterFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
)

// Shapes.kt
val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
)

// Theme.kt
@Composable
fun BulkBasketTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Primary700,
            secondary = Accent500,
            background = Color.White,
            surface = Color.White,
            onPrimary = Color.White,
            onSurface = Color(0xFF1A1A1A),
        ),
        typography = BulkBasketTypography,
        shapes = Shapes,
        content = content
    )
}
```

### Android (XML)

`colors.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="primary_50">#E8F1ED</color>
    <color name="primary_500">#2D6A4F</color>
    <color name="primary_700">#1B4332</color>
    <color name="accent_500">#D4A017</color>
    
    <color name="text_primary">#1A1A1A</color>
    <color name="text_secondary">#525252</color>
    
    <color name="bg_primary">#FFFFFF</color>
    <color name="bg_secondary">#FAFAFA</color>
    
    <color name="border_subtle">#E5E5E5</color>
</resources>
```

`dimens.xml`:

```xml
<resources>
    <dimen name="space_1">4dp</dimen>
    <dimen name="space_2">8dp</dimen>
    <dimen name="space_3">12dp</dimen>
    <dimen name="space_4">16dp</dimen>
    <dimen name="space_6">24dp</dimen>
    <dimen name="space_8">32dp</dimen>
    
    <dimen name="radius_md">8dp</dimen>
    <dimen name="radius_lg">12dp</dimen>
    
    <dimen name="touch_min">44dp</dimen>
</resources>
```

`styles.xml`:

```xml
<resources>
    <style name="TextAppearance.DisplayLarge" parent="TextAppearance.AppCompat">
        <item name="android:textSize">36sp</item>
        <item name="android:fontFamily">@font/sora_bold</item>
        <item name="android:textColor">@color/text_primary</item>
    </style>
    
    <style name="Widget.Button.Primary" parent="Widget.AppCompat.Button">
        <item name="android:background">@drawable/btn_primary_bg</item>
        <item name="android:textColor">@color/white</item>
        <item name="android:textSize">16sp</item>
        <item name="android:minHeight">@dimen/touch_min</item>
        <item name="android:paddingHorizontal">@dimen/space_6</item>
    </style>
</resources>
```

---

## ♿ Accessibility

### Color Contrast

All text must meet **WCAG AA** standards:

- **Normal text** (14px+): 4.5:1 contrast ratio
- **Large text** (18px+ or 14px+ bold): 3:1 contrast ratio
- **Interactive elements:** 3:1 against background

### Touch Targets

- **Minimum touch target:** 44 × 44 dp/px
- **Spacing between targets:** 8 dp/px minimum
- **Primary actions:** 48 × 48 dp/px recommended

### Screen Reader Support

#### Web

```html
<button aria-label="Add Long Grain Rice to cart" type="button">
  <span aria-hidden="true">+</span>
</button>

<img src="rice.jpg" alt="Long grain rice 50kg bag">
```

#### Android

```kotlin
Button(
    onClick = { /* ... */ },
    modifier = Modifier.semantics {
        contentDescription = "Add ${product.name} to cart"
    }
) {
    Text("+")
}
```

### Focus Indicators

Always provide visible focus states:

```css
:focus-visible {
  outline: 2px solid var(--color-primary-500);
  outline-offset: 2px;
}

.btn:focus-visible {
  box-shadow: var(--shadow-focus);
}
```

### Reduced Motion

Respect user preferences:

```css
@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}
```

---

## 📚 Resources & Tools

### Design Tools
- **Figma** — Source of truth for designs
- **Tabler Icons** — Icon library
- **Google Fonts** — Font hosting (Inter, Sora)

### Code Tools
- **Color contrast checker:** [webaim.org/resources/contrastchecker/](https://webaim.org/resources/contrastchecker/)
- **Tailwind Play:** [play.tailwindcss.com](https://play.tailwindcss.com/)
- **Compose Layout Inspector:** Android Studio

### Reference Designs
- Material Design 3: [m3.material.io](https://m3.material.io)
- iOS Human Interface Guidelines
- Refactoring UI book

---

## 🔄 Updating This Design System

When proposing changes:

1. **Open an issue** describing the change and rationale
2. **Update Figma** with the new spec
3. **Update this document** with new tokens/components
4. **Update code implementations** (Compose theme, CSS variables)
5. **Open a PR** with all related changes
6. **Get sign-off** from the Designer (Role 4)

---

**Design System Version:** 1.0  
**Last Updated:** June 2026  
**Maintained by:** UI/UX Designer (Role 4) + Engineering Team