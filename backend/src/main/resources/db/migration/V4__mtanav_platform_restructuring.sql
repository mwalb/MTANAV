-- MTANAV Platform Restructuring and Visual Redesign Migration V4

-- Referral Links
CREATE TABLE referral_links (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    code VARCHAR(50) NOT NULL UNIQUE,
    target_type VARCHAR(50) NOT NULL, -- PRODUCT, SERVICE, BUSINESS, GENERAL
    target_id BIGINT,
    clicks_count INT DEFAULT 0,
    conversions_count INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE referral_clicks (
    id BIGSERIAL PRIMARY KEY,
    referral_link_id BIGINT NOT NULL REFERENCES referral_links(id) ON DELETE CASCADE,
    visitor_ip VARCHAR(50),
    user_agent VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE attribution_records (
    id BIGSERIAL PRIMARY KEY,
    referral_link_id BIGINT NOT NULL REFERENCES referral_links(id) ON DELETE CASCADE,
    customer_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    order_id BIGINT REFERENCES orders(id) ON DELETE SET NULL,
    booking_id BIGINT REFERENCES bookings(id) ON DELETE SET NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ATTRIBUTED', -- ATTRIBUTED, QUALIFIED, REVERSED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Commissions & Ledger
CREATE TABLE commission_policies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    commission_type VARCHAR(30) NOT NULL, -- PERCENTAGE, FIXED
    rate_or_amount DECIMAL(12,2) NOT NULL,
    eligible_category VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE commissions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    referral_link_id BIGINT REFERENCES referral_links(id) ON DELETE SET NULL,
    order_id BIGINT REFERENCES orders(id) ON DELETE SET NULL,
    amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'TZS',
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, AVAILABLE, PAID, REVERSED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE earning_ledger (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'TZS',
    entry_type VARCHAR(20) NOT NULL, -- CREDIT, DEBIT
    description TEXT NOT NULL,
    reference_id VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Withdrawals & Mobile Money Payouts
CREATE TABLE withdrawal_destinations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider_name VARCHAR(50) NOT NULL, -- M_PESA, AIRTEL_MONEY, TIGO_PESA, BANK
    account_number VARCHAR(50) NOT NULL,
    account_holder_name VARCHAR(120) NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE withdrawal_requests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    destination_id BIGINT NOT NULL REFERENCES withdrawal_destinations(id) ON DELETE RESTRICT,
    amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'TZS',
    fee DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    net_amount DECIMAL(12,2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'REQUESTED', -- REQUESTED, RESERVED, PROCESSING, PAID, FAILED, CANCELLED, REVERSED, REQUIRES_REVIEW
    transaction_reference VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Local Jobs
CREATE TABLE local_jobs (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    category VARCHAR(100) NOT NULL, -- Delivery, Phone repair, Computer repair, Electrical work, Plumbing, Cleaning, Installation, Tailoring, Vehicle repair
    description TEXT NOT NULL,
    location VARCHAR(255) NOT NULL,
    budget_amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'TZS',
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN', -- OPEN, ASSIGNED, COMPLETED, DISPUTED
    poster_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE job_applications (
    id BIGSERIAL PRIMARY KEY,
    job_id BIGINT NOT NULL REFERENCES local_jobs(id) ON DELETE CASCADE,
    provider_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    proposed_rate DECIMAL(12,2) NOT NULL,
    cover_note TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, ACCEPTED, REJECTED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Creators and Promotion
CREATE TABLE creator_campaigns (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    brand_owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    budget DECIMAL(12,2) NOT NULL,
    compensation_amount DECIMAL(12,2) NOT NULL,
    requirements TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, PAUSED, COMPLETED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE campaign_applications (
    id BIGSERIAL PRIMARY KEY,
    campaign_id BIGINT NOT NULL REFERENCES creator_campaigns(id) ON DELETE CASCADE,
    creator_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    deliverable_url VARCHAR(500),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, PAID
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Community Rewards
CREATE TABLE community_rewards (
    id BIGSERIAL PRIMARY KEY,
    reporter_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    activity_type VARCHAR(50) NOT NULL, -- INCORRECT_LOCATION, CLOSED_BUSINESS, DETAILS_CORRECTION, DUPLICATE_LISTING
    target_entity VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    reward_amount DECIMAL(12,2) NOT NULL DEFAULT 5000.00,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Ensure Spoon is marked as quick product
UPDATE products SET is_quick_product = TRUE WHERE id = 1;

-- Seed initial commission policy
INSERT INTO commission_policies (name, commission_type, rate_or_amount, eligible_category, is_active) VALUES
('Standard Referral Commission', 'PERCENTAGE', 5.00, 'ALL', TRUE);
