# ========================================
# STAGE 1: Build avec Maven
# ========================================
ARG JAVA_VERSION=21
FROM maven:3.9-eclipse-temurin-${JAVA_VERSION}-alpine AS builder

WORKDIR /app

# Copier pom.xml
COPY pom.xml ./

# Télécharger les dépendances (layer Docker caché)
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Build (skip tests car déjà faits dans la CI)
RUN mvn clean package -DskipTests -B

# ========================================
# STAGE 2: Image finale légère
# ========================================
FROM eclipse-temurin:${JAVA_VERSION}-jre-alpine

WORKDIR /app

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copier le JAR depuis le stage builder
COPY --from=builder /app/target/*.jar app.jar

# Exposer le port
EXPOSE 8080

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Démarrer l'application
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
