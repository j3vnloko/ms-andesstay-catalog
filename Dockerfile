# ============================
# Stage 1: Build
# ============================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# ============================
# Stage 2: Runtime
# ============================
FROM eclipse-temurin:17-jre
WORKDIR /app

# Instalar curl para healthcheck
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

# Usuario no-root por seguridad
RUN groupadd -r appuser && useradd -r -g appuser appuser

COPY --from=build /app/target/ms-andesstay-catalog-*.jar app.jar

RUN chown -R appuser:appuser /app
USER appuser

EXPOSE 8081

HEALTHCHECK --interval=10s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
