# Items & Item Statuses

Base URL: `http://localhost:8080`

Endpoint create/update/delete item dan item status hanya untuk `super_admin`.

## Items

### GET `/api/items?page=0&size=10&search=Sony&sort=name&direction=asc`

Response success:

```json
{
  "data": {
    "content": [
      {
        "id": 1,
        "category": {
          "id": 1,
          "name": "Camera",
          "description": "Kamera untuk kebutuhan foto dan video profesional",
          "created_at": "2026-05-01T08:00:00",
          "updated_at": "2026-05-01T08:00:00"
        },
        "category_detail": {
          "id": 1,
          "name": "Mirrorless",
          "description": "Kamera mirrorless untuk foto dan video profesional",
          "created_at": "2026-05-01T08:00:00",
          "updated_at": "2026-05-01T08:00:00"
        },
        "name": "Sony A7 III",
        "brand": "Sony",
        "model": "A7 III",
        "serial_number": "SONY-A7III-001",
        "description": "Full-frame mirrorless camera",
        "daily_price": 250000,
        "stock": 2,
        "image": "public/sony-a7iii.jpg",
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
  "message": "Berhasil mengambil item"
}
```

Response empty sama seperti pagination lain, `content: []`, message tetap `Berhasil mengambil item`.

### GET `/api/items/available?page=0&size=10`

Response success message: `Berhasil mengambil item available`. Isi `content` berisi item yang punya minimal satu `item_status` dengan status `available`.

### GET `/api/items/{itemId}/available-item-status`

Response success:

```json
{
  "data": {
    "id": 4,
    "item": {
      "id": 3,
      "name": "Canon EOS R6",
      "brand": "Canon",
      "model": "EOS R6",
      "serial_number": "CANON-R6-001",
      "daily_price": 275000,
      "stock": 3,
      "image": "public/canon-r6.jpg"
    },
    "status": "available",
    "created_at": "2026-05-01T08:00:00",
    "updated_at": "2026-05-01T08:00:00"
  },
  "errors": null,
  "message": "Berhasil mengambil item status available"
}
```

Not found:

```json
{
  "data": null,
  "errors": null,
  "message": "item status available tidak ditemukan"
}
```

### POST `/api/items`

Multipart body:

```text
category_id=1
category_detail_id=1
name=Fujifilm X-T5
brand=Fujifilm
model=X-T5
serial_number=FUJI-XT5-001
description=Mirrorless APS-C high resolution
daily_price=225000
stock=2
image_upload=<file item.jpg>
```

Response success:

```json
{
  "data": {
    "id": 7,
    "category": {
      "id": 1,
      "name": "Camera"
    },
    "category_detail": {
      "id": 1,
      "name": "Mirrorless"
    },
    "name": "Fujifilm X-T5",
    "brand": "Fujifilm",
    "model": "X-T5",
    "serial_number": "FUJI-XT5-001",
    "description": "Mirrorless APS-C high resolution",
    "daily_price": 225000,
    "stock": 2,
    "image": "public/uuid-item.jpg",
    "created_at": "2026-06-12T10:00:00",
    "updated_at": "2026-06-12T10:00:00"
  },
  "errors": null,
  "message": "Berhasil membuat item"
}
```

Possible errors:

```json
{ "data": null, "errors": null, "message": "stock wajib diisi" }
```

```json
{ "data": null, "errors": null, "message": "image_upload wajib diisi" }
```

```json
{ "data": null, "errors": null, "message": "category tidak ditemukan" }
```

```json
{ "data": null, "errors": null, "message": "category detail tidak ditemukan" }
```

```json
{
  "data": null,
  "errors": {
    "category_id": "must not be null",
    "name": "must not be blank",
    "daily_price": "must not be null"
  },
  "message": "Validasi gagal"
}
```

### PUT `/api/items/{id}`

Multipart body sama seperti create. Pada update, `stock` tidak dipakai untuk set langsung; jumlah stock disinkronkan dari jumlah item status.

Success message: `Berhasil mengubah item`.

### DELETE `/api/items/{id}`

Success:

```json
{
  "data": null,
  "errors": null,
  "message": "Berhasil menghapus item"
}
```

Not found:

```json
{ "data": null, "errors": null, "message": "item tidak ditemukan" }
```

### GET `/storage/{filename}`

Response success bukan JSON. API mengembalikan file binary dengan header:

```http
Content-Type: image/jpeg
Cache-Control: public, max-age=31536000
```

Not found:

```json
{ "data": null, "errors": null, "message": "File tidak ditemukan" }
```

## Item Statuses

### GET `/api/item-statuses?page=0&size=10&status=available`

Response success:

```json
{
  "data": {
    "content": [
      {
        "id": 1,
        "item": {
          "id": 1,
          "name": "Sony A7 III",
          "brand": "Sony",
          "model": "A7 III"
        },
        "status": "available",
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
  "message": "Berhasil mengambil item status"
}
```

### POST `/api/items/{itemId}/item-statuses`

Body:

```json
{
  "status": "available"
}
```

Response success message: `Berhasil membuat item status`. Jika `status` kosong/null saat create, default menjadi `available`.

### PATCH or PUT `/api/item-statuses/{id}`

Body:

```json
{
  "status": "inactive"
}
```

Response success message: `Berhasil mengubah item status`.

Possible errors:

```json
{ "data": null, "errors": null, "message": "status wajib diisi" }
```

```json
{ "data": null, "errors": null, "message": "status tidak valid" }
```

```json
{ "data": null, "errors": null, "message": "item status tidak ditemukan" }
```

### DELETE `/api/item-statuses/{id}`

Success:

```json
{
  "data": null,
  "errors": null,
  "message": "Berhasil menghapus item status"
}
```
