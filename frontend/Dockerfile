# ========================================
# STAGE 1: Build Angular
# ========================================
ARG NODE_VERSION=20
FROM node:${NODE_VERSION}-alpine AS builder

WORKDIR /app

# Copier package.json et package-lock.json
COPY package*.json ./

# Installer les dépendances (besoin des devDependencies)
RUN npm ci --silent

# Copier le code source
COPY . .

# Build Angular en mode production
RUN npm run build -- --configuration production

# ========================================
# STAGE 2: Nginx pour servir l'app
# ========================================
FROM nginx:1.25-alpine3.19-slim
RUN apk add --no-cache curl 

# Copier la configuration Nginx personnalisée
COPY nginx.conf /etc/nginx/nginx.conf

# Copier les fichiers buildés depuis le stage précédent
COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html

# Exposer le port 80
EXPOSE 80

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost/ || exit 1

# Démarrer Nginx
CMD ["nginx", "-g", "daemon off;"]
