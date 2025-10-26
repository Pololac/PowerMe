-- =====================================================================
-- CONSTRAINTS métier
-- =====================================================================

-- Charging Station
ALTER TABLE charging_station
    ADD CONSTRAINT chk_station_rate_positive
        CHECK (hourly_rate > 0);

-- Booking
ALTER TABLE booking
    ADD CONSTRAINT chk_booking_dates_valid
        CHECK (end_time > start_time);

ALTER TABLE booking
    ADD CONSTRAINT chk_booking_price_positive
        CHECK (total_price > 0);

-- Photos
ALTER TABLE photo
    ADD CONSTRAINT chk_photos_single_parent
        CHECK (
            (charging_location_id IS NOT NULL AND charging_station_id IS NULL)
                OR
            (charging_location_id IS NULL AND charging_station_id IS NOT NULL)
            );
