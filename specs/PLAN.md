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

- [x] Definir pipeline de CI (build + test)
- [x] Definir pipeline de CD (build image → push → deploy)
- [x] Configurar variables de entorno por ambiente en el orquestador
- [x] Documentar proceso de promoción NPE → UAT → PROD

## Deuda técnica / Backlog

- [ ] Agregar validación de inputs con `spring-boot-starter-validation`
- [ ] Centralizar manejo de errores con `@ControllerAdvice`
- [ ] Agregar logging estructurado (JSON) para ambientes no-local
- [ ] Configurar rate limiting
