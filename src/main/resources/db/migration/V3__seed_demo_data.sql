INSERT INTO admins (name, email, password, role)
VALUES
    ('Admin Rental', 'rental@camera-rental.test', '$2a$10$QGTrNURXW/RbmBG/hSG5Hed/N5pPfpWfQDHdX3Zle.n8og8Cnky/q', 'admin');

INSERT INTO customers (name, phone, address, identity_type, identity_number, identity_image)
VALUES
    ('Budi Santoso', '081234567890', 'Jl. Melati No. 12, Jakarta', 'id_card', '3173010101900001', 'storage/identities/budi-ktp.jpg'),
    ('Siti Aminah', '081298765432', 'Jl. Kenanga No. 8, Bandung', 'id_card', '3273014402920002', 'storage/identities/siti-ktp.jpg'),
    ('Raka Pratama', '082112223333', 'Jl. Sudirman No. 10, Tangerang', 'driver_license', 'SIM-A-123456789', 'storage/identities/raka-sim.jpg');

INSERT INTO categories (name, description)
VALUES
    ('Camera', 'Kamera untuk kebutuhan foto dan video'),
    ('Lens', 'Lensa kamera berbagai focal length'),
    ('Accessory', 'Aksesoris pendukung penyewaan kamera');

INSERT INTO category_details (category_id, name, description)
VALUES
    ((SELECT id FROM categories WHERE name = 'Camera'), 'Mirrorless', 'Kamera mirrorless untuk foto dan video'),
    ((SELECT id FROM categories WHERE name = 'Camera'), 'DSLR', 'Kamera DSLR profesional'),
    ((SELECT id FROM categories WHERE name = 'Lens'), 'Prime Lens', 'Lensa fixed focal length'),
    ((SELECT id FROM categories WHERE name = 'Lens'), 'Zoom Lens', 'Lensa zoom fleksibel'),
    ((SELECT id FROM categories WHERE name = 'Accessory'), 'Stabilizer', 'Stabilizer dan gimbal'),
    ((SELECT id FROM categories WHERE name = 'Accessory'), 'Tripod', 'Tripod dan support kamera');

INSERT INTO items (category_id, category_detail_id, name, brand, model, serial_number, description, daily_price, stock, status, image)
VALUES
    ((SELECT id FROM categories WHERE name = 'Camera'), (SELECT id FROM category_details WHERE name = 'Mirrorless'), 'Sony A7 III', 'Sony', 'A7 III', 'SONY-A7III-001', 'Full-frame mirrorless camera', 250000.00, 2, 'available', 'storage/items/sony-a7iii.jpg'),
    ((SELECT id FROM categories WHERE name = 'Camera'), (SELECT id FROM category_details WHERE name = 'Mirrorless'), 'Canon EOS R6', 'Canon', 'EOS R6', 'CANON-R6-001', 'Mirrorless camera untuk foto dan video', 300000.00, 1, 'available', 'storage/items/canon-r6.jpg'),
    ((SELECT id FROM categories WHERE name = 'Lens'), (SELECT id FROM category_details WHERE name = 'Prime Lens'), 'Canon RF 50mm f1.8', 'Canon', 'RF 50mm f1.8', 'CANON-RF50-001', 'Prime lens portrait', 85000.00, 3, 'available', 'storage/items/canon-rf50.jpg'),
    ((SELECT id FROM categories WHERE name = 'Lens'), (SELECT id FROM category_details WHERE name = 'Zoom Lens'), 'Sony FE 24-70mm f2.8 GM', 'Sony', 'FE 24-70 GM', 'SONY-2470GM-001', 'Zoom lens profesional', 175000.00, 1, 'available', 'storage/items/sony-2470gm.jpg'),
    ((SELECT id FROM categories WHERE name = 'Accessory'), (SELECT id FROM category_details WHERE name = 'Stabilizer'), 'DJI RS 3 Mini', 'DJI', 'RS 3 Mini', 'DJI-RS3M-001', 'Stabilizer kamera mirrorless', 125000.00, 1, 'maintenance', 'storage/items/dji-rs3-mini.jpg'),
    ((SELECT id FROM categories WHERE name = 'Accessory'), (SELECT id FROM category_details WHERE name = 'Tripod'), 'Manfrotto Compact Action', 'Manfrotto', 'Compact Action', 'MANFROTTO-CA-001', 'Tripod compact untuk kamera', 50000.00, 4, 'available', 'storage/items/manfrotto-compact.jpg');

