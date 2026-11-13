# ms-andesstay-catalog

Microservicio de dominio del caso **AndesStay** (DSY1107 - Desarrollo Cloud Native I, Duoc UC).

Responsable del dominio **Catálogo de Unidades**: CRUD de habitaciones, cabañas y lodges con tarifas y disponibilidad.

---

## Stack

- **Java 17**
- **Spring Boot 3.3.4** (Web, Data JPA, Validation, Security, Actuator)
- **Spring Security OAuth2 Resource Server** (JWT - AWS Cognito)
- **PostgreSQL 16** (contenedor Docker local o RDS)
- **Docker & Docker Compose** (Docker Desktop)
- **Maven 3.9+**

---

## Arquitectura de Seguridad

El microservicio actúa como **Resource Server** OAuth2, validando access tokens JWT emitidos por AWS Cognito.

### Validaciones del token

| Validación       | Detalle                                                                 |
|------------------|-------------------------------------------------------------------------|
| **Issuer**       | `https://cognito-idp.{region}.amazonaws.com/{userPoolId}`               |
| **client_id**    | Claim `client_id` del access token debe coincidir con el App Client ID  |
| **token_use**    | Debe ser `"access"` (se rechazan id tokens)                            |
| **Firma (JWKS)** | Nimbus descarga automáticamente las claves del endpoint `.well-known/jwks.json` |
| **Vigencia**     | Se verifica `exp` automáticamente                                       |

### Roles y autorización

Los roles se extraen del claim `cognito:groups` del access token:

| Grupo Cognito | Authority Spring     | Permisos                              |
|---------------|----------------------|---------------------------------------|
| `ADMIN`       | `ROLE_ADMIN`         | GET, POST, PUT, DELETE                |
| `USER`        | `ROLE_USER`          | Solo GET (lectura)                    |

### Códigos de error de seguridad

| Código | Situación                              | Respuesta JSON                                                       |
|--------|----------------------------------------|----------------------------------------------------------------------|
| `401`  | Sin token o token inválido/expirado    | `{"error": "No autenticado. Incluya un Bearer token valido..."}` |
| `403`  | Token válido pero sin rol requerido    | `{"error": "Acceso denegado. No tiene los permisos necesarios..."}` |

---

## Variables de Entorno

| Variable                    | Descripción                                  | Default local                                       |
|-----------------------------|----------------------------------------------|-----------------------------------------------------|
| `DB_URL`                    | JDBC URL de PostgreSQL                       | `jdbc:postgresql://postgres:5432/andesstay_catalog` |
| `DB_USERNAME`               | Usuario de la BD                             | `postgres`                                          |
| `DB_PASSWORD`               | Contraseña de la BD                          | `postgres`                                          |
| `COGNITO_REGION`            | Región del User Pool                         | `us-east-1`                                         |
| `COGNITO_USER_POOL_ID`      | ID del User Pool                             | `CHANGE_ME`                                         |
| `COGNITO_APP_CLIENT_ID`     | App Client ID de Cognito                     | `CHANGE_ME`                                         |
| `FRONTEND_URL`              | Origen permitido para CORS (Angular)         | `http://localhost:4200`                              |
| `CORS_ENABLED`              | Habilitar CORS en el microservicio           | `true`                                               |

---

## Ejecución Local con Docker Desktop

Este microservicio está configurado para ejecutarse localmente con **Docker Desktop** junto con su propia base de datos PostgreSQL en un contenedor dedicado.

### Requisitos previos

1. Tener instalado y abierto **Docker Desktop** (asegurarse de que el ícono esté en verde / Engine running).
2. Crear tu archivo de variables locales a partir de la plantilla:
   ```bash
   cp .env.example .env
   ```
   Ajusta en `.env` los valores de tu User Pool de AWS Cognito (`COGNITO_USER_POOL_ID` y `COGNITO_APP_CLIENT_ID`).

---

### Opción 1: Levantar todo con Docker Compose (Recomendado)

Docker Compose levantará en una misma red (`andesstay-net`):
1. **`andesstay-catalog-db`**: Contenedor PostgreSQL 16 que monta automáticamente `schema.sql` y `data.sql` en su inicio inicial (`/docker-entrypoint-initdb.d/`), creando las tablas y 5 unidades de ejemplo.
2. **`ms-andesstay-catalog`**: Contenedor con la aplicación Spring Boot construida en un build multi-stage (usuario no-root, puerto 8081).

#### Paso a paso:

1. **Construir y levantar los contenedores:**
   ```bash
   docker compose up --build -d
   ```

2. **Verificar el estado de los contenedores:**
   ```bash
   docker compose ps
   ```
   Ambos contenedores deben figurar en estado `Up` / `healthy`.

3. **Ver los logs en tiempo real:**
   ```bash
   docker compose logs -f catalog-service
   ```

4. **Comprobar el Health Check:**
   Abre en el navegador o terminal:
   ```bash
   curl http://localhost:8081/actuator/health
   ```
   Respuesta esperada: `{"status":"UP"}`.

5. **Detener los servicios:**
   ```bash
   docker compose down
   ```
   Si deseas eliminar también el volumen de la base de datos para reinicializar desde cero:
   ```bash
   docker compose down -v
   ```

---

### Opción 2: Correr solo el contenedor del Microservicio (PostgreSQL en el Host)

Si ya tienes PostgreSQL instalado y corriendo localmente en tu máquina (Windows/Mac/Linux):

