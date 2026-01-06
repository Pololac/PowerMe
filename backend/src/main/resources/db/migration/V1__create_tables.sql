-- ============================================
-- POWERME - SCHEMA PRODUCTION-READY
-- ============================================

-- ATTENTION: Exécuter AVANT ce script (depuis psql -U postgres) :
-- DROP DATABASE IF EXISTS powerme_dev;
-- CREATE DATABASE powerme_dev OWNER powerme_user;
-- Puis:
-- psql -U powerme_user -d powerme_dev -f schema-optimized.sql


-- ============================================
-- EXTENSION POSTGIS
-- ============================================

-- CREATE EXTENSION IF NOT EXISTS postgis;


-- ============================================
-- NETTOYAGE (pour réinitialisation)
-- ============================================

DROP TABLE IF EXISTS user_activation CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS photo CASCADE;
DROP TABLE IF EXISTS unavailability_period CASCADE;
DROP TABLE IF EXISTS booking CASCADE;
DROP TABLE IF EXISTS charging_station CASCADE;
DROP TABLE IF EXISTS charging_location CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS address CASCADE;

-- ============================================
-- TABLES
-- ============================================

-- Address
CREATE TABLE address
(
    id             BIGSERIAL PRIMARY KEY,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    street_address VARCHAR(255) NOT NULL,
    city           VARCHAR(100) NOT NULL,
    postal_code    VARCHAR(20)  NOT NULL,
    country        VARCHAR(100) NOT NULL
);

COMMENT ON TABLE address IS 'Adresses postales des utilisateurs et des lieux de recharge';


-- Users
CREATE TABLE users
(
    id           BIGSERIAL PRIMARY KEY,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    deleted_at   TIMESTAMPTZ,

    email        VARCHAR(255) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    firstname    VARCHAR(100),
    lastname     VARCHAR(100),
    phone        VARCHAR(20),
    birthday     DATE,
    avatar_url   VARCHAR(500),
    is_activated BOOLEAN      NOT NULL DEFAULT FALSE,

    address_id   BIGINT,

    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT fk_users_address FOREIGN KEY (address_id)
        REFERENCES address (id) ON DELETE SET NULL
);

COMMENT ON TABLE users IS 'Utilisateurs de PowerMe (propriétaires et locataires de bornes)';


-- Table user_roles (stocke les rôles de chaque utilisateur)
CREATE TABLE user_roles
(
    user_id BIGINT      NOT NULL,
    role    VARCHAR(50) NOT NULL,

    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,

    -- Contrainte d'unicité pour qu'un user n'est pas plusieurs fois le même rôle
    CONSTRAINT uk_user_roles UNIQUE (user_id, role),

    -- Contrainte pour valider les valeurs (équivalent ENUM)
    CONSTRAINT chk_user_roles_valid
        CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN', 'ROLE_OWNER'))
);

