-- MTANAV Initial Schema V1

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(180) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    phone_number VARCHAR(30),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE service_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    icon VARCHAR(100),
    description TEXT
);

CREATE TABLE product_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    icon VARCHAR(100),
    description TEXT
);

CREATE TABLE businesses (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    phone VARCHAR(30),
    email VARCHAR(180),
    verification_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    supports_delivery BOOLEAN NOT NULL DEFAULT TRUE,
    supports_pickup BOOLEAN NOT NULL DEFAULT TRUE,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    region VARCHAR(100),
    country VARCHAR(100) DEFAULT 'Tanzania',
    building_name VARCHAR(150),
    floor VARCHAR(20),
    shop_number VARCHAR(30),
    street VARCHAR(150),
    landmark VARCHAR(150),
    logo_url VARCHAR(500),
    rating DOUBLE PRECISION DEFAULT 0.0,
    review_count INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE business_images (
    id BIGSERIAL PRIMARY KEY,
    business_id BIGINT NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
    image_url VARCHAR(500) NOT NULL,
    display_order INT DEFAULT 0
);

CREATE TABLE services (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT REFERENCES service_categories(id) ON DELETE SET NULL,
    name VARCHAR(150) NOT NULL UNIQUE,
    icon VARCHAR(100),
    description TEXT
);

CREATE TABLE business_services (
    id BIGSERIAL PRIMARY KEY,
    business_id BIGINT NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
    service_id BIGINT NOT NULL REFERENCES services(id) ON DELETE CASCADE,
    price DECIMAL(12,2) NOT NULL,
    duration_minutes INT DEFAULT 60,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (business_id, service_id)
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT REFERENCES product_categories(id) ON DELETE SET NULL,
    name VARCHAR(150) NOT NULL UNIQUE,
    is_quick_product BOOLEAN NOT NULL DEFAULT FALSE,
    quick_product_icon VARCHAR(100),
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE business_products (
    id BIGSERIAL PRIMARY KEY,
    business_id BIGINT NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    sku VARCHAR(100),
    price DECIMAL(12,2) NOT NULL,
    sale_price DECIMAL(12,2),
    min_order_quantity INT DEFAULT 1,
    available_quantity INT DEFAULT 100,
    unit VARCHAR(30) DEFAULT 'pcs',
    availability_status VARCHAR(30) NOT NULL DEFAULT 'IN_STOCK',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (business_id, product_id)
);

CREATE TABLE product_variants (
    id BIGSERIAL PRIMARY KEY,
    business_product_id BIGINT NOT NULL REFERENCES business_products(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit VARCHAR(30) DEFAULT 'pcs',
    price DECIMAL(12,2) NOT NULL,
    available_quantity INT DEFAULT 100
);

CREATE TABLE product_images (
    id BIGSERIAL PRIMARY KEY,
    business_product_id BIGINT NOT NULL REFERENCES business_products(id) ON DELETE CASCADE,
    image_url VARCHAR(500) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    business_id BIGINT NOT NULL REFERENCES businesses(id) ON DELETE RESTRICT,
    order_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    order_type VARCHAR(30) NOT NULL DEFAULT 'DELIVERY',
    total_amount DECIMAL(12,2) NOT NULL,
    delivery_address TEXT,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    business_product_id BIGINT NOT NULL REFERENCES business_products(id),
    variant_id BIGINT REFERENCES product_variants(id),
    product_name VARCHAR(150) NOT NULL,
    variant_name VARCHAR(100),
    unit_price DECIMAL(12,2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL
);

CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    business_id BIGINT NOT NULL REFERENCES businesses(id) ON DELETE RESTRICT,
    service_id BIGINT NOT NULL REFERENCES services(id) ON DELETE RESTRICT,
    booking_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    booking_date DATE NOT NULL,
    booking_time VARCHAR(20) NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id) ON DELETE SET NULL,
    booking_id BIGINT REFERENCES bookings(id) ON DELETE SET NULL,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    amount DECIMAL(12,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    transaction_reference VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    business_id BIGINT NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
    product_id BIGINT REFERENCES products(id) ON DELETE CASCADE,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE favorites (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    business_id BIGINT REFERENCES businesses(id) ON DELETE CASCADE,
    product_id BIGINT REFERENCES products(id) ON DELETE CASCADE,
    service_id BIGINT REFERENCES services(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    entity VARCHAR(100) NOT NULL,
    entity_id BIGINT,
    details TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for efficient map search & category lookups
CREATE INDEX idx_businesses_location ON businesses(latitude, longitude);
CREATE INDEX idx_businesses_verification ON businesses(verification_status);
CREATE INDEX idx_business_products_product ON business_products(product_id, availability_status);
CREATE INDEX idx_business_products_business ON business_products(business_id);
CREATE INDEX idx_business_services_service ON business_services(service_id, is_available);
CREATE INDEX idx_business_services_business ON business_services(business_id);
CREATE INDEX idx_products_quick ON products(is_quick_product);
