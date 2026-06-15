# Auth & App Info Responses

Base URL: `http://localhost:8080`

Semua endpoint private butuh header:

```http
Authorization: Bearer <token>
```

Format JSON global:

```json
{
  "data": {},
  "errors": null,
  "message": "..."
}
```

## POST `/api/auth/login`

Body success:

```json
{
  "email": "admin@camera-rental.test",
  "password": "admin123"
}
```

Response `200 OK`:

```json
{
  "data": {
    "token": "RANDOM_100_CHAR_TOKEN",
    "data": {
      "id": 1,
      "name": "Super Admin",
      "email": "admin@camera-rental.test",
      "role": "super_admin",
      "created_at": "2026-05-01T08:00:00",
      "updated_at": "2026-05-01T08:00:00"
    }
  },
  "errors": null,
  "message": "Login berhasil"
}
```

Body invalid credential:

```json
{
  "email": "admin@camera-rental.test",
  "password": "wrong-password"
}
```

Response `400 Bad Request`:

```json
{
  "data": null,
  "errors": null,
  "message": "Email / Password salah"
}
```

Body validation error:

```json
{
  "email": "",
  "password": ""
}
```

Response `400 Bad Request`:

```json
{
  "data": null,
  "errors": {
    "email": "must not be blank",
    "password": "must not be blank"
  },
  "message": "Validasi gagal"
}
```

## DELETE `/api/auth/logout`

Response `200 OK`:

```json
{
  "data": null,
  "errors": null,
  "message": "Logout berhasil"
}
```

Catatan: logout tetap `200 OK` walaupun token tidak ditemukan, selama endpoint berhasil dipanggil.

## GET `/api`

Response `200 OK`:

```json
{
  "data": {
    "app_name": "Camera Rental",
    "version": "1.0.00"
  },
  "errors": null,
  "message": "Berhasil mengambil informasi aplikasi"
}
```

## Error Auth Umum

Tanpa header, token salah, atau token expired:

```json
{
  "data": null,
  "errors": null,
  "message": "Token tidak valid"
}
```

Response: `401 Unauthorized`

Admin biasa akses endpoint super admin:

```json
{
  "data": null,
  "errors": null,
  "message": "Akses ditolak"
}
```

Response: `403 Forbidden`

Method tidak sesuai:

```json
{
  "data": null,
  "errors": null,
  "message": "Method tidak diizinkan"
}
```

Response: `405 Method Not Allowed`
