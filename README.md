# PowerMe

Application Java Spring Boot + Angular dans un environnement Linux (serveur sous **Ubuntu/Debian**)

## 📋 Prérequis

### Outils

- Curl : `sudo apt install -y curl`
- Git :

```bash
# Installation
sudo apt install -y git

# Version et configuration
git config --list
```
    
<!-- - Docker :
```bash
# Script complet : détection OS, installe prérequis, récupère GPG Docker, install docker-ce/docker-ce-cli/ containerd.io, démarrage et activation du service Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Plus Docker Compose si besoin
sudo apt install -y docker-compose-plugin

# Vérifier l'installation
docker --version
# → Docker version 24.0.7, build afdd53b
``` -->

### Backend (Java)

- **Java JDK 21(LTS)**

```bash
#Installation :
sudo apt update
sudo apt install openjdk-21-jdk

#Vérification :
java -version
javac -version
```

- **Maven** : pas d'installation nécessaire grâce au Maven Wrapper inclus

- **Spring Boot CLI 3.5(LTS)** : pas d'installation nécessaire sur serveur de prod

Vérification finale : `./mvnw -v`

### Base de données

- **PostgreSQL 17.6** : port par défaut `5432`

Installation :

````bash
# Installation PostgreSQL + contrib (extensions utiles)
sudo apt install -y postgresql-17 postgresql-contrib-17

# Démarrer et activer PostgreSQL
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Vérifier le statut
sudo systemctl status postgresql

#Création de la BDD et d'un user :
```bash
# Créer un utilisateur
sudo -u postgres createuser --interactive
# Répondre aux questions:
# - Nom du rôle: votre_user
# - Superutilisateur: n
# - Créer des BDD: y
# - Créer des rôles: n

# Définir un mot de passe pour cet utilisateur
sudo -u postgres psql
ALTER USER votre_user PASSWORD 'votre_mot_de_passe';
\q

# Créer une base de données
sudo -u postgres createdb votre_db -O votre_user

# Test de connexion
psql -h localhost -U votre_user -d votre_db
# Saisir le mot de passe quand demandé
````

### Frontend (Angular)

- **Node.js 22.20.0**
- **npm 10.9.3**

```bash
# Télécharger et installer nvm :
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.3/install.sh | bash

# au lieu de redémarrer le shell
\. "$HOME/.nvm/nvm.sh"

# Télécharger et installer Node.js :
nvm install 22.20.0

# Vérifiez la version de Node.js :
node -v # Doit afficher "v22.20.0".
# Vérifier la version de npm :
npm -v # Doit afficher "10.9.3".
```

- **Angular CLI 20.2.2**

Installation : `npm install -g @angular/cli@20.2.2`

Vérification finale : `ng version`

## 🏗️ Stack technique

### Backend

Dépendances listées dans `pom.xml`

- **Spring Security** - Authentification JWT
- **JPA/Hibernate** - ORM

### Base de données

- **PostGIS 3.5.3** - Localisation via lattitude/longitude

### Frontend

Dépendances listées dans `package-lock.json`
`npx ng version`

- **Angular 20** - Framework frontend
- **RxJS 7.8.0** - Programmation réactive

## 🔨 Build

### Backend

> Toujours utiliser le wrapper pour garantir la même version de Maven

```bash
## DEV (macOS/Linux) ##
# Build basique pour développer
./mvn clean compile
# → Compile juste le code, pas de JAR, pas de tests

# Build complet avec tests automatiques
./mvn clean install
# → Installe dépendances + Compile + Tests + Crée le JAR + L'installe en local

# Build SANS les tests (plus rapide)
./mvn clean install -DskipTests
# → Compile et crée le JAR, mais ignore les tests

# Lancer l'application
./mvn spring-boot:run

## TESTS
# Exécution des tests sans build
./mvn test
# → Lance tous les tests unitaires


## PROD (Linux): création du JAR final ##
# Build optimisé pour déploiement
./mvn clean verify
# → Compile + Tests unitaires + Tests integration + JAR

# Build avec profil production (si tu as un profil)
./mvn clean package -Pprod
# → Utilise les configs production

