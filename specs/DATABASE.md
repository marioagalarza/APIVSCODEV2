# Base de Datos — APIVSCODEV2

## Estado actual

Este proyecto **no utiliza base de datos** en su versión inicial.  
El endpoint `GET /api/v1/hello` es completamente stateless.

No hay dependencia de `spring-boot-starter-data-jpa`, `spring-boot-starter-data-jdbc` ni ningún driver de BD en el `pom.xml`.

---

## Estructura futura

Cuando se incorpore persistencia, se adoptará el siguiente esquema:

### Stack previsto

| Componente      | Tecnología sugerida                     |
|-----------------|-----------------------------------------|
| Motor           | PostgreSQL 15+                          |
| Acceso          | Spring Data JPA + Hibernate             |
| Migraciones     | Flyway                                  |
| Pool            | HikariCP (incluido en Spring Boot)      |
| Tests           | H2 en memoria (scope test) o Testcontainers |

### Convenciones de naming

- Tablas en `snake_case`, plural: `users`, `hello_messages`
- PKs: `id` de tipo `BIGINT GENERATED ALWAYS AS IDENTITY`
- Timestamps: `created_at`, `updated_at` de tipo `TIMESTAMPTZ`
- Sin prefijos en nombre de columnas

### Ejemplo de entidad futura

```sql
-- V1__create_hello_messages.sql
CREATE TABLE hello_messages (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    message     VARCHAR(255) NOT NULL,
    locale      VARCHAR(10)  NOT NULL DEFAULT 'es',
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
```

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

    // ...
}
```

### Configuración YAML futura (por ambiente)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:apivscodev2}
    username: ${DB_USER:apivscodev2}
    password: ${DB_PASSWORD:secret}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
  jpa:
    hibernate:
      ddl-auto: validate        # nunca 'create' o 'create-drop' en prod
    open-in-view: false
  flyway:
    enabled: true
    locations: classpath:db/migration
```

---

## Checklist para incorporar BD

- [ ] Agregar `spring-boot-starter-data-jpa` y driver PostgreSQL al `pom.xml`
- [ ] Agregar dependencia Flyway
- [ ] Crear carpeta `src/main/resources/db/migration/`
- [ ] Escribir primera migración `V1__init.sql`
- [ ] Configurar datasource en cada `application-{env}.yaml`
- [ ] Agregar variables de entorno en el `Makefile` / `docker-compose.yml`
- [ ] Incorporar Testcontainers para tests de integración
