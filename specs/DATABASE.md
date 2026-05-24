# Base de Datos — APIVSCODEV2

## Estado actual

Este proyecto **utiliza PostgreSQL 16** como motor de base de datos a partir de la Fase 2.

La conexión se gestiona mediante **Spring Data JPA + Hibernate** con pool **HikariCP**.  
Las migraciones de esquema son responsabilidad de **Flyway** (pendiente de incorporar — ver checklist).

> **Nota:** El endpoint `GET /api/v1/hello` sigue siendo stateless y no interactúa con la BD.  
> La conexión se puede verificar en `GET /api/v1/db-health`.

---

## Stack de persistencia

| Componente      | Tecnología                              | Estado         |
|-----------------|-----------------------------------------|----------------|
| Motor           | PostgreSQL 16 (Alpine)                  | ✅ Incorporado  |
| Acceso          | Spring Data JPA + Hibernate             | ✅ Incorporado  |
| Pool            | HikariCP (incluido en Spring Boot)      | ✅ Incorporado  |
| Migraciones     | Flyway                                  | ⏳ Pendiente    |
| Tests           | Testcontainers                          | ⏳ Pendiente    |

---

## Configuración por ambiente

### Local (docker-compose)

La DB corre como servicio Docker junto a la app. Las credenciales son fijas y solo para desarrollo.

```yaml
# application-local.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/apivscodev2
    username: appuser
    password: apppassword
  jpa:
    hibernate:
      ddl-auto: create-drop   # recrea el schema en cada arranque
    show-sql: true
```

Servicio en `docker-compose.yml`:

```yaml
postgres:
  image: postgres:16-alpine
  container_name: apivscodev2_postgres
  ports:
    - "5432:5432"
  environment:
    POSTGRES_DB: apivscodev2
    POSTGRES_USER: appuser
    POSTGRES_PASSWORD: apppassword
  volumes:
    - postgres_data:/var/lib/postgresql/data
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U appuser -d apivscodev2"]
    interval: 5s
    timeout: 5s
    retries: 5
```

### NPE / UAT / PROD

Todas las credenciales se inyectan como variables de entorno. **Nunca valores hardcodeados.**

```yaml
# application-npe.yml / application-uat.yml / application-prod.yml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
  jpa:
    hibernate:
      ddl-auto: validate      # nunca 'create' o 'create-drop' fuera de local
    open-in-view: false
  flyway:
    enabled: true
    locations: classpath:db/migration
```

---

## Convenciones de naming

- Tablas en `snake_case`, plural: `users`, `hello_messages`
- PKs: `id` de tipo `BIGINT GENERATED ALWAYS AS IDENTITY`
- Timestamps: `created_at`, `updated_at` de tipo `TIMESTAMPTZ`
- Sin prefijos en nombres de columna

---

## Migraciones Flyway (estructura futura)

Una vez incorporado Flyway, las migraciones vivirán en:

```
src/main/resources/db/migration/
├── V1__init.sql
├── V2__create_users.sql       ← necesario para Spring Security + JWT
└── ...
```

### Ejemplo — primera migración

```sql
-- V1__init.sql
CREATE TABLE hello_messages (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    message     VARCHAR(255) NOT NULL,
    locale      VARCHAR(10)  NOT NULL DEFAULT 'es',
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
```

### Ejemplo — entidad JPA correspondiente

```java
@Entity
@Table(name = "hello_messages")
public class HelloMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String message;

    @Column(nullable = false, length = 10)
    private String locale;

    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
```

---

## Checklist de incorporación

### Fase 2 — Conexión base (completada)

- [x] Agregar `spring-boot-starter-data-jpa` y driver PostgreSQL al `pom.xml`
- [x] Configurar datasource en cada `application-{env}.yml`
- [x] Agregar servicio `postgres` en `docker-compose.yml` con healthcheck
- [x] Configurar `depends_on` con `condition: service_healthy` en el servicio `app`
- [x] Agregar targets `db-up`, `db-down`, `db-logs`, `db-shell` al `Makefile`
- [x] Verificar conexión vía `GET /api/v1/db-health`

### Fase 3 — Migraciones y esquema (pendiente)

- [ ] Agregar dependencia Flyway al `pom.xml`
- [ ] Crear carpeta `src/main/resources/db/migration/`
- [ ] Escribir migración `V1__init.sql`
- [ ] Escribir migración `V2__create_users.sql` (requerida para Spring Security)
- [ ] Habilitar `flyway.enabled: true` en todos los ambientes no-local

### Fase 4 — Tests de integración (pendiente)

- [ ] Agregar dependencia Testcontainers al `pom.xml` (scope `test`)
- [ ] Configurar `@Testcontainers` en tests de integración
- [ ] Reemplazar H2 por Testcontainers con imagen `postgres:16-alpine`
