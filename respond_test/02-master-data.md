# Categories, Category Details, Customers

Base URL: `http://localhost:8080`

## Categories

### GET `/api/categories?page=0&size=10&search=Camera`

Response success:

```json
{
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Camera",
        "description": "Kamera untuk kebutuhan foto dan video profesional",
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
  "message": "Berhasil mengambil category"
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
  "message": "Berhasil mengambil category"
}
```

### GET `/api/categories/{id}`

Response not found:

```json
{
  "data": null,
  "errors": null,
  "message": "category tidak ditemukan"
}
```

### POST `/api/categories`

Body:

```json
{
  "name": "Lighting",
  "description": "Lampu dan perlengkapan lighting"
}
```

Response success:

```json
{
  "data": {
    "id": 4,
    "name": "Lighting",
    "description": "Lampu dan perlengkapan lighting",
    "created_at": "2026-06-12T10:00:00",
    "updated_at": "2026-06-12T10:00:00"
  },
  "errors": null,
  "message": "Berhasil membuat category"
}
```

Validation error:

```json
{
  "data": null,
  "errors": {
    "name": "must not be blank"
  },
  "message": "Validasi gagal"
}
```

### PUT `/api/categories/{id}`

Body sama seperti create. Message success: `Berhasil mengubah category`.

### DELETE `/api/categories/{id}`

Response success:

```json
{
  "data": null,
  "errors": null,
  "message": "Berhasil menghapus category"
}
```

## Category Details

### GET `/api/categories-detail/get/{category_id}`

Response success:

```json
{
  "data": [
    {
      "id": 1,
      "category": {
        "id": 1,
        "name": "Camera",
        "description": "Kamera untuk kebutuhan foto dan video profesional",
        "created_at": "2026-05-01T08:00:00",
        "updated_at": "2026-05-01T08:00:00"
      },
      "name": "Mirrorless",
      "description": "Kamera mirrorless untuk foto dan video profesional",
      "created_at": "2026-05-01T08:00:00",
      "updated_at": "2026-05-01T08:00:00"
    }
  ],
  "errors": null,
  "message": "Berhasil mengambil category detail berdasarkan category"
}
```

Endpoint lama `GET /api/category-details?page=0&size=10&search=Mirrorless` tetap tersedia untuk list paginated.

### POST `/api/categories-detail/create/{category_id}`

Body:

```json
{
  "name": "Memory Card",
  "description": "Kartu memori dan storage"
}
```

Response success message: `Berhasil membuat category detail`.

Possible errors:

```json
{
  "data": null,
  "errors": null,
  "message": "category tidak ditemukan"
}
```

```json
{
  "data": null,
  "errors": {
    "name": "must not be blank"
  },
  "message": "Validasi gagal"
}
```

### PUT `/api/categories-detail/update/{category_id}/{category_detail_id}`

Body:

```json
{
  "name": "Mirrorless",
  "description": "Kamera mirrorless untuk foto dan video profesional"
}
```

### DELETE `/api/categories-detail/delete/{category_id}/{category_detail_id}`

Endpoint lama `GET/PUT/DELETE /api/category-details/{id}` tetap tersedia.

Not found:

```json
{
  "data": null,
  "errors": null,
  "message": "category detail tidak ditemukan"
}
```

Success messages:

- GET: `Berhasil mengambil category detail`
- PUT: `Berhasil mengubah category detail`
- DELETE: `Berhasil menghapus category detail`

## Customers

### GET `/api/customers?page=0&size=10&search=Budi&identity_type=id_card`

Response success:

```json
{
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Budi Santoso",
        "phone": "081234567890",
        "address": "Jl. Melati No. 12, Jakarta",
        "identity_type": "id_card",
        "identity_number": "3173010101900001",
        "identity_image": "customers/ktp-budi.jpg",
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
  "message": "Berhasil mengambil customer"
}
```

### POST `/api/customers`

Multipart body:

```text
name=Dewi Lestari
phone=083344455566
address=Jl. Mawar No. 7, Depok
identity_type=id_card
identity_number=3275015505960004
identity_image=<file ktp.jpg>
```

JSON body juga didukung, tetapi create tetap membutuhkan `identity_image_upload`; jadi frontend disarankan memakai multipart untuk create.

Response success:

```json
{
  "data": {
    "id": 4,
    "name": "Dewi Lestari",
    "phone": "083344455566",
    "address": "Jl. Mawar No. 7, Depok",
    "identity_type": "id_card",
    "identity_number": "3275015505960004",
    "identity_image": "customers/uuid-ktp.jpg",
    "created_at": "2026-06-12T10:00:00",
    "updated_at": "2026-06-12T10:00:00"
  },
  "errors": null,
  "message": "Berhasil membuat customer"
}
```

Possible errors:

```json
{
  "data": null,
  "errors": null,
  "message": "identity_type tidak valid"
}
```

```json
{
  "data": null,
  "errors": null,
  "message": "identity_image_upload wajib diisi"
}
```

```json
{
  "data": null,
  "errors": null,
  "message": "Format image harus png, jpg, jpeg, webp, atau avif"
}
```

```json
{
  "data": null,
  "errors": null,
  "message": "Ukuran image maksimal kurang dari 3MB"
}
```

### PUT `/api/customers/{id}`

Multipart body sama seperti create. `identity_image` boleh tidak dikirim saat update; file lama tetap dipakai.

Success message: `Berhasil mengubah customer`.

### GET/DELETE `/api/customers/{id}`

Not found:

```json
{
  "data": null,
  "errors": null,
  "message": "customer tidak ditemukan"
}
```

DELETE success:

```json
{
  "data": null,
  "errors": null,
  "message": "Berhasil menghapus customer"
}
```
