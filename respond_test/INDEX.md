# Camera Rental API Response Examples

Dokumen ini dibuat dari request di folder `api.test` dan logic aplikasi saat ini. File hanya berisi contoh response untuk frontend; nilai `id`, timestamp, token, kode rental, kode payment, dan nama file upload bisa berbeda di database lokal.

## Daftar File

- [00-auth.md](00-auth.md): login, logout, app info, error auth umum.
- [01-admins.md](01-admins.md): admin CRUD dan response khusus super admin.
- [02-master-data.md](02-master-data.md): categories, category details, customers.
- [03-items-and-statuses.md](03-items-and-statuses.md): items, storage image, item statuses.
- [04-payments.md](04-payments.md): payment methods dan payment details untuk rental/penalty.
- [05-rentals-returns-penalties.md](05-rentals-returns-penalties.md): rentals, returns, penalties.
- [06-maintenance-and-reports.md](06-maintenance-and-reports.md): maintenance, dashboard, dan CSV reports.

## Format Umum JSON

Success:

```json
{
  "data": {},
  "errors": null,
  "message": "Berhasil ..."
}
```

Error:

```json
{
  "data": null,
  "errors": null,
  "message": "..."
}
```

Validation error:

```json
{
  "data": null,
  "errors": {
    "field_name": "must not be blank"
  },
  "message": "Validasi gagal"
}
```

Pagination:

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
  "message": "Berhasil mengambil ..."
}
```

## Enum Yang Dipakai Frontend

- `admin.role`: `super_admin`, `admin`
- `customer.identity_type`: `id_card`, `driver_license`
- `item_status.status`: `available`, `rented`, `maintenance`, `inactive`
- `payment_method.type`: `qr_code`, `bank_transfer`, `cash`, `e_wallet`
- `payment_method.content_type`: `image`, `text`
- `rental.status`: `pending`, `ongoing`, `returned`, `cancelled`
- `payment_detail.status`: `pending`, `paid`, `failed`
- `penalty.penalty_type`: `late_return`, `damage`, `lost_item`, `other`
- `penalty.status`: `unpaid`, `paid`
- `maintenance.status`: `in_progress`, `completed`

## Error Umum

`401 Unauthorized`:

```json
{
  "data": null,
  "errors": null,
  "message": "Token tidak valid"
}
```

`403 Forbidden`:

```json
{
  "data": null,
  "errors": null,
  "message": "Akses ditolak"
}
```

`405 Method Not Allowed`:

```json
{
  "data": null,
  "errors": null,
  "message": "Method tidak diizinkan"
}
```

`500 Internal Server Error`:

```json
{
  "data": null,
  "errors": null,
  "message": "Terjadi kesalahan server"
}
```
