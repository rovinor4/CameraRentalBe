# Maintenance, Dashboard & Reports

Base URL: `http://localhost:8080`

## Maintenance

Semua create/update/delete maintenance hanya untuk `super_admin`.

### GET `/api/maintenance?page=0&size=10&search=Kalibrasi&status=in_progress&start_date=2026-05-01&end_date=2026-05-31`

Response success:

```json
{
  "data": {
    "content": [
      {
        "id": 1,
        "item_status": {
          "id": 3,
          "status": "maintenance"
        },
        "admin": {
          "id": 1,
          "name": "Super Admin",
          "role": "super_admin"
        },
        "title": "Sensor cleaning",
        "description": "Pembersihan sensor dan pengecekan shutter",
        "maintenance_date": "2026-05-25",
        "cost": 200000,
        "status": "in_progress",
        "created_at": "2026-05-25T09:00:00",
        "updated_at": "2026-05-25T09:00:00"
      }
    ],
    "page": 0,
    "size": 10,
    "total_elements": 1,
    "total_pages": 1,
    "last": true
  },
  "errors": null,
  "message": "Berhasil mengambil maintenance"
}
```

### POST `/api/maintenance`

Body:

```json
{
  "item_status_id": 3,
  "title": "Sensor cleaning",
  "description": "Pembersihan sensor dan pengecekan shutter",
  "maintenance_date": "2026-05-25",
  "cost": 200000,
  "status": "in_progress"
}
```

Alternatif: kirim `item_id` tanpa `item_status_id`; backend akan mengambil satu status item yang available.

Response success:

```json
{
  "data": {
    "id": 3,
    "item_status": {
      "id": 3,
      "status": "maintenance"
    },
    "title": "Sensor cleaning",
    "description": "Pembersihan sensor dan pengecekan shutter",
    "maintenance_date": "2026-05-25",
    "cost": 200000,
    "status": "in_progress",
    "created_at": "2026-06-12T10:00:00",
    "updated_at": "2026-06-12T10:00:00"
  },
  "errors": null,
  "message": "Berhasil membuat maintenance"
}
```

Efek status:

- `in_progress`: item status menjadi `maintenance`
- `completed`: item status menjadi `available`

Possible errors:

```json
{ "data": null, "errors": null, "message": "item status tidak ditemukan" }
```

```json
{ "data": null, "errors": null, "message": "item status available tidak ditemukan" }
```

```json
{ "data": null, "errors": null, "message": "status tidak valid" }
```

```json
{
  "data": null,
  "errors": {
    "title": "must not be blank",
    "maintenance_date": "must not be null",
    "cost": "must not be null",
    "status": "must not be blank"
  },
  "message": "Validasi gagal"
}
```

### PUT `/api/maintenance/{id}`

Body:

```json
{
  "item_status_id": 8,
  "title": "Kalibrasi gimbal selesai",
  "description": "Kalibrasi motor dan pengecekan baterai selesai",
  "maintenance_date": "2026-05-20",
  "cost": 150000,
  "status": "completed"
}
```

Success message: `Berhasil mengubah maintenance`.

### GET/DELETE `/api/maintenance/{id}`

Not found:

```json
{ "data": null, "errors": null, "message": "maintenance tidak ditemukan" }
```

DELETE success:

```json
{
  "data": null,
  "errors": null,
  "message": "Berhasil menghapus maintenance"
}
```

## Reports CSV

## Dashboard

### GET `/api/dashboard`

Response success:

```json
{
  "data": {
    "total_barang": 12,
    "total_rental": 8,
    "total_pelanggan": 5,
    "total_pendapatan": 400000
  },
  "errors": null,
  "message": "Berhasil mengambil dashboard"
}
```

Endpoint report mengembalikan `text/csv`, bukan JSON, jika success.

Header success:

```http
HTTP/1.1 200 OK
Content-Type: text/csv
Content-Disposition: attachment; filename="rentals.csv"
```

### GET `/api/reports/rentals.csv?start_date=2026-05-01&end_date=2026-05-31`

Contoh body:

```csv
id,rental_code,customer,admin,rental_date,planned_return_date,actual_return_date,total_price,status
1,RNT-DEMO001,Budi Santoso,Admin Rental,2026-05-18,2026-05-21,,750000,ongoing
```

Empty result:

```csv
id,rental_code,customer,admin,rental_date,planned_return_date,actual_return_date,total_price,status
```

### GET `/api/reports/returns.csv?start_date=2026-05-01&end_date=2026-05-31`

```csv
id,rental_code,admin,return_date,has_penalty,condition_note
1,RNT-DEMO001,Admin Rental,2026-05-24,false,Semua barang kembali lengkap
```

### GET `/api/reports/penalties.csv?start_date=2026-05-01&end_date=2026-05-31`

```csv
id,return_id,penalty_type,description,amount,status,paid_at
1,1,damage,Gores kecil pada body kamera,50000,paid,2026-05-12T17:00:00
```

### GET `/api/reports/customers.csv`

```csv
id,name,phone,address,identity_type,identity_number,identity_image
1,Budi Santoso,081234567890,"Jl. Melati No. 12, Jakarta",id_card,3173010101900001,customers/ktp-budi.jpg
```

### GET `/api/reports/items.csv`

Super admin only.

```csv
id,name,category,category_detail,brand,model,serial_number,daily_price,stock,available_count,rented_count,maintenance_count,inactive_count
1,Sony A7 III,Camera,Mirrorless,Sony,A7 III,SONY-A7III-001,250000,2,1,1,0,0
```

Admin biasa response:

```json
{
  "data": null,
  "errors": null,
  "message": "Akses ditolak"
}
```

Response: `403 Forbidden`

### GET `/api/reports/payments.csv?start_date=2026-05-01&end_date=2026-05-31`

Super admin only.

```csv
id,payment_code,rental_code,penalty_id,payment_method,amount,payment_date,status,proof_image
1,PAY-ABCD1234EFGH,RNT-DEMO001,,Cash,350000,2026-05-22T13:30:00,paid,rental-payments/uuid-proof.jpg
2,PAY-IJKL5678MNOP,,1,Cash,50000,2026-05-22T15:00:00,paid,rental-payments/manual-proof.jpg
```

## Report Error

Token invalid:

```json
{
  "data": null,
  "errors": null,
  "message": "Token tidak valid"
}
```

Date format invalid, misalnya `start_date=2026/05/01`, diproses sebagai bad request oleh Spring dan bisa masuk handler umum:

```json
{
  "data": null,
  "errors": null,
  "message": "Terjadi kesalahan server"
}
```
