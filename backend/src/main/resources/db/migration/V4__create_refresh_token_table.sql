-- Migration V4: Création de la table refresh_token pour la gestion des JWT

DROP TABLE IF EXISTS refresh_token;

CREATE TABLE refresh_token
(
    id         BIGSERIAL PRIMARY KEY,
    token      VARCHAR(255) NOT NULL,
    user_id    BIGINT      NOT NULL,
    expires_at TIMESTAMPTZ   NOT NULL,

    CONSTRAINT uk_refresh_token_token UNIQUE (token),
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

-- Index pour optimiser les recherches par token
CREATE INDEX idx_refresh_token_token ON refresh_token (token);

-- Index pour optimiser les recherches par user_id
CREATE INDEX idx_refresh_token_user_id ON refresh_token (user_id);

-- Index pour le nettoyage des tokens expirés
CREATE INDEX idx_refresh_token_expires_at ON refresh_token (expires_at);
