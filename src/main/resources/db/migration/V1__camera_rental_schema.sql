CREATE TABLE IF NOT EXISTS admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_admins_role CHECK (role IN ('super_admin', 'admin'))
);

CREATE TABLE IF NOT EXISTS admin_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    admin_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    ip_address VARCHAR(100) NULL,
    user_agent TEXT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_admin_sessions_token (token),
    INDEX idx_admin_sessions_admin_id (admin_id),
    CONSTRAINT fk_admin_sessions_admin FOREIGN KEY (admin_id) REFERENCES admins(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    address TEXT NOT NULL,
    identity_type VARCHAR(50) NOT NULL,
    identity_number VARCHAR(100) NOT NULL UNIQUE,
    identity_image VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customers_phone (phone),
    CONSTRAINT chk_customers_identity_type CHECK (identity_type IN ('id_card', 'driver_license'))
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS category_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_category_details_category_name (category_id, name),
    INDEX idx_category_details_category_id (category_id),
    CONSTRAINT fk_category_details_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    category_detail_id BIGINT NULL,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255) NULL,
    model VARCHAR(255) NULL,
    serial_number VARCHAR(255) NULL UNIQUE,
    description TEXT NULL,
    daily_price DECIMAL(15,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'available',
    image VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_items_category_id (category_id),
    INDEX idx_items_category_detail_id (category_detail_id),
    INDEX idx_items_status (status),
    CONSTRAINT fk_items_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    CONSTRAINT fk_items_category_detail FOREIGN KEY (category_detail_id) REFERENCES category_details(id) ON DELETE SET NULL,
    CONSTRAINT chk_items_status CHECK (status IN ('available', 'rented', 'maintenance', 'inactive')),
    CONSTRAINT chk_items_stock CHECK (stock >= 0),
    CONSTRAINT chk_items_daily_price CHECK (daily_price >= 0)
);

CREATE TABLE IF NOT EXISTS payment_methods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    content_type VARCHAR(50) NOT NULL,
    content_value TEXT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_payment_methods_active (is_active),
    CONSTRAINT chk_payment_methods_type CHECK (type IN ('qr_code', 'bank_transfer', 'cash', 'e_wallet')),
    CONSTRAINT chk_payment_methods_content_type CHECK (content_type IN ('image', 'text'))
);

CREATE TABLE IF NOT EXISTS rentals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    admin_id BIGINT NOT NULL,
    rental_code VARCHAR(100) NOT NULL UNIQUE,
    rental_date DATE NOT NULL,
    planned_return_date DATE NOT NULL,
    actual_return_date DATE NULL,
    total_price DECIMAL(15,2) NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'pending',
    note TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_rentals_customer_id (customer_id),
    INDEX idx_rentals_admin_id (admin_id),
    INDEX idx_rentals_status (status),
    INDEX idx_rentals_rental_date (rental_date),
    CONSTRAINT fk_rentals_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE RESTRICT,
    CONSTRAINT fk_rentals_admin FOREIGN KEY (admin_id) REFERENCES admins(id) ON DELETE RESTRICT,
    CONSTRAINT chk_rentals_status CHECK (status IN ('pending', 'ongoing', 'returned', 'cancelled')),
    CONSTRAINT chk_rentals_dates CHECK (planned_return_date >= rental_date)
);

CREATE TABLE IF NOT EXISTS rental_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rental_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    daily_price DECIMAL(15,2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_rental_details_rental_id (rental_id),
    INDEX idx_rental_details_item_id (item_id),
    CONSTRAINT fk_rental_details_rental FOREIGN KEY (rental_id) REFERENCES rentals(id) ON DELETE CASCADE,
    CONSTRAINT fk_rental_details_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE RESTRICT,
    CONSTRAINT chk_rental_details_quantity CHECK (quantity > 0)
);

CREATE TABLE IF NOT EXISTS rental_payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rental_id BIGINT NOT NULL,
    payment_method_id BIGINT NOT NULL,
    payment_code VARCHAR(100) NOT NULL UNIQUE,
    amount DECIMAL(15,2) NOT NULL,
    payment_date TIMESTAMP NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'pending',
    proof_image VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_rental_payments_rental_id (rental_id),
    INDEX idx_rental_payments_payment_method_id (payment_method_id),
    INDEX idx_rental_payments_status (status),
    CONSTRAINT fk_rental_payments_rental FOREIGN KEY (rental_id) REFERENCES rentals(id) ON DELETE CASCADE,
    CONSTRAINT fk_rental_payments_payment_method FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id) ON DELETE RESTRICT,
    CONSTRAINT chk_rental_payments_status CHECK (status IN ('pending', 'paid', 'failed')),
    CONSTRAINT chk_rental_payments_amount CHECK (amount >= 0)
);

CREATE TABLE IF NOT EXISTS returns (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rental_id BIGINT NOT NULL UNIQUE,
    admin_id BIGINT NOT NULL,
    return_date DATE NOT NULL,
    condition_note TEXT NULL,
    has_penalty BOOLEAN NOT NULL DEFAULT FALSE,
    penalty_payment_method_id BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_returns_admin_id (admin_id),
    INDEX idx_returns_return_date (return_date),
    CONSTRAINT fk_returns_rental FOREIGN KEY (rental_id) REFERENCES rentals(id) ON DELETE RESTRICT,
    CONSTRAINT fk_returns_admin FOREIGN KEY (admin_id) REFERENCES admins(id) ON DELETE RESTRICT,
    CONSTRAINT fk_returns_penalty_payment_method FOREIGN KEY (penalty_payment_method_id) REFERENCES payment_methods(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS penalties (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    return_id BIGINT NOT NULL,
    penalty_type VARCHAR(50) NOT NULL,
    description TEXT NULL,
    amount DECIMAL(15,2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'unpaid',
    paid_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_penalties_return_id (return_id),
    INDEX idx_penalties_status (status),
    CONSTRAINT fk_penalties_return FOREIGN KEY (return_id) REFERENCES returns(id) ON DELETE CASCADE,
    CONSTRAINT chk_penalties_type CHECK (penalty_type IN ('late_return', 'damage', 'lost_item', 'other')),
    CONSTRAINT chk_penalties_status CHECK (status IN ('unpaid', 'paid')),
    CONSTRAINT chk_penalties_amount CHECK (amount >= 0)
);

CREATE TABLE IF NOT EXISTS item_maintenance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    admin_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NULL,
    maintenance_date DATE NOT NULL,
    cost DECIMAL(15,2) NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'in_progress',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_item_maintenance_item_id (item_id),
    INDEX idx_item_maintenance_admin_id (admin_id),
    INDEX idx_item_maintenance_status (status),
    CONSTRAINT fk_item_maintenance_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE RESTRICT,
    CONSTRAINT fk_item_maintenance_admin FOREIGN KEY (admin_id) REFERENCES admins(id) ON DELETE RESTRICT,
    CONSTRAINT chk_item_maintenance_status CHECK (status IN ('in_progress', 'completed')),
    CONSTRAINT chk_item_maintenance_cost CHECK (cost >= 0)
);
