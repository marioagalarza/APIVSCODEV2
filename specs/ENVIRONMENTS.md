# Ambientes — APIVSCODEV2

El proyecto usa perfiles de Spring Boot (`spring.profiles.active`) para separar
la configuración por ambiente. El archivo base `application.yaml` contiene
los valores comunes; cada perfil sobreescribe lo necesario.

## Resumen de ambientes

| Ambiente | Perfil Spring | Propósito                              | URL base                         |
|----------|---------------|----------------------------------------|----------------------------------|
| Local    | `local`       | Desarrollo en máquina del desarrollador | `http://localhost:8080`          |
| NPE      | `npe`         | Non-Production Environment / QA interna | `http://api-npe.marete.com.ar`  |
| UAT      | `uat`         | User Acceptance Testing con stakeholders | `http://api-uat.marete.com.ar` |
| PROD     | `prod`        | Producción                             | `https://api.marete.com.ar`      |

---

## `application.yaml` (base — común a todos los perfiles)

```yaml
spring:
  application:
    name: apivscodev2

server:
  port: 8080
  servlet:
    context-path: /

logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

---

## `application-local.yaml`

```yaml
spring:
  config:
    activate:
      on-profile: local

server:
  port: 8080

logging:
  level:
    root: INFO
    ar.com.marete: DEBUG
```

Activar localmente:

```bash
# con Maven
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# con variable de entorno
SPRING_PROFILES_ACTIVE=local java -jar target/apivscodev2-0.0.1-SNAPSHOT.jar
```

---

## `application-npe.yaml`

```yaml
spring:
  config:
    activate:
      on-profile: npe

server:
  port: 8080

logging:
  level:
    root: INFO
    ar.com.marete: INFO
  pattern:
    console: '{"timestamp":"%d{yyyy-MM-dd HH:mm:ss}","level":"%level","logger":"%logger{36}","message":"%msg"}%n'
```

Variables de entorno requeridas en NPE:

| Variable                  | Descripción               |
|---------------------------|---------------------------|
| `SPRING_PROFILES_ACTIVE`  | Debe ser `npe`            |
| `SERVER_PORT`             | Puerto (default: `8080`)  |

---

## `application-uat.yaml`

```yaml
spring:
  config:
    activate:
      on-profile: uat

server:
  port: 8080

logging:
  level:
    root: WARN
    ar.com.marete: INFO
  pattern:
    console: '{"timestamp":"%d{yyyy-MM-dd HH:mm:ss}","level":"%level","logger":"%logger{36}","message":"%msg"}%n'
```

Variables de entorno requeridas en UAT:

| Variable                  | Descripción               |
|---------------------------|---------------------------|
| `SPRING_PROFILES_ACTIVE`  | Debe ser `uat`            |
| `SERVER_PORT`             | Puerto (default: `8080`)  |

---

## `application-prod.yaml`

```yaml
spring:
  config:
    activate:
      on-profile: prod

server:
  port: 8080
  tomcat:
    max-threads: 200

logging:
  level:
    root: WARN
    ar.com.marete: WARN
  pattern:
    console: '{"timestamp":"%d{yyyy-MM-dd HH:mm:ss}","level":"%level","logger":"%logger{36}","message":"%msg","trace":"%X{traceId}"}%n'
```

Variables de entorno requeridas en PROD:

| Variable                  | Descripción                   |
|---------------------------|-------------------------------|
| `SPRING_PROFILES_ACTIVE`  | Debe ser `prod`               |
| `SERVER_PORT`             | Puerto (default: `8080`)      |

> En PROD nunca se commitean secretos al repositorio. Toda configuración
> sensible se inyecta por variables de entorno o desde un gestor de secretos
> (HashiCorp Vault, AWS Secrets Manager, etc.).

---

## Checklist por ambiente

- [ ] Crear `src/main/resources/application-local.yaml`
- [ ] Crear `src/main/resources/application-npe.yaml`
- [ ] Crear `src/main/resources/application-uat.yaml`
- [ ] Crear `src/main/resources/application-prod.yaml`
- [ ] Agregar `application-*.yaml` al `.gitignore` si contienen secretos locales
- [ ] Documentar las variables de entorno en el `Makefile` o en un `.env.example`
