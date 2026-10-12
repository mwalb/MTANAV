-- MTANAV Seed Data V2

-- Roles
INSERT INTO roles (id, name) VALUES
(1, 'ROLE_CUSTOMER'),
(2, 'ROLE_BUSINESS_OWNER'),
(3, 'ROLE_ADMIN'),
(4, 'ROLE_SUPER_ADMIN');

-- Password for all seed users is: password123 ($2a$10$e8wYn7o.9e3X0m1m1m1m1e8wYn7o.9e3X0m1m1m1m1 - standard BCrypt)
-- Hash generated for 'password123': $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a
INSERT INTO users (id, email, password_hash, full_name, phone_number, is_active) VALUES
(1, 'admin@mtanav.tz', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'System Admin', '+255700000001', TRUE),
(2, 'owner1@mtanav.tz', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Juma Juma', '+255700000002', TRUE),
(3, 'owner2@mtanav.tz', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Amina Hassan', '+255700000003', TRUE),
(4, 'customer1@mtanav.tz', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Baraka Ally', '+255700000004', TRUE);

INSERT INTO user_roles (user_id, role_id) VALUES
(1, 4), (1, 3),
(2, 2),
(3, 2),
(4, 1);

-- Service Categories
INSERT INTO service_categories (id, name, icon, description) VALUES
(1, 'Financial', '🏦', 'Banking, Mobile Money, and Financial Services'),
(2, 'Automotive', '🔧', 'Garage, Repair, and Car Care Services'),
(3, 'Laundry & Cleaning', '🧺', 'Dry Cleaning, Washing, and Tailoring'),
(4, 'Beauty & Wellness', '💇', 'Salon, Barber, and Personal Care'),
(5, 'Medical & Health', '🏥', 'Clinics, Pharmacies, and Diagnostics'),
(6, 'Printing & Tech', '🖨', 'Printing, Photocopy, and Device Repair');

-- Product Categories
INSERT INTO product_categories (id, name, icon, description) VALUES
(1, 'Household & Kitchen', '🥄', 'Cookware, Utensils, and Home Items'),
(2, 'Food & Groceries', '🍚', 'Grains, Staples, and Daily Groceries'),
(3, 'Electronics & Mobile', '📱', 'Smartphones, Chargers, and Accessories'),
(4, 'Apparel & Shoes', '👟', 'Footwear, Clothing, and Tailored Wear'),
(5, 'Furniture & Home', '🪑', 'Chairs, Tables, and Office Furniture');

-- Services
INSERT INTO services (id, category_id, name, icon, description) VALUES
(1, 1, 'Banking', '🏦', 'Commercial Banking & Money Transfers'),
(2, 2, 'Garage', '🔧', 'Vehicle Maintenance & Engine Repair'),
(3, 3, 'Washing', '🧺', 'Laundry & Dry Cleaning Services'),
(4, 4, 'Salon', '💇', 'Hair Styling & Beauty Treatment'),
(5, 5, 'Medical', '🏥', 'General Outpatient & Medical Care'),
(6, 6, 'Printing', '🖨', 'Document Printing, Binding & Graphics'),
(7, 6, 'Phone Repair', '📱', 'Smartphone Screen & Hardware Repairs'),
(8, 4, 'Barber', '✂️', 'Men Haircuts & Shaving'),
(9, 2, 'Car Wash', '🚗', 'Full Body Car Wash & Detailing'),
(10, 3, 'Tailoring', '👗', 'Custom Tailoring & Fabric Repair');

-- Products (with 4 Quick Products marked)
INSERT INTO products (id, category_id, name, is_quick_product, quick_product_icon, description) VALUES
(1, 1, 'Spoon', FALSE, '🥄', 'Stainless steel and metallic dining spoons'),
(2, 2, 'Rice', TRUE, '🍚', 'Grade 1 Kyela Super Rice and Aromatic Rice'),
(3, 3, 'Phones', TRUE, '📱', 'Smartphones and Feature Phones'),
(4, 3, 'Chargers', TRUE, '🔌', 'Fast Charging Adapters & USB Cables'),
(5, 4, 'Shoes', TRUE, '👟', 'Leather Shoes, Sneakers & Casual Footwear'),
(6, 4, 'Clothes', FALSE, '👕', 'Shirts, Trousers & Dresses'),
(7, 3, 'Laptops', FALSE, '💻', 'Notebooks, Laptops & Computing Devices'),
(8, 5, 'Furniture', FALSE, '🪑', 'Office & Living Room Furniture');

-- Businesses (Realistic Dar es Salaam coordinates marked as DEMO)
INSERT INTO businesses (id, owner_id, name, description, phone, email, verification_status, supports_delivery, supports_pickup, latitude, longitude, address, city, region, country, building_name, floor, shop_number, street, landmark, logo_url, rating, review_count) VALUES
(1, 2, 'Demo Household Shop (Kariakoo)', 'Premier supplier of kitchenware and household items in Kariakoo marketplace.', '+255711000001', 'info@demohousehold.co.tz', 'VERIFIED', TRUE, TRUE, -6.8182, 39.2778, 'Swahili Street / Aggrey', 'Dar es Salaam', 'Ilala', 'Tanzania', 'Kariakoo Commercial Complex', '1', 'A-12', 'Swahili Street', 'Near Msimbazi Police', 'https://picsum.photos/seed/shop1/200/200', 4.8, 42),
(2, 3, 'Demo Electronics Store (Mlimani)', 'Authorized seller of original smartphones, chargers, and computing accessories.', '+255711000002', 'sales@demoelectronics.co.tz', 'VERIFIED', TRUE, TRUE, -6.7725, 39.2215, 'Mlimani City Mall', 'Dar es Salaam', 'Ubungo', 'Tanzania', 'Mlimani Mall Block B', 'Ground', 'B-104', 'Sam Nujoma Road', 'Opposite University of Dar es Salaam', 'https://picsum.photos/seed/shop2/200/200', 4.6, 28),
(3, 2, 'Demo Kitchen & Hardware (Sinza)', 'Quality kitchenware, utensils, spoons, and durable home furniture.', '+255711000003', 'contact@demokitchen.co.tz', 'VERIFIED', TRUE, TRUE, -6.7845, 39.2280, 'Sinza Mori', 'Dar es Salaam', 'Kinondoni', 'Tanzania', 'Mori Plaza', '1', 'Shop 5', 'Shekilango Road', 'Near Mori Bus Stop', 'https://picsum.photos/seed/shop3/200/200', 4.5, 19),
(4, 3, 'Demo Auto Garage & Car Wash (Mikocheni)', 'Expert vehicle mechanical repairs, oil changes, and full body car wash.', '+255711000004', 'service@demogarage.co.tz', 'VERIFIED', FALSE, TRUE, -6.7580, 39.2450, 'Mikocheni B', 'Dar es Salaam', 'Kinondoni', 'Tanzania', 'Mikocheni Auto Yard', 'Ground', 'Yard 1', 'Old Bagamoyo Road', 'Near Kairuki Hospital', 'https://picsum.photos/seed/shop4/200/200', 4.7, 35),
(5, 2, 'Demo Printing & Tech Hub (Posta)', 'High speed document printing, binding, phone repairs, and charger sales.', '+255711000005', 'hub@demoprint.co.tz', 'VERIFIED', TRUE, TRUE, -6.8150, 39.2880, 'Posta Mpya', 'Dar es Salaam', 'Ilala', 'Tanzania', 'Samora Tower', '2', '204', 'Samora Avenue', 'Near Askari Monument', 'https://picsum.photos/seed/shop5/200/200', 4.4, 15);

-- Business Images
INSERT INTO business_images (business_id, image_url, display_order) VALUES
(1, 'https://picsum.photos/seed/shop1_front/600/400', 1),
(2, 'https://picsum.photos/seed/shop2_front/600/400', 1),
(3, 'https://picsum.photos/seed/shop3_front/600/400', 1),
(4, 'https://picsum.photos/seed/shop4_front/600/400', 1),
(5, 'https://picsum.photos/seed/shop5_front/600/400', 1);

-- Business Products
-- Business 1 (Demo Household Shop Kariakoo) sells Spoon and Rice
INSERT INTO business_products (id, business_id, product_id, sku, price, sale_price, min_order_quantity, available_quantity, unit, availability_status) VALUES
(1, 1, 1, 'SPN-001-KAZ', 3000.00, 2800.00, 1, 500, 'pcs', 'IN_STOCK'),
(2, 1, 2, 'RICE-001-KYE', 3500.00, NULL, 1, 200, 'kg', 'IN_STOCK');

-- Business 2 (Demo Electronics Store Mlimani) sells Phones, Chargers, Laptops
INSERT INTO business_products (id, business_id, product_id, sku, price, sale_price, min_order_quantity, available_quantity, unit, availability_status) VALUES
(3, 2, 3, 'PHN-S24-ULT', 2500000.00, 2400000.00, 1, 15, 'pcs', 'IN_STOCK'),
(4, 2, 4, 'CHG-PD-65W', 45000.00, 40000.00, 1, 80, 'pcs', 'IN_STOCK'),
(5, 2, 7, 'LTP-MAC-M3', 3800000.00, NULL, 1, 8, 'pcs', 'IN_STOCK');

-- Business 3 (Demo Kitchen & Hardware Sinza) sells Spoon and Furniture
INSERT INTO business_products (id, business_id, product_id, sku, price, sale_price, min_order_quantity, available_quantity, unit, availability_status) VALUES
(6, 3, 1, 'SPN-002-PREM', 3500.00, 3200.00, 1, 150, 'pcs', 'IN_STOCK'),
(7, 3, 8, 'FUR-OFF-CHR', 250000.00, 220000.00, 1, 12, 'pcs', 'IN_STOCK');

-- Business 5 (Demo Printing & Tech Hub Posta) sells Chargers
INSERT INTO business_products (id, business_id, product_id, sku, price, sale_price, min_order_quantity, available_quantity, unit, availability_status) VALUES
(8, 5, 4, 'CHG-USB-C', 25000.00, NULL, 1, 50, 'pcs', 'IN_STOCK'),
(9, 1, 5, 'SHS-LTH-01', 45000.00, 40000.00, 1, 30, 'pairs', 'IN_STOCK'),
(10, 3, 5, 'SHS-SNK-02', 35000.00, NULL, 1, 40, 'pairs', 'IN_STOCK');

-- Product Variants / Bundles for Spoons in Shop 1
INSERT INTO product_variants (business_product_id, name, quantity, unit, price, available_quantity) VALUES
(1, 'Single Spoon', 1, 'pcs', 3000.00, 500),
(1, 'Small Bundle (6 Pieces)', 6, 'pcs', 15000.00, 80),
(1, 'Family Bundle (12 Pieces)', 12, 'pcs', 28000.00, 40),
(1, 'Wholesale Carton (50 Pieces)', 50, 'pcs', 100000.00, 10);

-- Product Variants / Bundles for Spoons in Shop 3
INSERT INTO product_variants (business_product_id, name, quantity, unit, price, available_quantity) VALUES
(6, 'Single Premium Spoon', 1, 'pcs', 3500.00, 150),
(6, '6-Piece Set', 6, 'pcs', 18000.00, 25);

-- Business Services
-- Business 4 (Demo Auto Garage) provides Garage and Car Wash
INSERT INTO business_services (business_id, service_id, price, duration_minutes, is_available) VALUES
(4, 2, 80000.00, 120, TRUE),
(4, 9, 15000.00, 45, TRUE);

-- Business 5 (Demo Printing & Tech Hub) provides Printing, Phone Repair and Salon
INSERT INTO business_services (business_id, service_id, price, duration_minutes, is_available) VALUES
(5, 6, 500.00, 10, TRUE),
(5, 7, 35000.00, 60, TRUE),
(5, 4, 25000.00, 45, TRUE);

-- Initial Reviews
INSERT INTO reviews (user_id, business_id, product_id, rating, comment) VALUES
(4, 1, 1, 5, 'Great quality spoons and super fast delivery in Kariakoo!'),
(4, 2, 3, 5, 'Original smartphone with genuine warranty. Excellent service.');