1. Construir la imagen Docker:
   ```bash
   docker build -t andesstay-catalog .
   ```

2. Ejecutar apuntando a la base de datos de tu máquina con `host.docker.internal`:
   ```bash
   docker run -d --name catalog \
     -p 8081:8081 \
     -e DB_URL=jdbc:postgresql://host.docker.internal:5432/andesstay_catalog \
     -e DB_USERNAME=postgres \
     -e DB_PASSWORD=tu_password \
     -e COGNITO_REGION=us-east-1 \
     -e COGNITO_USER_POOL_ID=us-east-1_XXXXXXXXX \
     -e COGNITO_APP_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx \
     -e FRONTEND_URL=http://localhost:4200 \
     -e CORS_ENABLED=true \
     andesstay-catalog
   ```

---

## Ejecución Local sin Docker (Maven CLI)

Si deseas ejecutarlo directamente en tu terminal:

1. Asegúrate de tener PostgreSQL corriendo en `localhost:5432` con la base `andesstay_catalog`.
2. Exportar variables:
   ```bash
   export DB_PASSWORD=postgres
   export COGNITO_REGION=us-east-1
   export COGNITO_USER_POOL_ID=us-east-1_XXXXXXXXX
   export COGNITO_APP_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx
   ```
3. Ejecutar:
   ```bash
   mvn spring-boot:run
   ```

---

## Cómo Correr los Tests

```bash
mvn clean verify
```

Los tests se ejecutan sobre una base H2 en memoria y simulan el `JwtDecoder`. **No requieren conexión a Cognito ni a PostgreSQL real**.

---

## Endpoints

| Método   | Ruta                          | Rol requerido | Descripción                     |
|----------|-------------------------------|---------------|---------------------------------|
| `GET`    | `/api/catalog/units`          | ADMIN o USER  | Lista todas las unidades        |
| `GET`    | `/api/catalog/units/{id}`     | ADMIN o USER  | Obtiene una unidad por id       |
| `POST`   | `/api/catalog/units`          | ADMIN         | Crea una unidad                 |
| `PUT`    | `/api/catalog/units/{id}`     | ADMIN         | Actualiza una unidad            |
| `DELETE` | `/api/catalog/units/{id}`     | ADMIN         | Elimina una unidad              |
| `GET`    | `/actuator/health`            | Público       | Health check de la aplicación   |

### Body de ejemplo (POST / PUT)

```json
{
  "name": "Cabaña Lago 1",
  "type": "CABANA",
  "capacity": 4,
  "pricePerNight": 45000,
  "available": true
}
```

- `type`: valores válidos: `HABITACION`, `CABANA`, `LODGE`
- `capacity`: obligatorio, número positivo
- `pricePerNight`: obligatorio, número positivo

### Ejemplos de respuesta JSON

**GET /api/catalog/units**
```json
[
  {
    "id": 1,
    "name": "Habitacion Estandar 101",
    "type": "HABITACION",
    "capacity": 2,
    "pricePerNight": 35000.00,
    "available": true
  },
  {
    "id": 2,
    "name": "Habitacion Doble 202",
    "type": "HABITACION",
    "capacity": 4,
    "pricePerNight": 55000.00,
    "available": true
  },
  {
    "id": 3,
    "name": "Cabana Lago Azul",
    "type": "CABANA",
    "capacity": 6,
    "pricePerNight": 85000.00,
    "available": true
  }
]
```

**GET /api/catalog/units/1**
```json
{
  "id": 1,
  "name": "Habitacion Estandar 101",
  "type": "HABITACION",
  "capacity": 2,
  "pricePerNight": 35000.00,
  "available": true
}
```

**POST /api/catalog/units → 201**
```json
{
  "id": 6,
  "name": "Lodge Premium",
  "type": "LODGE",
  "capacity": 8,
  "pricePerNight": 120000.00,
  "available": true
}
```

**DELETE /api/catalog/units/6 → 204 (sin cuerpo)**

**Error 401 (sin token o token inválido)**
```json
{
  "error": "No autenticado. Incluya un Bearer token valido en el header Authorization."
}
```

**Error 403 (rol insuficiente)**
```json
{
  "error": "Acceso denegado. No tiene los permisos necesarios para esta operacion."
}
```

**Error 400 (error de validación)**
```json
{
  "error": "Error de validacion",
  "details": {
    "name": "El nombre es obligatorio",
    "capacity": "La capacidad es obligatoria"
  }
}
```

**Error 404 (unidad no encontrada)**
```json
{
  "error": "No se encontro la unidad con id 99"
}
```

---

## Pruebas con `curl` hacia Docker

```bash
# 1. Health check (público) -> 200 OK
curl -i http://localhost:8081/actuator/health

# 2. Sin token -> 401 Unauthorized
curl -i http://localhost:8081/api/catalog/units

# 3. GET con token USER -> 200 OK
curl -i -H "Authorization: Bearer $USER_TOKEN" http://localhost:8081/api/catalog/units

# 4. POST con token USER -> 403 Forbidden
curl -i -X POST http://localhost:8081/api/catalog/units \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Cabaña Bosque","type":"CABANA","capacity":4,"pricePerNight":60000,"available":true}'

# 5. POST con token ADMIN -> 201 Created
curl -i -X POST http://localhost:8081/api/catalog/units \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Lodge Valle Nevado","type":"LODGE","capacity":6,"pricePerNight":110000,"available":true}'
```

---

## Equipo

Jean Flores, Christian Fuentes, Emmanuel Valenzuela.
