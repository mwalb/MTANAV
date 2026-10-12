-- Migration V5: Enhance Local Jobs & Add Business Promotions System

-- 1. Enhance local_jobs table
ALTER TABLE local_jobs
    ADD COLUMN IF NOT EXISTS business_id BIGINT REFERENCES businesses(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS employment_type VARCHAR(50) NOT NULL DEFAULT 'FULL_TIME',
    ADD COLUMN IF NOT EXISTS pay_type VARCHAR(30) DEFAULT 'FIXED',
    ADD COLUMN IF NOT EXISTS pay_amount_min DECIMAL(12,2),
    ADD COLUMN IF NOT EXISTS pay_amount_max DECIMAL(12,2),
    ADD COLUMN IF NOT EXISTS pay_frequency VARCHAR(30) DEFAULT 'ONE_TIME',
    ADD COLUMN IF NOT EXISTS application_deadline TIMESTAMP WITH TIME ZONE,
    ADD COLUMN IF NOT EXISTS qualifications TEXT,
    ADD COLUMN IF NOT EXISTS responsibilities TEXT,
    ADD COLUMN IF NOT EXISTS contact_instructions TEXT,
    ADD COLUMN IF NOT EXISTS publication_status VARCHAR(30) NOT NULL DEFAULT 'PUBLISHED',
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

-- 2. Enhance job_applications table
ALTER TABLE job_applications
    ADD COLUMN IF NOT EXISTS applicant_name VARCHAR(150),
    ADD COLUMN IF NOT EXISTS applicant_email VARCHAR(180),
    ADD COLUMN IF NOT EXISTS applicant_phone VARCHAR(50),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

-- Update default status constraint check if needed or handle standard values:
-- Status values: SUBMITTED, UNDER_REVIEW, SHORTLISTED, ACCEPTED, REJECTED, WITHDRAWN

-- 3. Create promotions table
CREATE TABLE IF NOT EXISTS promotions (
    id BIGSERIAL PRIMARY KEY,
    business_id BIGINT NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
    product_id BIGINT REFERENCES business_products(id) ON DELETE SET NULL,
    service_id BIGINT REFERENCES business_services(id) ON DELETE SET NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(100) NOT NULL,
    original_price DECIMAL(12,2),
    promotional_price DECIMAL(12,2),
    discount_percentage INT,
    terms_and_conditions TEXT,
    offer_type VARCHAR(50) NOT NULL DEFAULT 'DISCOUNT', -- DISCOUNT, BOGO, FLASH_SALE, SPECIAL_OFFER
    image_url VARCHAR(500),
    start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- DRAFT, PENDING_REVIEW, SCHEDULED, ACTIVE, PAUSED, EXPIRED, REJECTED
    is_sponsored BOOLEAN NOT NULL DEFAULT FALSE,
    impressions_count INT DEFAULT 0,
    clicks_count INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. Create saved_promotions table
CREATE TABLE IF NOT EXISTS saved_promotions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    promotion_id BIGINT NOT NULL REFERENCES promotions(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, promotion_id)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_local_jobs_business ON local_jobs(business_id);
CREATE INDEX IF NOT EXISTS idx_local_jobs_status ON local_jobs(publication_status, status);
CREATE INDEX IF NOT EXISTS idx_local_jobs_category ON local_jobs(category);
CREATE INDEX IF NOT EXISTS idx_job_applications_job ON job_applications(job_id);
CREATE INDEX IF NOT EXISTS idx_job_applications_provider ON job_applications(provider_id);

CREATE INDEX IF NOT EXISTS idx_promotions_business ON promotions(business_id);
CREATE INDEX IF NOT EXISTS idx_promotions_status_dates ON promotions(status, start_date, expiry_date);
CREATE INDEX IF NOT EXISTS idx_promotions_category ON promotions(category);
CREATE INDEX IF NOT EXISTS idx_promotions_sponsored ON promotions(is_sponsored);

-- Seed initial Local Jobs
INSERT INTO local_jobs (title, category, description, location, budget_amount, currency, status, publication_status, poster_id, business_id, employment_type, pay_type, pay_amount_min, pay_amount_max, pay_frequency, application_deadline, qualifications, responsibilities, contact_instructions)
VALUES
('Delivery Rider & Courier', 'Delivery and logistics', 'Urgent need for express parcel delivery rider with valid motorcycle license and smartphone for Kariakoo route.', 'Kariakoo, Dar es Salaam', 350000.00, 'TZS', 'OPEN', 'PUBLISHED', 2, 1, 'FULL_TIME', 'MONTHLY', 300000.00, 450000.00, 'MONTHLY', NOW() + INTERVAL '30 days', 'Valid riding license, reliable motorcycle, good knowledge of Dar es Salaam roads.', 'Pick up items from shop, deliver safely to clients, update delivery statuses on app.', 'Apply via MTANAV app or call shop manager directly.'),
('Shop Sales Assistant', 'Retail and shop assistance', 'Customer service, stock organization and POS checkout handling for household electronics store.', 'Mlimani City, Dar es Salaam', 400000.00, 'TZS', 'OPEN', 'PUBLISHED', 2, 2, 'FULL_TIME', 'MONTHLY', 350000.00, 450000.00, 'MONTHLY', NOW() + INTERVAL '20 days', 'Secondary education certificate, basic math and spoken Swahili & English.', 'Assist incoming customers, maintain clean product displays, record daily sales.', 'Submit application with contact details.'),
('Smartphone & Tablet Repair Technician', 'Repairs and technical work', 'Skilled technician for screen replacements, battery swaps, and micro-soldering.', 'Posta, Dar es Salaam', 600000.00, 'TZS', 'OPEN', 'PUBLISHED', 3, 5, 'CONTRACT', 'MONTHLY', 500000.00, 750000.00, 'MONTHLY', NOW() + INTERVAL '15 days', 'At least 2 years hands-on phone repair experience.', 'Diagnose hardware faults, perform screen & chip repairs, test repaired devices.', 'Apply through MTANAV platform with portfolio or references.'),
('Casual Loading & Unloading Assistant', 'Temporary and casual work', 'Day worker needed for offloading cargo containers at Kariakoo store.', 'Kariakoo, Dar es Salaam', 25000.00, 'TZS', 'OPEN', 'PUBLISHED', 2, 1, 'CASUAL', 'DAILY', 20000.00, 30000.00, 'DAILY', NOW() + INTERVAL '7 days', 'Physical fitness and reliability.', 'Offload boxes carefully, arrange stock in warehouse.', 'Report to shop in Kariakoo at 8:00 AM.');

-- Seed initial Promotions
INSERT INTO promotions (business_id, product_id, title, description, category, original_price, promotional_price, discount_percentage, terms_and_conditions, offer_type, image_url, start_date, expiry_date, status, is_sponsored)
VALUES
(1, 1, 'Mega Kitchen Sale — Stainless Steel Spoon Bundle', 'Get 20% off high-grade stainless steel dining spoon sets! Perfect for home and restaurant use.', 'Household', 15000.00, 12000.00, 20, 'Offer valid while stocks last. Maximum 5 bundles per customer.', 'DISCOUNT', 'https://picsum.photos/seed/promo1/600/400', NOW() - INTERVAL '2 days', NOW() + INTERVAL '14 days', 'ACTIVE', TRUE),
(2, 3, 'Fast Charger Flash Sale — 30% Off', 'Top quality Type-C & iPhone fast chargers at discounted prices this week only at Mlimani Store.', 'Electronics', 25000.00, 17500.00, 30, 'Limited time offer. Show promotion voucher at checkout.', 'FLASH_SALE', 'https://picsum.photos/seed/promo2/600/400', NOW() - INTERVAL '1 day', NOW() + INTERVAL '7 days', 'ACTIVE', TRUE),
(4, NULL, 'Weekend Premium Car Wash & Wax — 15% OFF', 'Complete exterior hand wash, vacuuming and tire shine treatment at Mikocheni Auto Yard.', 'Automotive', 25000.00, 21250.00, 15, 'Valid on Friday, Saturday, and Sunday bookings.', 'SPECIAL_OFFER', 'https://picsum.photos/seed/promo3/600/400', NOW() - INTERVAL '3 days', NOW() + INTERVAL '21 days', 'ACTIVE', FALSE);