INSERT INTO payment_methods (name, type, content_type, content_value, is_active)
VALUES
    ('Cash', 'cash', 'text', 'Bayar tunai di kasir', TRUE),
    ('Bank BCA', 'bank_transfer', 'text', 'BCA 1234567890 a.n Camera Rental', TRUE),
    ('QRIS Camera Rental', 'qr_code', 'image', 'storage/payments/qris-camera-rental.png', TRUE),
    ('GoPay', 'e_wallet', 'text', 'GoPay 081234567890 a.n Camera Rental', TRUE);

INSERT INTO rentals (customer_id, admin_id, rental_code, rental_date, planned_return_date, actual_return_date, total_price, status, note)
VALUES
    ((SELECT id FROM customers WHERE identity_number = '3173010101900001'), (SELECT id FROM admins WHERE email = 'rental@camera-rental.test'), 'RNT-DEMO-001', '2026-05-18', '2026-05-21', NULL, 850000.00, 'ongoing', 'Sewa untuk dokumentasi event kantor'),
    ((SELECT id FROM customers WHERE identity_number = '3273014402920002'), (SELECT id FROM admins WHERE email = 'rental@camera-rental.test'), 'RNT-DEMO-002', '2026-05-10', '2026-05-12', '2026-05-12', 770000.00, 'returned', 'Sewa untuk prewedding');

INSERT INTO rental_details (rental_id, item_id, daily_price, quantity, subtotal)
VALUES
    ((SELECT id FROM rentals WHERE rental_code = 'RNT-DEMO-001'), (SELECT id FROM items WHERE serial_number = 'SONY-A7III-001'), 250000.00, 1, 750000.00),
    ((SELECT id FROM rentals WHERE rental_code = 'RNT-DEMO-001'), (SELECT id FROM items WHERE serial_number = 'MANFROTTO-CA-001'), 50000.00, 2, 100000.00),
    ((SELECT id FROM rentals WHERE rental_code = 'RNT-DEMO-002'), (SELECT id FROM items WHERE serial_number = 'CANON-R6-001'), 300000.00, 1, 600000.00),
    ((SELECT id FROM rentals WHERE rental_code = 'RNT-DEMO-002'), (SELECT id FROM items WHERE serial_number = 'CANON-RF50-001'), 85000.00, 1, 170000.00);

INSERT INTO rental_payments (rental_id, payment_method_id, payment_code, amount, payment_date, status, proof_image)
VALUES
    ((SELECT id FROM rentals WHERE rental_code = 'RNT-DEMO-001'), (SELECT id FROM payment_methods WHERE name = 'Bank BCA'), 'PAY-DEMO-001', 500000.00, '2026-05-18 10:30:00', 'paid', 'storage/proofs/pay-demo-001.jpg'),
    ((SELECT id FROM rentals WHERE rental_code = 'RNT-DEMO-001'), (SELECT id FROM payment_methods WHERE name = 'Cash'), 'PAY-DEMO-002', 350000.00, NULL, 'pending', NULL),
    ((SELECT id FROM rentals WHERE rental_code = 'RNT-DEMO-002'), (SELECT id FROM payment_methods WHERE name = 'QRIS Camera Rental'), 'PAY-DEMO-003', 770000.00, '2026-05-10 09:15:00', 'paid', 'storage/proofs/pay-demo-003.jpg');

INSERT INTO returns (rental_id, admin_id, return_date, condition_note, has_penalty, penalty_payment_method_id)
VALUES
    ((SELECT id FROM rentals WHERE rental_code = 'RNT-DEMO-002'), (SELECT id FROM admins WHERE email = 'rental@camera-rental.test'), '2026-05-12', 'Barang kembali lengkap, ada gores kecil pada body kamera', TRUE, (SELECT id FROM payment_methods WHERE name = 'Cash'));

INSERT INTO penalties (return_id, penalty_type, description, amount, status, paid_at)
VALUES
    ((SELECT id FROM returns WHERE rental_id = (SELECT id FROM rentals WHERE rental_code = 'RNT-DEMO-002')), 'damage', 'Gores kecil pada body kamera', 50000.00, 'paid', '2026-05-12 17:00:00');

INSERT INTO item_maintenance (item_id, admin_id, title, description, maintenance_date, cost, status)
VALUES
    ((SELECT id FROM items WHERE serial_number = 'DJI-RS3M-001'), (SELECT id FROM admins WHERE email = 'rental@camera-rental.test'), 'Kalibrasi gimbal', 'Kalibrasi motor dan pengecekan baterai', '2026-05-20', 150000.00, 'in_progress');