-- Table: user_activation
CREATE TABLE user_activation
(
    id              BIGSERIAL PRIMARY KEY,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    activation_code INTEGER     NOT NULL,
    expiration_date TIMESTAMPTZ NOT NULL,
    is_used         BOOLEAN     NOT NULL DEFAULT FALSE,

    user_id         BIGINT      NOT NULL,

    CONSTRAINT uk_user_activation_code UNIQUE (activation_code),
    CONSTRAINT fk_user_activation_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

COMMENT ON TABLE user_activation IS 'Codes d''activation à 6 chiffres envoyés par email (expiration 24h)';


-- Table: charging_location
CREATE TABLE charging_location
(
    id          BIGSERIAL PRIMARY KEY,
    created_at  TIMESTAMPTZ            NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ            NOT NULL DEFAULT NOW(),

    name        VARCHAR(255)           NOT NULL,
    description TEXT,
    latitude    NUMERIC(10, 8)         NOT NULL,
    longitude   NUMERIC(11, 8)         NOT NULL,
    location    GEOGRAPHY(Point, 4326) NOT NULL,

    owner_id    BIGINT                 NOT NULL,
    address_id  BIGINT                 NOT NULL,

    CONSTRAINT fk_charging_location_owner FOREIGN KEY (owner_id)
        REFERENCES users (id) ON DELETE RESTRICT, -- Empêche suppression user si locations
    CONSTRAINT fk_charging_location_address FOREIGN KEY (address_id)
        REFERENCES address (id)
);

COMMENT ON TABLE charging_location IS 'Lieux de recharge (ex: domicile, parking entreprise)';
COMMENT ON COLUMN charging_location.location IS 'Coordonnées GPS (PostGIS Point - SRID 4326)';


-- Table: charging_station
CREATE TABLE charging_station
(
    id                   BIGSERIAL PRIMARY KEY,
    created_at           TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ   NOT NULL DEFAULT NOW(),

    name                 VARCHAR(255)  NOT NULL,
    socket_type          VARCHAR(20)   NOT NULL
        CHECK (socket_type IN ('TYPE_2S', 'TYPE_2', 'CCS', 'CHADEMO')),
    power                VARCHAR(20)   NOT NULL
        CHECK (power IN ('POWER_3_7', 'POWER_7_4', 'POWER_11', 'POWER_22',
                         'POWER_50', 'POWER_100', 'POWER_150', 'POWER_350')),
    hourly_rate          NUMERIC(8, 2) NOT NULL,
    active               BOOLEAN       NOT NULL DEFAULT TRUE,
    available_from       TIME,
    available_to         TIME,

    charging_location_id BIGINT        NOT NULL,

    CONSTRAINT fk_charging_station_location FOREIGN KEY (charging_location_id)
        REFERENCES charging_location (id) ON DELETE CASCADE -- Suppression si CL supprimées
);

COMMENT ON TABLE charging_station IS 'Bornes de recharge';

-- Table: unavailability_period
CREATE TABLE unavailability_period
(
    id                  BIGSERIAL PRIMARY KEY,
    created_at          TIMESTAMPTZ NOT NULL,

    start_date          DATE        NOT NULL,
    end_date            DATE        NOT NULL,

    charging_station_id BIGINT      NOT NULL,

    CONSTRAINT fk_unavailability_station FOREIGN KEY (charging_station_id)
        REFERENCES charging_station (id) ON DELETE CASCADE
);

COMMENT ON TABLE unavailability_period IS 'Périodes d''indisponibilité des bornes';


-- Table: booking
CREATE TABLE booking
(
    id                       BIGSERIAL PRIMARY KEY,
    created_at               TIMESTAMPTZ   NOT NULL,
    updated_at               TIMESTAMPTZ   NOT NULL,

    start_time               TIMESTAMPTZ   NOT NULL,
    end_time                 TIMESTAMPTZ   NOT NULL,
    total_price              NUMERIC(8, 2) NOT NULL,
    booking_status           VARCHAR(20)   NOT NULL
        CHECK (booking_status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'COMPLETED', 'CANCELLED')),

    user_id                  BIGINT        NOT NULL,

    -- Relation nullable (peut devenir NULL si borne supprimée)
    charging_station_id      BIGINT,

    -- Snapshots : infos figées au moment de la réservation
    station_name_snapshot    VARCHAR(255)  NOT NULL,
    station_address_snapshot TEXT          NOT NULL,
    hourly_rate_snapshot     NUMERIC(8, 2) NOT NULL,

    -- Garde le booking même si borne supprimée (CS_id Null mais infos dans "snapshots")
    CONSTRAINT fk_booking_station FOREIGN KEY (charging_station_id)
        REFERENCES charging_station (id) ON DELETE SET NULL,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users (id)

);

COMMENT ON TABLE booking IS 'Réservations de bornes de recharge';


-- Table: photo
CREATE TABLE photo
(
    id                   BIGSERIAL PRIMARY KEY,
    created_at           TIMESTAMPTZ  NOT NULL,

    filename             VARCHAR(255) NOT NULL,

    charging_location_id BIGINT,
    charging_station_id  BIGINT,

    CONSTRAINT uk_photo_filename UNIQUE (filename),
    CONSTRAINT fk_photo_location FOREIGN KEY (charging_location_id)
        REFERENCES charging_location (id) ON DELETE CASCADE,
    CONSTRAINT fk_photo_station FOREIGN KEY (charging_station_id)
        REFERENCES charging_station (id) ON DELETE CASCADE
);

COMMENT ON TABLE photo IS 'Photos des lieux et/ou des bornes de recharge';

