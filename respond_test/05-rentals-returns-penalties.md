# Rentals, Returns, Penalties

Base URL: `http://localhost:8080`

## Rentals

### GET `/api/rentals?page=0&size=10&search=RNT&status=ongoing&start_date=2026-05-01&end_date=2026-05-31`

Response success:

```json
{
  "data": {
    "content": [
      {
        "id": 1,
        "customer": {
          "id": 1,
          "name": "Budi Santoso",
          "phone": "081234567890"
        },
        "admin": {
          "id": 2,
          "name": "Admin Rental",
          "email": "rental@camera-rental.test",
          "role": "admin"
        },
        "rental_code": "RNT-DEMO001",
        "rental_date": "2026-05-18",
        "planned_return_date": "2026-05-21",
        "actual_return_date": null,
        "total_price": 750000,
        "total_paid": 250000,
        "balance_due": 500000,
        "status": "ongoing",
        "note": "Sewa untuk dokumentasi event kantor",
        "details": [
          {
            "id": 1,
            "item_status": {
              "id": 2,
              "status": "rented"
            },
            "daily_price": 250000,
            "quantity": 1,
            "subtotal": 750000
          }
        ],
        "created_at": "2026-05-18T09:00:00",
        "updated_at": "2026-05-18T09:00:00"
      }
    ],
    "page": 0,
    "size": 10,
    "total_elements": 1,
    "total_pages": 1,
    "last": true
  },
  "errors": null,
  "message": "Berhasil mengambil rental"
}
```

### GET `/api/rental-not-returned`

Mengambil semua rental dengan status `ongoing`.

Response success:

```json
{
  "data": [
    {
      "id": 1,
      "rental_code": "RNT-DEMO001",
      "total_price": 750000,
      "total_paid": 250000,
      "balance_due": 500000,
      "status": "ongoing"
    }
  ],
  "errors": null,
  "message": "Berhasil mengambil rental belum kembali"
}
```

### POST `/api/rentals`

Body dari `api.test`:

```json
{
  "customer_id": 1,
  "rental_date": "2026-05-22",
  "planned_return_date": "2026-05-24",
  "status": "pending",
  "note": "Sewa untuk demo request API",
  "details": [
    { "item_status_id": 4 },
    { "item_status_id": 11 }
  ]
}
```

Alternatif body berdasarkan item dan quantity:

```json
{
  "customer_id": 1,
  "rental_date": "2026-05-22",
  "planned_return_date": "2026-05-24",
  "status": "pending",
  "details": [
    { "item_id": 3, "quantity": 2 }
  ]
}
```

Response success:

```json
{
  "data": {
    "id": 4,
    "rental_code": "RNT-ABCD1234EFGH",
    "rental_date": "2026-05-22",
    "planned_return_date": "2026-05-24",
    "actual_return_date": null,
    "total_price": 900000,
    "total_paid": 0,
    "balance_due": 900000,
    "status": "pending",
    "note": "Sewa untuk demo request API",
    "details": [
      {
        "id": 8,
        "item_status": {
          "id": 4,
          "status": "rented"
        },
        "daily_price": 225000,
        "quantity": 1,
        "subtotal": 450000
      }
    ],
    "created_at": "2026-06-12T10:00:00",
    "updated_at": "2026-06-12T10:00:00"
  },
  "errors": null,
  "message": "Berhasil membuat rental"
}
```

Possible errors:

```json
{ "data": null, "errors": null, "message": "plannedReturnDate harus setelah rentalDate" }
```

```json
{ "data": null, "errors": null, "message": "item_status_id wajib diisi" }
```

```json
{ "data": null, "errors": null, "message": "item status tidak tersedia: 4" }
```

```json
{ "data": null, "errors": null, "message": "item status tidak boleh duplikat: 4" }
```

```json
{ "data": null, "errors": null, "message": "stok item tidak cukup" }
```

```json
{ "data": null, "errors": null, "message": "status tidak valid" }
```

```json
{
  "data": null,
  "errors": {
    "customer_id": "must not be null",
    "rental_date": "must not be null",
    "planned_return_date": "must not be null",
    "details": "must not be empty"
  },
  "message": "Validasi gagal"
}
```

### PUT `/api/rentals/{id}`

Body sama seperti create. Success message: `Berhasil mengubah rental`.

Error khusus update:

```json
{ "data": null, "errors": null, "message": "rental sudah returned" }
```

### DELETE `/api/rental-details/{id}`

Success:

```json
{
  "data": null,
  "errors": null,
  "message": "Berhasil menghapus rental detail"
}
```

Not found:

```json
{ "data": null, "errors": null, "message": "rental detail tidak ditemukan" }
```

### GET/DELETE `/api/rentals/{id}`

Not found:

```json
{ "data": null, "errors": null, "message": "rental tidak ditemukan" }
```

DELETE success:

```json
{
  "data": null,
  "errors": null,
  "message": "Berhasil menghapus rental"
}
```

## Returns

### GET `/api/returns?page=0&size=10&search=RNT-DEMO&has_penalty=true&start_date=2026-05-01&end_date=2026-05-31`

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
          "status": "returned"
        },
        "admin": {
          "id": 2,
          "name": "Admin Rental",
          "role": "admin"
        },
        "return_date": "2026-05-24",
        "condition_note": "Semua barang kembali lengkap",
        "has_penalty": false,
        "penalty_payment_method": null,
        "created_at": "2026-05-24T15:00:00",
        "updated_at": "2026-05-24T15:00:00"
      }
    ],
    "page": 0,
    "size": 10,
    "total_elements": 1,
    "total_pages": 1,
    "last": true
  },
  "errors": null,
  "message": "Berhasil mengambil return"
}
```

### POST `/api/returns`

Body:

```json
{
  "rental_id": 1,
  "return_date": "2026-05-24",
  "condition_note": "Semua barang kembali lengkap"
}
```

Response success message: `Berhasil membuat return`.

`has_penalty` tidak dikirim dari request body. Nilai ini dikelola otomatis oleh sistem: `false` saat return dibuat, berubah menjadi `true` saat penalty dibuat untuk return tersebut, dan kembali `false` saat semua penalty pada return tersebut sudah dihapus.

Possible errors:

```json
{ "data": null, "errors": null, "message": "rental sudah memiliki return" }
```

```json
{ "data": null, "errors": null, "message": "rental cancelled tidak bisa direturn" }
```

```json
{
  "data": null,
  "errors": {
    "rental_id": "must not be null",
    "return_date": "must not be null"
  },
  "message": "Validasi gagal"
}
```

### PUT `/api/returns/{id}`

Body:

```json
{
  "rental_id": 2,
  "return_date": "2026-05-12",
  "condition_note": "Barang kembali lengkap, ada gores kecil pada body kamera"
}
```

Success message: `Berhasil mengubah return`.

### GET/DELETE `/api/returns/{id}`

Not found:

```json
{ "data": null, "errors": null, "message": "return tidak ditemukan" }
```

DELETE success:

```json
{
  "data": null,
  "errors": null,
  "message": "Berhasil menghapus return"
}
```

## Penalties

### GET `/api/penalties?page=0&size=10&search=damage&status=paid&start_date=2026-05-01&end_date=2026-05-31`

Response success:

```json
{
  "data": {
    "content": [
      {
        "id": 1,
        "rental_return": {
          "id": 1,
          "return_date": "2026-05-24"
        },
        "penalty_type": "damage",
        "description": "Gores kecil pada body kamera",
        "amount": 50000,
        "total_paid": 25000,
        "balance_due": 25000,
        "status": "paid",
        "paid_at": "2026-05-12T17:00:00",
        "created_at": "2026-05-12T17:00:00",
        "updated_at": "2026-05-12T17:00:00"
      }
    ],
    "page": 0,
    "size": 10,
    "total_elements": 1,
    "total_pages": 1,
    "last": true
  },
  "errors": null,
  "message": "Berhasil mengambil denda"
}
```

### GET `/api/returns/{return_id}/penalties`

Response success message: `Berhasil mengambil denda pengembalian`.

### POST `/api/returns/{return_id}/penalties`

Body:

```json
{
  "penalty_type": "late_return",
  "description": "Keterlambatan pengembalian 1 hari",
  "amount": 100000,
  "status": "unpaid",
  "paid_at": null
}
```

Response success message: `Berhasil membuat denda`.

Possible errors:

```json
{ "data": null, "errors": null, "message": "return tidak ditemukan" }
```

```json
{ "data": null, "errors": null, "message": "penalty_type tidak valid" }
```

```json
{ "data": null, "errors": null, "message": "status tidak valid" }
```

```json
{
  "data": null,
  "errors": {
    "penalty_type": "must not be blank",
    "amount": "must not be null",
    "status": "must not be blank"
  },
  "message": "Validasi gagal"
}
```

### PUT `/api/returns/{return_id}/penalties/{penalty_id}`

Body:

```json
{
  "penalty_type": "damage",
  "description": "Gores kecil pada body kamera",
  "amount": 50000,
  "status": "paid",
  "paid_at": "2026-05-12T17:00:00"
}
```

Jika `status=paid` dan `paid_at=null`, backend otomatis mengisi `paid_at` dengan waktu saat request diproses.

### GET/DELETE `/api/returns/{return_id}/penalties/{penalty_id}`

Endpoint penalty memakai path bahasa Inggris. Gunakan `/api/penalties` atau nested route `/api/returns/{return_id}/penalties`.

Not found:

```json
{ "data": null, "errors": null, "message": "denda tidak ditemukan" }
```

DELETE success:

```json
{
  "data": null,
  "errors": null,
  "message": "Berhasil menghapus denda"
}
```
