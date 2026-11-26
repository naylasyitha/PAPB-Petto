-- ==============================================================================
-- PETTO: SCHEMA SUPABASE UNTUK FITUR SHELTER DONATION (FUNGSI 4)
-- Penanggung Jawab: Aqeela Sahla (Kelompok 5 PAPB)
-- ==============================================================================

-- 1. TABEL: donation_campaigns (Menyimpan seluruh kampanye donasi shelter)
CREATE TABLE IF NOT EXISTS public.donation_campaigns (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    shelter_name TEXT NOT NULL,
    is_verified BOOLEAN DEFAULT TRUE,
    location TEXT NOT NULL,
    category TEXT NOT NULL,
    description TEXT NOT NULL,
    image_url TEXT NOT NULL,
    target_amount DOUBLE PRECISION NOT NULL,
    collected_amount DOUBLE PRECISION DEFAULT 0.0,
    deadline_timestamp BIGINT NOT NULL,
    status TEXT NOT NULL DEFAULT 'ACTIVE',
    fund_allocations JSONB DEFAULT '[]'::jsonb,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 2. TABEL: donation_transactions (Menyimpan seluruh riwayat transaksi donasi)
CREATE TABLE IF NOT EXISTS public.donation_transactions (
    transaction_id TEXT PRIMARY KEY,
    campaign_id TEXT NOT NULL REFERENCES public.donation_campaigns(id) ON DELETE CASCADE,
    donor_name TEXT NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    payment_method TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'SUCCESS',
    message TEXT,
    timestamp BIGINT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 3. INDEX UNTUK PERFORMA QUERY
CREATE INDEX IF NOT EXISTS idx_campaigns_status ON public.donation_campaigns(status);
CREATE INDEX IF NOT EXISTS idx_campaigns_category ON public.donation_campaigns(category);
CREATE INDEX IF NOT EXISTS idx_transactions_campaign ON public.donation_transactions(campaign_id);
CREATE INDEX IF NOT EXISTS idx_transactions_timestamp ON public.donation_transactions(timestamp DESC);

-- 4. ROW LEVEL SECURITY (RLS) & POLICIES
-- Mengaktifkan RLS agar aman, dan memberikan hak akses bagi client aplikasi Android (anon role)
ALTER TABLE public.donation_campaigns ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.donation_transactions ENABLE ROW LEVEL SECURITY;

-- Policy untuk donation_campaigns
DROP POLICY IF EXISTS "Public can view donation campaigns" ON public.donation_campaigns;
CREATE POLICY "Public can view donation campaigns"
    ON public.donation_campaigns FOR SELECT
    TO anon, authenticated
    USING (true);

DROP POLICY IF EXISTS "Public can insert donation campaigns" ON public.donation_campaigns;
CREATE POLICY "Public can insert donation campaigns"
    ON public.donation_campaigns FOR INSERT
    TO anon, authenticated
    WITH CHECK (true);

DROP POLICY IF EXISTS "Public can update donation campaigns" ON public.donation_campaigns;
CREATE POLICY "Public can update donation campaigns"
    ON public.donation_campaigns FOR UPDATE
    TO anon, authenticated
    USING (true);

-- Policy untuk donation_transactions
DROP POLICY IF EXISTS "Public can view donation transactions" ON public.donation_transactions;
CREATE POLICY "Public can view donation transactions"
    ON public.donation_transactions FOR SELECT
    TO anon, authenticated
    USING (true);

DROP POLICY IF EXISTS "Public can insert donation transactions" ON public.donation_transactions;
CREATE POLICY "Public can insert donation transactions"
    ON public.donation_transactions FOR INSERT
    TO anon, authenticated
    WITH CHECK (true);

-- 5. INITIAL SEED DATA (KAMPANYE DONASI AWAL)
INSERT INTO public.donation_campaigns (
    id, title, shelter_name, is_verified, location, category, description,
    image_url, target_amount, collected_amount, deadline_timestamp, status, fund_allocations
)
VALUES
(
    'campaign-sahabat-satwa-01',
    'Emergency ICU & Surgeries for 14 Rescued Flood Strays',
    'Sahabat Satwa Shelter',
    TRUE,
    'Jakarta Timur, DKI Jakarta',
    'Emergency Medical',
    'Fourteen dogs and cats were recently rescued from severe flash floods in East Jakarta, suffering from acute pneumonia, open fractures, and hypothermia. This emergency fund covers immediate ICU hospitalization, life-saving orthopedic surgeries, and intensive 24/7 post-op recovery care before they can be safely transitioned to our general rescue shelter.',
    'https://images.unsplash.com/photo-1548767797-d8c844163c4c?w=800&auto=format&fit=crop&q=80',
    25000000.0,
    21250000.0,
    (EXTRACT(EPOCH FROM NOW() + INTERVAL '2 days') * 1000)::BIGINT,
    'ACTIVE',
    '[
        {"category_name": "Emergency ICU & Critical Surgeries", "percentage": 50, "amount": 12500000.0},
        {"category_name": "Post-op Antibiotics & Specialized Meds", "percentage": 25, "amount": 6250000.0},
        {"category_name": "Nutritional Recovery Food & Medical Boarding", "percentage": 25, "amount": 6250000.0}
    ]'::jsonb
),
(
    'campaign-pejaten-sanctuary-02',
    'Urgent Food & Medical Support for 300+ Abandoned Dogs',
    'Pejaten Animal Sanctuary',
    TRUE,
    'Jakarta Selatan, DKI Jakarta',
    'Food & Medical',
    'Pejaten Animal Sanctuary currently shelters over 300 abandoned and abused senior dogs. Monthly food supplies are critically low, and several older rescues need immediate blood-work and chronic arthritis medication. Your donations ensure no bowl is left empty this month.',
    'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=800&auto=format&fit=crop&q=80',
    40000000.0,
    26000000.0,
    (EXTRACT(EPOCH FROM NOW() + INTERVAL '7 days') * 1000)::BIGINT,
    'ACTIVE',
    '[
        {"category_name": "Dry & Wet Dog Food (1.5 tons)", "percentage": 55, "amount": 22000000.0},
        {"category_name": "Geriatric Veterinary Care & Vitamins", "percentage": 30, "amount": 12000000.0},
        {"category_name": "Shelter Sanitation & Tick Treatments", "percentage": 15, "amount": 6000000.0}
    ]'::jsonb
),
(
    'campaign-rumah-kucing-03',
    'Shelter Quarantine Renovation & Cat Rescue Operations',
    'Rumah Kucing Parung',
    TRUE,
    'Bogor, Jawa Barat',
    'Shelter Renovation',
    'Due to the recent rainy season, our quarantine isolation room for rescued kittens with panleukopenia suffered roof collapses and moisture damage. We urgently need to rebuild sanitary isolation enclosures to prevent virus outbreaks across 200+ rescued felines.',
    'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=800&auto=format&fit=crop&q=80',
    35000000.0,
    14000000.0,
    (EXTRACT(EPOCH FROM NOW() + INTERVAL '14 days') * 1000)::BIGINT,
    'ACTIVE',
    '[
        {"category_name": "Waterproof Roofing & Room Reconstruction", "percentage": 45, "amount": 15750000.0},
        {"category_name": "Stainless Steel Quarantine Cages", "percentage": 35, "amount": 12250000.0},
        {"category_name": "Disinfection & Air Filtration Systems", "percentage": 20, "amount": 7000000.0}
    ]'::jsonb
),
(
    'campaign-garda-satwa-04',
    'Emergency Medical Aid & Animal Welfare Fund',
    'Garda Satwa Foundation',
    TRUE,
    'Tangerang Selatan, Banten',
    'Emergency Medical',
    'Garda Satwa Foundation responds to daily animal cruelty and hit-and-run distress calls. This campaign replenishes our revolving emergency veterinary reserve fund so no rescue is turned away from emergency clinics during the night.',
    'https://images.unsplash.com/photo-1537151608828-ea2b11777ee8?w=800&auto=format&fit=crop&q=80',
    50000000.0,
    50000000.0,
    (EXTRACT(EPOCH FROM NOW() + INTERVAL '5 days') * 1000)::BIGINT,
    'COMPLETED',
    '[
        {"category_name": "Revolving Emergency Clinic Fund", "percentage": 60, "amount": 30000000.0},
        {"category_name": "Rescue Vehicle Fuel & Animal Transport", "percentage": 20, "amount": 10000000.0},
        {"category_name": "Foster Care Support & Supplies", "percentage": 20, "amount": 10000000.0}
    ]'::jsonb
)
ON CONFLICT (id) DO UPDATE SET
    title = EXCLUDED.title,
    shelter_name = EXCLUDED.shelter_name,
    is_verified = EXCLUDED.is_verified,
    location = EXCLUDED.location,
    category = EXCLUDED.category,
    description = EXCLUDED.description,
    image_url = EXCLUDED.image_url,
    target_amount = EXCLUDED.target_amount,
    collected_amount = EXCLUDED.collected_amount,
    deadline_timestamp = EXCLUDED.deadline_timestamp,
    status = EXCLUDED.status,
    fund_allocations = EXCLUDED.fund_allocations,
    updated_at = NOW();

