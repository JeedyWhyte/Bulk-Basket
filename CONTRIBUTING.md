# Contributing to BulkBasket

First off — thanks for taking the time to contribute! 🎉

This document outlines the standards, workflows, and expectations for contributing to the BulkBasket project. As a 5-person academic team, following these guidelines ensures we work efficiently, avoid conflicts, and produce a polished final deliverable.

---

## 📋 Table of Contents

1. [Code of Conduct](#-code-of-conduct)
2. [Getting Started](#-getting-started)
3. [Development Workflow](#-development-workflow)
4. [Branch Strategy](#-branch-strategy)
5. [Commit Standards](#-commit-standards)
6. [Pull Request Process](#-pull-request-process)
7. [Code Review Guidelines](#-code-review-guidelines)
8. [Coding Standards](#-coding-standards)
9. [Testing Requirements](#-testing-requirements)
10. [Documentation Standards](#-documentation-standards)
11. [Issue Reporting](#-issue-reporting)
12. [Team Communication](#-team-communication)
13. [Conflict Resolution](#-conflict-resolution)

---

## 🤝 Code of Conduct

### Our Standards

- **Respect:** Treat all team members with respect, regardless of skill level or role
- **Constructive feedback:** Critique code, not people
- **Patience:** Everyone learns at their own pace — help, don't shame
- **Honesty:** Communicate blockers and mistakes early
- **Ownership:** Take responsibility for your work and assigned tasks

### Unacceptable Behavior

- Personal attacks or insulting comments
- Public criticism of teammates without first attempting private resolution
- Ignoring team agreements or unilaterally making major decisions
- Sitting silently on blockers instead of asking for help
- Committing untested or broken code to the `main` branch

---

## 🚀 Getting Started

### 1. Set Up Your Environment

Before contributing, complete the setup steps in the [main README](./README.md#-setup--installation):

- [ ] Clone the repository
- [ ] Install all prerequisites (Python, Docker, Android Studio)
- [ ] Configure your `.env` files
- [ ] Run the project locally and verify everything works
- [ ] Configure Git with your team email

### 2. Configure Git

```bash
# Set your name and email
git config --global user.name "Your Full Name"
git config --global user.email "your-team-email@school.edu"

# Set default branch to main
git config --global init.defaultBranch main

# Enable colored output
git config --global color.ui auto

# Set up SSH or HTTPS for GitHub (recommended: SSH)
ssh-keygen -t ed25519 -C "your-team-email@school.edu"
# Add the public key to your GitHub account
```

### 3. Set Up Pre-commit Hooks (Recommended)

```bash
# Install pre-commit
pip install pre-commit

# Install the hooks defined in .pre-commit-config.yaml
pre-commit install
```

This runs linters and formatters automatically before every commit.

---

## 🔄 Development Workflow

### The Complete Cycle

```
   ┌─────────────────────────────────────────────┐
   │  1. Pick a task from GitHub Projects board │
   └────────────────────┬────────────────────────┘
                        ▼
   ┌─────────────────────────────────────────────┐
   │  2. Create a feature branch from main      │
   └────────────────────┬────────────────────────┘
                        ▼
   ┌─────────────────────────────────────────────┐
   │  3. Develop locally; commit often          │
   └────────────────────┬────────────────────────┘
                        ▼
   ┌─────────────────────────────────────────────┐
   │  4. Write/update tests                     │
   └────────────────────┬────────────────────────┘
                        ▼
   ┌─────────────────────────────────────────────┐
   │  5. Push branch & open Pull Request        │
   └────────────────────┬────────────────────────┘
                        ▼
   ┌─────────────────────────────────────────────┐
   │  6. Address review feedback                │
   └────────────────────┬────────────────────────┘
                        ▼
   ┌─────────────────────────────────────────────┐
   │  7. Merge to main after approval           │
   └─────────────────────────────────────────────┘
```

### Daily Routine

1. **Pull latest changes** before starting work:
   ```bash
   git checkout main
   git pull origin main
   ```

2. **Create your feature branch:**
   ```bash
   git checkout -b feature/your-task-name
   ```

3. **Commit small, focused changes** throughout the day

4. **Push to remote** at least once per day:
   ```bash
   git push -u origin feature/your-task-name
   ```

5. **Sync with `main`** if working on a long-running branch:
   ```bash
   git fetch origin
   git rebase origin/main
   ```

---

## 🌿 Branch Strategy

We use a **simplified Git Flow** with `main` as the protected primary branch.

### Branch Types

| Prefix | Purpose | Example |
|--------|---------|---------|
| `feature/` | New features or screens | `feature/buyer-cart-screen` |
| `bugfix/` | Bug fixes | `bugfix/order-status-update` |
| `hotfix/` | Critical production fixes | `hotfix/auth-token-expiry` |
| `docs/` | Documentation changes only | `docs/api-spec-update` |
| `refactor/` | Code refactoring (no behavior change) | `refactor/repository-pattern` |
| `test/` | Adding or updating tests | `test/order-endpoint-coverage` |
| `chore/` | Build, deps, tooling updates | `chore/upgrade-django-4.2` |

### Naming Rules

- Use **lowercase** with **hyphens** (kebab-case)
- Keep names **short but descriptive** (3-6 words max)
- Include a **ticket/issue number** if applicable: `feature/123-add-search`
- **Avoid:** spaces, underscores, special characters, vague names like `feature/fix`

### Protected Branch: `main`

- ❌ **No direct commits** to `main`
- ✅ All changes via Pull Request
- ✅ Require **2 approvals** before merge
- ✅ All CI checks must pass
- ✅ Branch must be up-to-date with `main`

---

## 📝 Commit Standards

We follow the [**Conventional Commits**](https://www.conventionalcommits.org/) specification.

### Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

| Type | When to Use |
|------|-------------|
| `feat` | A new feature |
| `fix` | A bug fix |
| `docs` | Documentation only |
| `style` | Code style/formatting (no logic change) |
| `refactor` | Code restructure without behavior change |
| `test` | Adding or updating tests |
| `chore` | Build process, dependency updates, tooling |
| `perf` | Performance improvements |
| `ci` | CI/CD configuration changes |

### Scope

The scope identifies which part of the codebase is affected:

- `buyer` — Buyer app
- `seller` — Seller app
- `rider` — Rider app
- `api` — Backend API
- `db` — Database
- `auth` — Authentication
- `ui` — Design/UI components
- `infra` — Infrastructure/DevOps

### Subject

- Use **imperative, present tense:** "add" not "added" or "adds"
- **Lowercase** the first letter
- **No period** at the end
- **Maximum 50 characters**

### Examples

✅ **Good commits:**
```
feat(buyer): add product search screen
fix(api): resolve null pointer in order serializer
docs(readme): update installation steps
test(orders): add integration test for checkout flow
refactor(auth): extract JWT validation to middleware
chore(deps): upgrade Retrofit to 2.11.0
```

❌ **Bad commits:**
```
Updated stuff                          # No type or scope
fix: Fixed the bug.                    # Vague, has period
feat(api): Added a new endpoint        # Past tense
WIP                                    # Meaningless
```

### Multi-line Commits

For complex changes, use a body:

```
feat(orders): implement order acceptance flow

- Add accept/reject endpoints to orders API
- Update seller dashboard to show pending orders
- Send FCM notification to buyer on acceptance
- Add unit tests for new endpoints

Closes #42
```

---

## 🔍 Pull Request Process

### Before Opening a PR

- [ ] Code compiles and runs locally
- [ ] All tests pass (`pytest` for backend, `./gradlew test` for Android)
- [ ] Linter passes (`black`, `ktlint`)
- [ ] You have tested the change manually
- [ ] Documentation is updated (if applicable)
- [ ] No commented-out code or `console.log`/`println` debugging statements
- [ ] No hardcoded secrets, API keys, or credentials
- [ ] Branch is up-to-date with `main`

### Opening a PR

1. **Push your branch** to GitHub:
   ```bash
   git push -u origin feature/your-task-name
   ```

2. **Navigate to GitHub** and click "Compare & pull request"

3. **Fill out the PR template** (auto-loaded):

```markdown
## Summary
Brief description of what this PR does and why.

## Type of Change
- [ ] New feature
- [ ] Bug fix
- [ ] Documentation
- [ ] Refactor
- [ ] Test addition

## Related Issues
Closes #<issue-number>

## Changes
- Added new screen for product search
- Implemented Retrofit API call to /products/search/
- Added unit tests for SearchViewModel

## Screenshots (for UI changes)
[Insert screenshots or screen recordings here]

## Testing Steps
1. Pull this branch
2. Run the app
3. Navigate to the home screen
4. Tap "Search" and enter "rice"
5. Verify results display correctly

## Checklist
- [ ] My code follows the project style guidelines
- [ ] I have performed a self-review
- [ ] I have added tests for new functionality
- [ ] Tests pass locally
- [ ] Documentation is updated
- [ ] No secrets/credentials are committed
```

4. **Request reviews:**
   - Tag the **Project Lead** (mandatory)
   - Tag **at least 1 peer** for code review
   - For UI changes, tag the **Designer** for design review

5. **Wait for CI checks** to complete (linting, tests, build)

### PR Title Format

Follow the same Conventional Commits format:

```
feat(buyer): add product search screen
fix(api): resolve null pointer in order serializer
```

### After the PR Is Open

- **Monitor notifications** for review comments
- **Respond within 24 hours** to feedback
- **Push fixes** to the same branch (no force pushes after review starts)
- **Re-request review** after addressing all comments
- **Mark conversations resolved** as you address them

### Merging

Once approved:
- ✅ All CI checks pass
- ✅ At least 2 approvals received
- ✅ Branch is up-to-date with `main`
- ✅ No unresolved review comments

**Use "Squash and merge"** to keep the `main` branch history clean.

After merge:
```bash
# Delete your local branch
git branch -d feature/your-task-name

# Delete the remote branch (or let GitHub handle it)
git push origin --delete feature/your-task-name
```

---

## 👀 Code Review Guidelines

### For Reviewers

When reviewing a PR, check for:

#### Functionality
- [ ] Does the code do what the PR description claims?
- [ ] Are edge cases handled (empty input, null values, network errors)?
- [ ] Is error handling implemented appropriately?
- [ ] Are loading and error states present in UI?

#### Code Quality
- [ ] Is the code readable and self-documenting?
- [ ] Are variable and function names descriptive?
- [ ] Is there unnecessary complexity that could be simplified?
- [ ] Is code duplicated when it should be extracted?
- [ ] Are there commented-out blocks that should be removed?

#### Security
- [ ] Are user inputs validated?
- [ ] Are credentials/secrets in environment variables (not hardcoded)?
- [ ] Are SQL queries parameterized (no string concatenation)?
- [ ] Are sensitive endpoints protected with authentication?

#### Testing
- [ ] Are new functions/features tested?
- [ ] Do tests cover happy path AND edge cases?
- [ ] Are tests deterministic (no random failures)?

#### Standards
- [ ] Does code follow the project style guide?
- [ ] Are commits formatted correctly?
- [ ] Is the PR description clear and complete?

### Providing Feedback

Use these prefixes to communicate intent:

- **`nit:`** — Minor stylistic comment, non-blocking. *"nit: this could be a one-liner"*
- **`suggestion:`** — Optional improvement. *"suggestion: consider extracting to a helper"*
- **`question:`** — Asking for clarification. *"question: what happens if this fails?"*
- **`issue:`** — Blocking concern that must be addressed. *"issue: this could throw NPE"*
- **`praise:`** — Acknowledge good work! *"praise: love how clean this is!"*

### Sample Review Comments

✅ **Good feedback:**
> *"issue: This endpoint doesn't check if the user is the seller for this product. Could leak data across sellers. Suggest adding a permission check on line 47."*

> *"suggestion: Consider extracting the JWT parsing logic into a utility function — it's repeated in 3 places now."*

❌ **Bad feedback:**
> *"This is bad."*  (Not actionable)

> *"Why would you do it this way?"*  (Hostile tone)

> *"Just rewrite this whole file."*  (Not scoped to the PR)

### For Authors

When responding to reviews:

- **Don't take it personally** — feedback is about code, not you
- **Ask questions** if feedback is unclear
- **Push back respectfully** if you disagree, with reasoning
- **Thank reviewers** for thorough reviews
- **Mark conversations resolved** only after the issue is addressed

---

## 💻 Coding Standards

### Python (Backend)

#### Style Guide
- Follow **PEP 8**
- Use **`black`** formatter (line length: 100)
- Use **`isort`** for import sorting
- Use **`flake8`** for linting

#### Naming
- **Variables/functions:** `snake_case`
- **Classes:** `PascalCase`
- **Constants:** `UPPER_SNAKE_CASE`
- **Private methods:** `_leading_underscore`

#### Type Hints

Use type hints for function signatures:

```python
# Good
def get_product_by_id(product_id: str) -> Product | None:
    return Product.objects.filter(id=product_id).first()

# Bad
def get_product_by_id(product_id):
    return Product.objects.filter(id=product_id).first()
```

#### Docstrings

Use **Google-style** docstrings for public functions:

```python
def calculate_order_total(items: list[OrderItem], discount: float = 0.0) -> Decimal:
    """Calculate the total amount for an order.

    Args:
        items: List of order items to sum.
        discount: Discount percentage (0.0 to 1.0).

    Returns:
        Total order amount after discount.

    Raises:
        ValueError: If discount is outside valid range.
    """
    ...
```

### Kotlin (Android)

#### Style Guide
- Follow [**Kotlin Coding Conventions**](https://kotlinlang.org/docs/coding-conventions.html)
- Use **`ktlint`** for formatting and linting
- Line length: **120 characters**

#### Naming
- **Variables/functions:** `camelCase`
- **Classes:** `PascalCase`
- **Constants (companion object):** `UPPER_SNAKE_CASE`
- **Composable functions:** `PascalCase` (e.g., `BuyerHomeScreen`)
- **Resource IDs (XML):** `snake_case`

#### Prefer Immutability

```kotlin
// Good — immutable
val products = repository.getProducts()

// Bad — unnecessarily mutable
var products = repository.getProducts()
```

#### Null Safety

```kotlin
// Good — explicit null handling
val seller = order.seller ?: throw IllegalStateException("Order has no seller")

// Bad — unsafe non-null assertion
val seller = order.seller!!
```

#### KDoc Comments

Use **KDoc** for public APIs:

```kotlin
/**
 * Fetches the list of sellers near the buyer's location.
 *
 * @param lat Buyer's latitude
 * @param lng Buyer's longitude
 * @param radiusKm Search radius in kilometers (default: 5km)
 * @return List of nearby sellers sorted by distance
 */
suspend fun getNearbySellers(
    lat: Double,
    lng: Double,
    radiusKm: Double = 5.0,
): List<Seller> {
    ...
}
```

### General Rules

#### File Organization
- One class/component per file (with exceptions for small data classes)
- File name matches the primary class name
- Group related files in feature folders

#### Imports
- No wildcard imports (`import package.*`)
- Group imports: standard library → third-party → local

#### Comments
- **Write code that doesn't need comments** when possible
- Comment **why**, not **what**
- Remove commented-out code before committing

#### Magic Numbers/Strings

```kotlin
// Bad
if (status == 3) { ... }

// Good
const val ORDER_STATUS_DELIVERED = 3
if (status == ORDER_STATUS_DELIVERED) { ... }

// Even better
if (status == OrderStatus.DELIVERED) { ... }
```

---

## 🧪 Testing Requirements

### Coverage Goals
- **Backend:** ≥80% line coverage
- **Android ViewModels:** ≥75% line coverage
- **Critical paths** (auth, checkout, payment): **100%** coverage

### What to Test

#### Backend (Pytest)
- All API endpoints (happy path + error cases)
- Business logic in services
- Permissions and authorization
- Database queries (using fixtures)

#### Android (JUnit + Espresso)
- ViewModels (state transitions, business logic)
- Repository methods (with mocked data sources)
- UI flows for critical paths (login, checkout)
- Integration with Room database

### Test Structure

Use the **Arrange-Act-Assert** pattern:

```python
# Python example
def test_create_order_success():
    # Arrange
    buyer = User.objects.create(email="buyer@test.com", user_type="buyer")
    seller = Seller.objects.create(business_name="Test Shop")
    product = Product.objects.create(seller=seller, name="Rice", price=1000)
    
    # Act
    order = OrderService.create_order(
        buyer=buyer,
        items=[{"product_id": product.id, "quantity": 2}]
    )
    
    # Assert
    assert order.status == "pending"
    assert order.total_amount == 2000
    assert order.buyer == buyer
```

### Test Naming

Use descriptive names that read like sentences:

```python
# Good
def test_create_order_fails_when_product_out_of_stock():
    ...

def test_calculate_total_applies_discount_correctly():
    ...

# Bad
def test_order():
    ...

def test_1():
    ...
```

---

## 📚 Documentation Standards

### What Requires Documentation

- **New features:** Update relevant section in README
- **API changes:** Update API.md and Postman collection
- **Database changes:** Update database schema docs
- **Setup changes:** Update installation instructions
- **Breaking changes:** Add migration notes to CHANGELOG.md

### Markdown Style

- Use **ATX-style headers** (`# Header`)
- Use **fenced code blocks** with language specified
- Use **tables** for structured data
- Use **emoji sparingly** for visual hierarchy

### Inline Code Documentation

Document **complex logic** with brief explanations:

```kotlin
// We use a 30-second debounce to prevent excessive API calls
// when the user is actively typing in the search field.
private val searchDebounceMs = 30_000L
```

---

## 🐛 Issue Reporting

### Before Filing an Issue

- [ ] Check existing issues to avoid duplicates
- [ ] Try to reproduce the bug consistently
- [ ] Test against the latest `main` branch

### Bug Report Template

```markdown
## Description
Brief description of the bug.

## Steps to Reproduce
1. Open the Buyer app
2. Navigate to product search
3. Enter "rice" and tap search
4. Observe error

## Expected Behavior
Search results should display.

## Actual Behavior
App crashes with NullPointerException.

## Environment
- Device: Pixel 6 emulator
- Android version: 14
- App version: 0.1.0-dev
- Backend: localhost:8000

## Screenshots / Logs
[Attach stacktrace or screenshots]
```

### Feature Request Template

```markdown
## Problem
Buyers can't filter products by price range.

## Proposed Solution
Add a price range slider to the search screen.

## Alternatives Considered
- Min/max price input fields (less intuitive)
- Predefined price buckets (less flexible)

## Additional Context
This is a common pattern in e-commerce apps.
```

### Labels

Tag issues with appropriate labels:

| Label | Meaning |
|-------|---------|
| `bug` | Something isn't working |
| `enhancement` | New feature or improvement |
| `documentation` | Documentation update needed |
| `good-first-issue` | Easy starter task |
| `help-wanted` | Needs additional input |
| `priority:high` | Blocking other work |
| `priority:medium` | Important but not blocking |
| `priority:low` | Nice to have |
| `frontend` | Android app related |
| `backend` | Django API related |
| `design` | UI/UX related |
| `infra` | DevOps/infrastructure |

---

## 💬 Team Communication

### Channels

| Channel | Purpose |
|---------|---------|
| **Weekly standup (Friday)** | Sync on progress, blockers, plans |
| **Slack/Discord** | Day-to-day communication |
| **GitHub Issues** | Bug reports, feature discussions |
| **GitHub PRs** | Code review discussions |
| **Email** | Formal communication only |

### Response Time Expectations

| Type | Expected Response |
|------|-------------------|
| Blocking question | Within 4 hours (business hours) |
| Code review request | Within 24 hours |
| Non-urgent question | Within 48 hours |
| Weekend/holiday | No expectation |

### Status Updates

In the team chat, post a **brief daily update** when working:

```
✅ Done: Buyer home screen layout
🟡 In progress: Wiring up Retrofit for product list
🔴 Blocked: Need clarification on API response format
```

---

## ⚖️ Conflict Resolution

### Technical Disagreements

1. **Discuss in the PR comments** with reasoning and trade-offs
2. **Bring to the team chat** if unresolved after 2 rounds of comments
3. **Escalate to the Project Lead** for a final decision
4. **Document the decision** in the codebase or wiki for future reference

### Interpersonal Conflicts

1. **Address directly** with the person in private first
2. **Stay focused on behavior**, not personality
3. **Bring in a third party** (Lead) if direct conversation doesn't resolve it
4. **Take a break** if emotions are high — revisit later

### Workload Disputes

1. **Raise in standup** before the situation gets critical
2. **Lead reallocates tasks** if someone is overloaded
3. **Document new assignments** in GitHub Projects

---

## 🎓 Learning Resources

### Backend
- [Django Documentation](https://docs.djangoproject.com/)
- [DRF Documentation](https://www.django-rest-framework.org/)
- [Supabase Docs](https://supabase.com/docs)
- [Real Python](https://realpython.com/) for Python tutorials

### Android
- [Android Developers Guide](https://developer.android.com/guide)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Jetpack Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)

### Git
- [Pro Git Book](https://git-scm.com/book/en/v2) (free online)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Atlassian Git Tutorial](https://www.atlassian.com/git/tutorials)

---

## 🙏 Thank You

Following these guidelines makes our project run smoothly. If something is unclear or could be improved, **open an issue or PR** to update this document.

Let's build something great together! 🚀

---

**Maintained by:** The BulkBasket Project Team  
**Last updated:** June 2026