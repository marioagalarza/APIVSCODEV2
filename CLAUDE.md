# CLAUDE.md — APIVSCODEV2

## Contexto del proyecto

API REST construida con Spring Boot 3.x, Java 17, Maven.
Metodología: Spec Driven Development. Las specs viven en specs/ y son la fuente de verdad.
Antes de implementar cualquier feature, consultá specs/ para entender el contrato esperado.

## Stack

- Java 17 + Spring Boot 3.x + Maven
- PostgreSQL 16 (Docker) + Spring Data JPA + HikariCP
- Flyway (migraciones — pendiente)
- Docker + Docker Compose + Makefile
- WSL2 Ubuntu

## Estructura de paquetes

ar.com.marete.apivscodev2
├── controller/     → endpoints REST y DTOs de respuesta
├── service/        → lógica de negocio
├── repository/     → interfaces Spring Data JPA
├── entity/         → entidades JPA
├── config/         → configuración de Spring (Security, OpenAPI, etc.)
└── exception/      → manejo centralizado de errores

## Convenciones Java

- Clases en PascalCase, métodos y variables en camelCase
- DTOs de respuesta como records: public record HelloResponse(String message) {}
- DTOs de request como records con @Valid
- Entidades JPA con @Entity, @Table(name = "nombre_en_snake_case")
- PKs: @GeneratedValue(strategy = GenerationType.IDENTITY)
- Timestamps: OffsetDateTime para created_at y updated_at
- Inyección de dependencias siempre por constructor, nunca @Autowired en campo
- Sin lógica en controllers — delegar siempre a un Service

## Convenciones de endpoints

- Base path: /api/v1/
- Sustantivos en plural: /api/v1/users, /api/v1/messages
- Respuestas siempre con ResponseEntity<T>
- HTTP status explícito: 200, 201, 400, 401, 403, 404, 503

## Convenciones de tests

- Tests unitarios con @WebMvcTest para controllers
- Tests de integración con @SpringBootTest + MockMvc
- Tests de servicio con @ExtendWith(MockitoExtension.class)
- Nombre del método: given_when_then o should_when
- Un assert por test siempre que sea posible

## Migraciones Flyway

- Ubicación: src/main/resources/db/migration/
- Nomenclatura: V{n}__{descripcion_en_snake_case}.sql
- Ejemplo: V1__init.sql, V2__create_users.sql
- Nunca modificar una migración ya aplicada — siempre crear una nueva

## Comandos frecuentes

```bash
make run          # levantar app en perfil local (puerto 8082)
make db-up        # levantar PostgreSQL en Docker
make db-down      # detener PostgreSQL
make db-shell     # abrir psql en el contenedor
make docker-build # construir imagen Docker
make docker-run   # levantar stack completo con docker-compose
make clean        # limpiar build y contenedores
./mvnw test       # correr todos los tests
```

## Ambientes

| Ambiente | Perfil Spring | DB           | ddl-auto    |
|----------|---------------|--------------|-------------|
| Local    | local         | Docker local | create-drop |
| NPE      | npe           | ${DB_URL}    | validate    |
| UAT      | uat           | ${DB_URL}    | validate    |
| PROD     | prod          | ${DB_URL}    | validate    |

## Specs

- specs/PLAN.md          → fases de implementación con checkboxes
- specs/API.md           → contratos de endpoints
- specs/DATABASE.md      → esquema de BD y convenciones
- specs/ENVIRONMENTS.md  → configuración por ambiente
- specs/DEPLOYMENT.md    → proceso de despliegue

## Reglas importantes

- Nunca hardcodear credenciales — siempre variables de entorno fuera de local
- Nunca usar ddl-auto: create o create-drop fuera del perfil local
- Nunca @Autowired en campo — siempre inyección por constructor
- Nunca lógica de negocio en controllers
- Siempre escribir el test antes o junto con la implementación