-- 6. INITIAL SEED DATA (TRANSAKSI AWAL)
INSERT INTO public.donation_transactions (
    transaction_id, campaign_id, donor_name, amount, payment_method, status, message, timestamp
)
VALUES
(
    'TRX-INIT-001',
    'campaign-sahabat-satwa-01',
    'Budi Santoso',
    250000.0,
    'BCA Virtual Account',
    'SUCCESS',
    'Cepat sembuh ya anabul tersayang!',
    (EXTRACT(EPOCH FROM NOW() - INTERVAL '15 minutes') * 1000)::BIGINT
),
(
    'TRX-INIT-002',
    'campaign-sahabat-satwa-01',
    'Anindya Putri',
    500000.0,
    'GoPay',
    'SUCCESS',
    'Semoga operasinya lancar dan lekas pulih.',
    (EXTRACT(EPOCH FROM NOW() - INTERVAL '1 hour') * 1000)::BIGINT
),
(
    'TRX-INIT-003',
    'campaign-sahabat-satwa-01',
    'Hamba Allah',
    100000.0,
    'QRIS',
    'SUCCESS',
    'Sedikit bantuan untuk anak-anak bulu.',
    (EXTRACT(EPOCH FROM NOW() - INTERVAL '3 hours') * 1000)::BIGINT
),
(
    'TRX-INIT-004',
    'campaign-pejaten-sanctuary-02',
    'Dewi Lestari',
    300000.0,
    'Mandiri Virtual Account',
    'SUCCESS',
    'Semangat selalu untuk para rescuer!',
    (EXTRACT(EPOCH FROM NOW() - INTERVAL '4 hours') * 1000)::BIGINT
)
ON CONFLICT (transaction_id) DO NOTHING;
