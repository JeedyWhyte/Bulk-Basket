# API Documentation

See [API.md](../API.md) in the project root for the full API reference.

## Quick Links
- Base URL (local): `http://localhost:8000/api/v1/`
- Auth: JWT Bearer token via `Authorization: Bearer <token>`
- All responses follow the format:
```json
  {
    "status": "success",
    "message": "...",
    "data": {}
  }
```