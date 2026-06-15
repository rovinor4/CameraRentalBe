# Admin Responses

Base URL: `http://localhost:8080`

Semua endpoint `/api/admins` hanya untuk `super_admin`.

## GET `/api/admins?page=0&size=10`

Query tambahan dari `api.test`:

- `search=rental`
- `role=admin`
- `sort=name`
- `direction=asc`
- `start_date=2026-01-01`
- `end_date=2026-12-31`

Response success:

```json
{
  "data": {
    "content": [
      {
        "id": 2,
        "name": "Admin Rental",
        "email": "rental@camera-rental.test",
        "role": "admin",
        "created_at": "2026-05-01T08:00:00",
        "updated_at": "2026-05-01T08:00:00"
      }
    ],
    "page": 0,
    "size": 10,
    "total_elements": 1,
    "total_pages": 1,
    "last": true
  },
  "errors": null,
  "message": "Berhasil mengambil admin"
}
```

Response empty:

```json
{
  "data": {
    "content": [],
    "page": 0,
    "size": 10,
    "total_elements": 0,
    "total_pages": 0,
    "last": true
  },
  "errors": null,
  "message": "Berhasil mengambil admin"
}
```

## GET `/api/admins/{id}`

Response success:

```json
{
  "data": {
    "id": 1,
    "name": "Super Admin",
    "email": "admin@camera-rental.test",
    "role": "super_admin",
    "created_at": "2026-05-01T08:00:00",
    "updated_at": "2026-05-01T08:00:00"
  },
  "errors": null,
  "message": "Berhasil mengambil admin"
}
```

Response not found:

```json
{
  "data": null,
  "errors": null,
  "message": "admin tidak ditemukan"
}
```

Response: `404 Not Found`

## POST `/api/admins`

Body:

```json
{
  "name": "Admin Demo Baru",
  "email": "admin.demo@camera-rental.test",
  "password": "admin123",
  "role": "admin"
}
```

Response success:

```json
{
  "data": {
    "id": 3,
    "name": "Admin Demo Baru",
    "email": "admin.demo@camera-rental.test",
    "role": "admin",
    "created_at": "2026-06-12T10:00:00",
    "updated_at": "2026-06-12T10:00:00"
  },
  "errors": null,
  "message": "Berhasil membuat admin"
}
```

Possible error:

```json
{
  "data": null,
  "errors": null,
  "message": "email sudah digunakan"
}
```

```json
{
  "data": null,
  "errors": null,
  "message": "password wajib diisi"
}
```

```json
{
  "data": null,
  "errors": null,
  "message": "role tidak valid"
}
```

Validation error:

```json
{
  "data": null,
  "errors": {
    "name": "must not be blank",
    "email": "must be a well-formed email address",
    "role": "must not be blank"
  },
  "message": "Validasi gagal"
}
```

## PUT `/api/admins/{id}`

Body:

```json
{
  "name": "Admin Rental Updated",
  "email": "rental@camera-rental.test",
  "password": "",
  "role": "admin"
}
```

Response success:

```json
{
  "data": {
    "id": 2,
    "name": "Admin Rental Updated",
    "email": "rental@camera-rental.test",
    "role": "admin",
    "created_at": "2026-05-01T08:00:00",
    "updated_at": "2026-06-12T10:00:00"
  },
  "errors": null,
  "message": "Berhasil mengubah admin"
}
```

Catatan: `password` kosong berarti password lama tetap dipakai.

## DELETE `/api/admins/{id}`

Response success:

```json
{
  "data": null,
  "errors": null,
  "message": "Berhasil menghapus admin"
}
```

Response not found:

```json
{
  "data": null,
  "errors": null,
  "message": "admin tidak ditemukan"
}
```
