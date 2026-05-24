# Plan de Implementación — APIVSCODEV2

## Fase 1 — Bootstrapping del proyecto

- [x] Crear proyecto con Spring Initializr (Spring Boot 3.x, Java 17, Maven)
- [x] Configurar `application.yaml` base con nombre de aplicación
- [x] Verificar que la aplicación levanta correctamente
- [x] Agregar `spring-boot-starter-web` para habilitar el servidor HTTP
- [x] Configurar puerto por defecto (`8080`)
- [x] Agregar health check en `/actuator/health` (optional: `spring-boot-starter-actuator`)

## Fase 2 — Endpoint `/api/v1/hello`

- [x] Crear `HelloController` en `ar.com.marete.apivscodev2.controller`
- [x] Implementar `GET /api/v1/hello` que devuelva un saludo en JSON
- [x] Definir `HelloResponse` como record o DTO
- [x] Escribir test de integración con `@SpringBootTest` + `MockMvc`
- [x] Escribir test unitario del controller con `@WebMvcTest`

## Fase 3 — Documentación y contratos

- [x] Redactar specs en `specs/`
- [x] Agregar dependencia `springdoc-openapi-starter-webmvc-ui`
- [x] Exponer Swagger UI en `/swagger-ui.html`
- [x] Verificar que el contrato generado coincide con `specs/API.md`

## Fase 4 — Configuración por ambiente

- [x] Crear `application-local.yaml`
- [x] Crear `application-npe.yaml`
- [x] Crear `application-uat.yaml`
- [x] Crear `application-prod.yaml`
- [x] Parametrizar host, puerto y log level por perfil

## Fase 5 — Containerización

- [x] Crear `Dockerfile` multi-stage (build + runtime)
- [x] Crear `docker-compose.yml` para desarrollo local
- [x] Crear `Makefile` con targets: `build`, `run`, `test`, `docker-build`, `docker-run`
- [x] Verificar imagen en ambiente local

## Fase 6 — Pipeline CI/CD

- [ ] Definir pipeline de CI con GitHub Actions (build + test)
- [ ] Definir pipeline de CD (build image → push → deploy)
- [ ] Configurar variables de entorno por ambiente en el orquestador
- [ ] Documentar proceso de promoción NPE → UAT → PROD

## Fase 7 — PostgreSQL

- [x] Agregar `spring-boot-starter-data-jpa` y driver PostgreSQL al `pom.xml`
- [x] Agregar servicio `postgres` en `docker-compose.yml` con healthcheck
- [x] Configurar `depends_on` con `condition: service_healthy` en el servicio `app`
- [x] Configurar datasource en cada `application-{env}.yml`
- [x] Agregar targets `db-up`, `db-down`, `db-logs`, `db-shell` al `Makefile`
- [x] Verificar conexión vía `GET /api/v1/db-health`
- [x] Actualizar `specs/DATABASE.md` con estado actual
- [ ] Agregar dependencia Flyway al `pom.xml`
- [ ] Crear carpeta `src/main/resources/db/migration/`
- [ ] Escribir migración `V1__init.sql`

## Fase 8 — Spring Security + JWT

- [ ] Agregar `spring-boot-starter-security` al `pom.xml`
- [ ] Agregar dependencia `jjwt` (io.jsonwebtoken)
- [ ] Crear migración `V2__create_users.sql`
- [ ] Crear entidad `User` con roles
- [ ] Implementar `UserDetailsService` con carga desde BD
- [ ] Crear `JwtService` (generación y validación de tokens)
- [ ] Crear `JwtAuthenticationFilter`
- [ ] Configurar `SecurityFilterChain` (rutas públicas vs protegidas)
- [ ] Implementar `POST /api/v1/auth/register`
- [ ] Implementar `POST /api/v1/auth/login`
- [ ] Proteger `GET /api/v1/hello` con autenticación JWT
- [ ] Actualizar `specs/API.md` con nuevos endpoints
- [ ] Escribir tests de integración para flujo auth completo

## Fase 9 — GitHub Actions CI/CD

- [ ] Crear `.github/workflows/ci.yml` (build + test en cada PR)
- [ ] Crear `.github/workflows/cd.yml` (build image → push a registry)
- [ ] Configurar secrets en GitHub (credenciales de registry, ambientes)
- [ ] Configurar variables por ambiente (NPE, UAT, PROD)
- [ ] Documentar proceso de promoción NPE → UAT → PROD
- [ ] Actualizar `specs/DEPLOYMENT.md`

## Fase 10 — BDD con Cucumber

- [ ] Agregar dependencias Cucumber al `pom.xml` (cucumber-spring, cucumber-junit-platform)
- [ ] Crear estructura `src/test/resources/features/`
- [ ] Escribir feature `hello.feature` como caso base
- [ ] Escribir feature `auth.feature` para flujo de autenticación
- [ ] Implementar step definitions
- [ ] Integrar ejecución de Cucumber en el ciclo Maven (`mvn test`)
- [ ] Configurar reporte HTML de Cucumber

---

## Deuda técnica / Backlog

- [ ] Agregar validación de inputs con `spring-boot-starter-validation`
- [ ] Centralizar manejo de errores con `@ControllerAdvice`
- [ ] Agregar logging estructurado (JSON) para ambientes no-local
- [ ] Configurar rate limiting
- [ ] Incorporar Testcontainers para tests de integración con BD real
