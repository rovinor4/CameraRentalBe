Camera Rental Backend
---

## Login Demo

Akun ini dibuat otomatis oleh Flyway migration saat database pertama kali dibuat.

| Role | Email / Username | Password |
| --- | --- | --- |
| Super Admin | `admin@camera-rental.test` | `admin123` |
| Admin | `rental@camera-rental.test` | `admin123` |

Endpoint login:

```http
POST /api/auth/login
Content-Type: application/json
```

Contoh body:

```json
{
  "email": "admin@camera-rental.test",
  "password": "admin123"
}
```