# Build sans tests pour déploiement rapide
./mvn clean package -DskipTests
# → Compile et crée le JAR, mais ignore les tests

## SERVEUR DE PROD
# Copier SEULEMENT le JAR
scp target/powerme-1.0.0.jar user@serveur:/opt/app/

# Sur le serveur (avec SEULEMENT Java installé)
ssh user@serveur
cd /opt/app

# Lancer l'application sur le serveur de prod
java -jar demo-app-1.0.0.jar
```

### Frontend

```bash
#TESTS
# Exécution des tests sans build
npm run test

# BUILD
cd frontend

# Installer les dépendances
npm install
# → Télécharge node_modules/ (peut prendre 2-3min)

# Build développement
npm run build

# Build production (optimisé)
npm run build --configuration=production
# → Génère dist/ folder
```

## 📦 Artefacts générés

### Backend

- **JAR exécutable Spring Boot** : target/mon-app-1.0.0.jar
- **Lancer l'app en prod** : `java -jar target/mon-app-1.0.0.jar`

### Frontend

- **Application Angular buildée** : app/dist/powerme/
- **Point d'entrée** : app/dist/powerme/index.html

## 🔧 Configuration développement

### Démarrage rapide

````bash
# 1. Démarrer PostgreSQL
docker compose -f docker-compose.dev.yml up -d

# 2. (Optionnel) Personnaliser la config
cp .env.dev.example .env.dev
# Modifier .env.dev selon vos besoins

# 3. Lancer le backend dans IntelliJ
# Active profiles: dev

---

## ✅ **Checklist : Ton setup est prêt si...**

```bash
# ✅ Docker Compose démarre la BDD
docker compose -f docker-compose.dev.yml up -d
docker compose -f docker-compose.dev.yml ps
# → db: Up, pgadmin: Up

# ✅ Spring Boot se connecte à la BDD
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
# → Logs "HikariPool-1 - Start completed"

# ✅ pgAdmin accessible
open http://localhost:5050
# → Page de login affichée

# ✅ Backend accessible
curl http://localhost:8080/actuator/health
# → {"status":"UP"}




## Déploiement
### Backend
Via un service systemd

- Préparation :
```bash
# 1. Créer utilisateur dédié (sécurité)
sudo useradd -r -s /bin/false appuser
sudo mkdir -p /opt/app
sudo mkdir -p /var/log/app

# 2. Copier le JAR
scp target/mon-app-1.0.0.jar user@serveur:/opt/app/app.jar

# 3. Permissions
sudo chown -R appuser:appuser /opt/app
sudo chown -R appuser:appuser /var/log/app
sudo chmod +x /opt/app/app.jar
````

- Service systemd : `/etc/systemd/system/mon-app.service``

```bash
[Unit]
Description=Mon App Spring Boot
After=network.target
StartLimitIntervalSec=0

[Service]
Type=simple
User=appuser
Group=appuser
ExecStart=/usr/bin/java -jar /opt/app/app.jar
ExecReload=/bin/kill -HUP $MAINPID
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal

# Variables d'environnement
Environment=SPRING_PROFILES_ACTIVE=prod
Environment=SERVER_PORT=8080
Environment=DB_HOST=localhost
Environment=DB_PORT=5432
Environment=DB_NAME=myapp
EnvironmentFile=-/opt/app/.env

# Sécurité
WorkingDirectory=/opt/app
PrivateTmp=true
NoNewPrivileges=true

# Logs
SyslogIdentifier=mon-app

[Install]
WantedBy=multi-user.target

```

- Fichier d'environnement : `/opt/app/.env`

```bash
# Base de données
DB_USER=app_user
DB_PASSWORD=SuperSecurePassword123!
JWT_SECRET=MonSuperSecretJWT2024!

# CORS pour le frontend
CORS_ALLOWED_ORIGINS=https://mon-app.com,https://www.mon-app.com

# Configuration serveur
LOGGING_LEVEL_ROOT=INFO
LOGGING_FILE_PATH=/var/log/app/app.log

```

- Gestion du service :

