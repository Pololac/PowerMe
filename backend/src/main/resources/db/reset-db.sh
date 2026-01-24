#!/bin/bash

echo "🔄 Réinitialisation de la base de données..."

# Création du user admin si il n'existe pas
psql -U postgres -c "CREATE ROLE powerme_user WITH LOGIN PASSWORD 'dev';" 2>/dev/null || true

# Ferme toutes les connexions actives avant de drop
psql -U postgres -c "
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'powerme_dev'
  AND pid <> pg_backend_pid();
" 2>/dev/null || true

# Suppression de la BDD si elle existe
psql -U postgres -c "DROP DATABASE IF EXISTS powerme_dev;" 2>/dev/null

# Recréation de la BDD
psql -U postgres -c "CREATE DATABASE powerme_dev OWNER powerme_user;"

# Donne tous les droits au user
psql -U postgres -d powerme_dev -c "GRANT ALL PRIVILEGES ON DATABASE powerme_dev TO powerme_user;"
psql -U postgres -d powerme_dev -c "GRANT ALL ON SCHEMA public TO powerme_user;"

# Ajout extension PostGIS
psql -U postgres -d powerme_dev -c "CREATE EXTENSION IF NOT EXISTS postgis;"

echo "✅ Base de données réinitialisée avec succès !"
echo "📊 Vous pouvez maintenant lancer : mvn flyway:migrate"
