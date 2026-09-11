# ms-andesstay-catalog

Microservicio de dominio del caso **AndesStay** (EP1 - DSY1107, Desarrollo Cloud Native I).

Responsable del dominio **Unidades / disponibilidad**: CRUD de habitaciones/cabañas, cupos y tarifas.

## Stack

- Java 17
- Spring Boot 3.3.4 (Web, Data JPA, Validation)
- PostgreSQL en la fase local (recomendado por la profesora). En el caso completo se especifica Oracle para producción/cloud.

## Cómo correrlo local

1. Instalar PostgreSQL (o levantarlo en Docker) y crear la base manualmente, ya que a diferencia de MySQL no se crea sola:
   ```bash
   psql -U postgres -c "CREATE DATABASE andesstay_catalog;"
   ```
2. Si tu usuario/clave de Postgres no son `postgres`/`postgres`, exporta las variables `DB_USERNAME` y `DB_PASSWORD` antes de correr.
3. Ejecutar:
   ```bash
   mvn spring-boot:run
   ```
4. El servicio queda arriba en `http://localhost:8081`.

## Endpoints

| Método | Ruta                        | Descripción                    |
|--------|-----------------------------|---------------------------------|
| GET    | `/api/catalog/units`        | Lista todas las unidades        |
| GET    | `/api/catalog/units/{id}`   | Obtiene una unidad por id       |
| POST   | `/api/catalog/units`        | Crea una unidad                 |
| PUT    | `/api/catalog/units/{id}`   | Actualiza tarifa/disponibilidad |
| DELETE | `/api/catalog/units/{id}`   | Elimina una unidad              |

### Ejemplo body (POST / PUT)

```json
{
  "name": "Cabaña Lago 1",
  "type": "CABANA",
  "capacity": 4,
  "pricePerNight": 45000,
  "available": true
}
```

## Pendiente (próxima fase)

- Filtro de validación de JWT (recibido desde el BFF) cuando se conecte con Azure AD.
- Despliegue en EC2 + configuración con AWS API Gateway.

## Equipo

Jean Flores, Christian Fuentes, Emmanuel Valenzuela.