```bash
# Recharger systemd
sudo systemctl daemon-reload

# Activer au démarrage
sudo systemctl enable mon-app

# Démarrer le service
sudo systemctl start mon-app

# Vérifier le statut
sudo systemctl status mon-app

# Voir les logs en temps réel
sudo journalctl -f -u mon-app

# Redémarrer
sudo systemctl restart mon-app

# Arrêter
sudo systemctl stop mon-app
```

### Frontend

#### Déploiement sur serveur Nginx/Apache :

```bash
# Copie vers serveur :
sudo mkdir -p /var/www/mon-app
sudo cp -r dist/mon-app/* /var/www/mon-app/

# Permissions correctes
sudo chown -R www-data:www-data /var/www/mon-app
sudo chmod -R 755 /var/www/mon-app
```

#### Configuration d'un serveur Web NGINX pour app fullstack :

- Via server block HTTPS avec proxy vers JAR : `sudo nano /etc/nginx/sites-available/fullstack-app`

```bash
server {
    # Redirection HTTP → HTTPS
    listen 80;
    server_name mon-app.com www.mon-app.com;
    return 301 https://$server_name$request_uri;
}

server {
    # 🔐 HTTPS sécurisé
    listen 443 ssl http2;
    server_name mon-app.com www.mon-app.com;

    # Certificats SSL
    ssl_certificate /etc/letsencrypt/live/mon-app.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/mon-app.com/privkey.pem;

    # Configuration SSL moderne
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;

    # Headers de sécurité
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Frame-Options DENY always;
    add_header X-Content-Type-Options nosniff always;
    add_header Referrer-Policy strict-origin-when-cross-origin always;

    # 📁 Frontend Angular (fichiers statiques)
    root /var/www/mon-app;
    index index.html;

    # Gestion des routes Angular (SPA)
    location / {
        try_files $uri $uri/ /index.html;

        # Cache pour les fichiers statiques
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
            add_header Vary Accept-Encoding;
        }
    }

    # 🔗 Proxy vers le JAR Spring Boot
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $server_name;

        # Timeouts
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;

        # Pas de cache pour l'API
        add_header Cache-Control "no-cache, no-store, must-revalidate";
        add_header Pragma "no-cache";
        add_header Expires "0";
    }

    # Healthcheck accessible
    location /actuator/health {
        proxy_pass http://127.0.0.1:8080/actuator/health;
        proxy_set_header Host $host;
        access_log off;
    }

    # Logs
    access_log /var/log/nginx/mon-app.access.log;
    error_log /var/log/nginx/mon-app.error.log;

    # Compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css application/javascript application/json image/svg+xml;
}
```

- Finalisation de l'installation :

```bash
# Activer le site
sudo ln -s /etc/nginx/sites-available/mon-app /etc/nginx/sites-enabled/

# Tester la configuration
sudo nginx -t

# Recharger Nginx (sans interruption)
sudo systemctl reload nginx

# Redémarrer Nginx
sudo systemctl restart nginx
```

#### Configuration d'un serveur Web APACHE pour app fullstack :

- Via VirtualHost HTTPS : `sudo nano /etc/apache2/sites-available/mon-app-ssl.conf`

