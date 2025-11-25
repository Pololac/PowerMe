#!/bin/bash

# Création du user admin de la bdd si il n'existe pas
psql -U postgres -c "CREATE ROLE powerme_user WITH LOGIN PASSWORD 'dev';" 2>/dev/null || true

# Suppression de la BDD si elle existe et recréation
psql -U postgres -c "DROP DATABASE IF EXISTS powerme_dev;" 2>/dev/null || true
psql -U postgres -c "CREATE DATABASE powerme_dev OWNER powerme_user;"

# Ajout extension postgis
psql -U postgres -d powerme_dev -c "CREATE EXTENSION IF NOT EXISTS postgis;"

echo "✅ Base de données réinitialisée avec succès !"
