-- =====================================================================
-- INDEXES pour performances
-- =====================================================================

-- TABLE CHARGING_LOCATION: Index spatial
-- Index GIST pour ST_DWithin (recherche dans un rayon)
CREATE INDEX idx_charging_location_coordinates_gist
    ON charging_location USING GIST (location);

-- TABLE CHARGING_STATION : Index métier
-- Index sur FK pour JOIN rapide charging_station → charging_location
CREATE INDEX idx_charging_station_location_fk
    ON charging_station(charging_location_id);
-- Index sur disponibilité (index partiel sélectif)
CREATE INDEX idx_charging_station_active
    ON charging_station(is_active)
    WHERE is_active = TRUE;
-- Vérification dispo d'une borne
CREATE INDEX idx_unavailability_station_id
    ON unavailability_period(charging_station_id);


-- TABLE BOOKING & INDEXES COMPOSITES
-- Vérification dispo d'une borne & Calendrier résa d'une borne
CREATE INDEX idx_booking_station_active_times
    ON booking(charging_station_id, start_time, end_time)
    WHERE booking_status IN ('PENDING', 'ACCEPTED');
-- Affichage résa actives d'un user
CREATE INDEX idx_booking_user_active
    ON booking(user_id, booking_status, start_time DESC)
    WHERE booking_status IN ('PENDING', 'ACCEPTED');
-- Historique user & export
CREATE INDEX idx_booking_user_history
    ON booking(user_id, booking_status, start_time DESC, end_time);