```bash
# Redirection HTTP → HTTPS
<VirtualHost *:80>
    ServerName mon-app.com
    ServerAlias www.mon-app.com
    DocumentRoot /var/www/mon-app

    # Redirection forcée vers HTTPS
    RewriteEngine On
    RewriteCond %{HTTPS} off
    RewriteRule ^(.*)$ https://%{HTTP_HOST}%{REQUEST_URI} [R=301,L]

    # Logs
    ErrorLog ${APACHE_LOG_DIR}/mon-app-error.log
    CustomLog ${APACHE_LOG_DIR}/mon-app-access.log combined
</VirtualHost>

# Configuration HTTPS principale
<VirtualHost *:443>
    ServerName mon-app.com
    ServerAlias www.mon-app.com
    DocumentRoot /var/www/mon-app

    # 🔐 SSL Configuration
    SSLEngine on
    SSLProtocol all -SSLv2 -SSLv3 -TLSv1 -TLSv1.1
    SSLCipherSuite ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-RSA-CHACHA20-POLY1305:DHE-RSA-AES128-GCM-SHA256:DHE-RSA-AES256-GCM-SHA384
    SSLHonorCipherOrder off

    # Certificats SSL (Let's Encrypt)
    SSLCertificateFile /etc/letsencrypt/live/mon-app.com/fullchain.pem
    SSLCertificateKeyFile /etc/letsencrypt/live/mon-app.com/privkey.pem

    # 🔒 Headers de sécurité
    Header always set Strict-Transport-Security "max-age=31536000; includeSubDomains"
    Header always set X-Content-Type-Options nosniff
    Header always set X-Frame-Options SAMEORIGIN
    Header always set Referrer-Policy strict-origin-when-cross-origin
    Header always set X-XSS-Protection "1; mode=block"

    # 📁 Frontend Angular
    DirectoryIndex index.html

    <Directory "/var/www/mon-app">
        AllowOverride All
        Require all granted

        # Configuration pour SPA Angular
        RewriteEngine On

        # Handle Angular Router
        RewriteBase /
        RewriteRule ^index\.html$ - [L]
        RewriteCond %{REQUEST_FILENAME} !-f
        RewriteCond %{REQUEST_FILENAME} !-d
        RewriteRule . /index.html [L]

        # Cache pour les assets statiques
        <FilesMatch "\.(css|js|png|jpg|jpeg|gif|ico|svg|woff|woff2)$">
            ExpiresActive On
            ExpiresDefault "access plus 1 year"
            Header set Cache-Control "public, immutable"
        </FilesMatch>

        # Pas de cache pour index.html
        <FilesMatch "index\.html$">
            Header set Cache-Control "no-cache, no-store, must-revalidate"
            Header set Pragma "no-cache"
            Header set Expires 0
        </FilesMatch>
    </Directory>

    # 🔗 Proxy vers Spring Boot JAR
    ProxyPreserveHost On
    ProxyRequests Off

    # API Backend
    <Location "/api/">
        ProxyPass "http://127.0.0.1:8080/api/"
        ProxyPassReverse "http://127.0.0.1:8080/api/"

        # Headers pour le reverse proxy
        ProxyPassReverseAdjustHeaders On
        ProxySetHeader Host %{HTTP_HOST}
        ProxySetHeader X-Real-IP %{REMOTE_ADDR}
        ProxySetHeader X-Forwarded-For %{REMOTE_ADDR}
        ProxySetHeader X-Forwarded-Proto %{REQUEST_SCHEME}
        ProxySetHeader X-Forwarded-Host %{HTTP_HOST}

        # Pas de cache pour l'API
        Header set Cache-Control "no-cache, no-store, must-revalidate"
        Header set Pragma "no-cache"
        Header set Expires 0
    </Location>

    # Healthcheck
    <Location "/api/actuator/health">
        ProxyPass "http://127.0.0.1:8080/api/actuator/health"
        ProxyPassReverse "http://127.0.0.1:8080/api/actuator/health"
    </Location>

    # 📊 Compression
    <Location "/">
        SetOutputFilter DEFLATE
        SetEnvIfNoCase Request_URI \
            \.(?:gif|jpe?g|png)$ no-gzip dont-vary
        SetEnvIfNoCase Request_URI \
            \.(?:exe|t?gz|zip|bz2|sit|rar)$ no-gzip dont-vary
    </Location>

    # 📝 Logs
    ErrorLog ${APACHE_LOG_DIR}/mon-app-ssl-error.log
    CustomLog ${APACHE_LOG_DIR}/mon-app-ssl-access.log combined

    # Log niveau SSL (debugging)
    LogLevel ssl:warn
</VirtualHost>
```

- Finalisation de l'installation :

```bash
# Activer le site
sudo a2ensite mon-app-ssl.conf

# Désactiver le site par défaut si besoin
sudo a2dissite 000-default.conf

# Tester la configuration
sudo apache2ctl configtest

# Recharger Apache
sudo systemctl reload apache2
```
