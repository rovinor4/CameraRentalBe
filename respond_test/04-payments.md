# Payment Methods & Payment Details

Base URL: `http://localhost:8080`

## Payment Methods

Admin biasa hanya melihat payment method `active=true`; super admin bisa melihat semua.

### GET `/api/payment-methods?page=0&size=10&content_type=text`

Response success:

```json
{
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Cash",
        "type": "cash",
        "content_type": "text",
        "content_value": "Bayar tunai di kasir",
        "image_upload": null,
        "active": true,
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
  "message": "Berhasil mengambil payment method"
}
```

### POST `/api/payment-methods` text

Body:

```json
{
  "name": "Mandiri",
  "type": "bank_transfer",
  "content_type": "text",
  "content_value": "Mandiri 9876543210 a.n Camera Rental",
  "active": true
}
```

Response success message: `Berhasil membuat payment method`.

### POST `/api/payment-methods` image

Multipart body:

```text
name=QRIS Outlet
type=qr_code
content_type=image
active=true
image_upload=<file qris.png>
```

Response success data:

```json
{
  "id": 5,
  "name": "QRIS Outlet",
  "type": "qr_code",
  "content_type": "image",
  "content_value": null,
  "image_upload": "payment-methods/uuid-qris.png",
  "active": true,
  "created_at": "2026-06-12T10:00:00",
  "updated_at": "2026-06-12T10:00:00"
}
```

Possible errors:

```json
{ "data": null, "errors": null, "message": "type tidak valid" }
```

```json
{ "data": null, "errors": null, "message": "content_type tidak valid" }
```

```json
{ "data": null, "errors": null, "message": "content_value wajib diisi jika content_type text" }
```

```json
{ "data": null, "errors": null, "message": "image_upload wajib diisi jika content_type image" }
```

```json
{
  "data": null,
  "errors": {
    "name": "must not be blank",
    "type": "must not be blank",
    "content_type": "must not be blank",
    "active": "must not be null"
  },
  "message": "Validasi gagal"
}
```

### GET/PUT/DELETE `/api/payment-methods/{id}`

Not found:

```json
{ "data": null, "errors": null, "message": "payment method tidak ditemukan" }
```

Admin biasa akses payment method inactive:

```json
{ "data": null, "errors": null, "message": "payment method tidak aktif" }
```

Success messages:

- GET: `Berhasil mengambil payment method`
- PUT: `Berhasil mengubah payment method`
- DELETE: `Berhasil menghapus payment method`

### GET `/storage/payment-methods/{filename}`

Response success berupa binary file, bukan JSON. Not found message: `File tidak ditemukan`.

## Payment Details

Payment detail menghubungkan `payment_method` dengan salah satu target: `rental_id` atau `penalty_id`.

### GET `/api/payment-details?page=0&size=10&search=PAY&status=paid&start_date=2026-05-01&end_date=2026-05-31`

Response success:

```json
{
  "data": {
    "content": [
      {
        "id": 1,
        "rental": {
          "id": 1,
          "rental_code": "RNT-DEMO001",
          "total_price": 350000,
          "status": "ongoing"
        },
        "penalty": null,
        "payment_method": {
          "id": 1,
          "name": "Cash",
          "type": "cash",
          "active": true
        },
        "payment_code": "PAY-ABCD1234EFGH",
        "amount": 350000,
        "payment_date": "2026-05-22T13:30:00",
        "status": "paid",
        "proof_image": "rental-payments/uuid-proof.jpg",
        "created_at": "2026-05-22T13:30:00",
        "updated_at": "2026-05-22T13:30:00"
      }
    ],
    "page": 0,
    "size": 10,
    "total_elements": 1,
    "total_pages": 1,
    "last": true
  },
  "errors": null,
  "message": "Berhasil mengambil rental payment"
}
```

### GET `/api/payment-details/rental/{rental_id}`

Response success message: `Berhasil mengambil payment detail rental`.

### GET `/api/payment-details/penalty/{penalty_id}`

Response success message: `Berhasil mengambil payment detail penalty`.

### POST `/api/payment-details/rental/{rental_id}`

Multipart body:

```text
rental_id=1
payment_method_id=1
amount=350000
payment_date=2026-05-22T13:30:00
status=paid
proof_image=<file proof.jpg>
```

Response success message: `Berhasil membuat rental payment`. `payment_code` dibuat otomatis dengan prefix `PAY-`.

### POST `/api/payment-details/penalty/{penalty_id}`

JSON body:

```json
{
  "payment_method_id": 1,
  "amount": 50000,
  "payment_date": "2026-05-22T15:00:00",
  "status": "paid",
  "proof_image": "rental-payments/manual-proof.jpg"
}
```

Response success message: `Berhasil membuat payment detail penalty`.

Possible errors:

```json
{ "data": null, "errors": null, "message": "rental tidak ditemukan" }
```

```json
{ "data": null, "errors": null, "message": "payment method tidak ditemukan" }
```

```json
{ "data": null, "errors": null, "message": "payment method tidak aktif" }
```

```json
{ "data": null, "errors": null, "message": "status tidak valid" }
```

```json
{ "data": null, "errors": null, "message": "proof_image_upload wajib diisi" }
```

```json
{
  "data": null,
  "errors": {
    "payment_method_id": "must not be null",
    "amount": "must not be null",
    "status": "must not be blank"
  },
  "message": "Validasi gagal"
}
```

```json
{ "data": null, "errors": null, "message": "rental_id atau penalty_id wajib diisi" }
```

```json
{ "data": null, "errors": null, "message": "payment detail hanya boleh punya satu target rental_id atau penalty_id" }
```

### PUT `/api/payment-details/{id}`

Body sama seperti create. Pada update, `proof_image` atau `proof_image_upload` boleh tidak dikirim; file lama tetap dipakai.

Success message: `Berhasil mengubah payment detail`.

### GET/DELETE `/api/payment-details/{id}`

Not found:

```json
{ "data": null, "errors": null, "message": "rental payment tidak ditemukan" }
```

DELETE success:

```json
{
  "data": null,
  "errors": null,
  "message": "Berhasil menghapus rental payment"
}
```

Endpoint lama `/api/rental-payments` tetap tersedia untuk kompatibilitas.
