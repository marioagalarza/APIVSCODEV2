# Plan de Implementación — APIVSCODEV2

## Fase 1 — Bootstrapping del proyecto

- [x] Crear proyecto con Spring Initializr (Spring Boot 3.x, Java 17, Maven)
- [x] Configurar `application.yaml` base con nombre de aplicación
- [x] Verificar que la aplicación levanta correctamente
- [ ] Agregar `spring-boot-starter-web` para habilitar el servidor HTTP
- [ ] Configurar puerto por defecto (`8080`)
- [ ] Agregar health check en `/actuator/health` (optional: `spring-boot-starter-actuator`)

## Fase 2 — Endpoint `/api/v1/hello`

- [ ] Crear `HelloController` en `ar.com.marete.apivscodev2.controller`
- [ ] Implementar `GET /api/v1/hello` que devuelva un saludo en JSON
- [ ] Definir `HelloResponse` como record o DTO
- [ ] Escribir test de integración con `@SpringBootTest` + `MockMvc`
- [ ] Escribir test unitario del controller con `@WebMvcTest`

## Fase 3 — Documentación y contratos

- [x] Redactar specs en `specs/`
- [ ] Agregar dependencia `springdoc-openapi-starter-webmvc-ui`
- [ ] Exponer Swagger UI en `/swagger-ui.html`
- [ ] Verificar que el contrato generado coincide con `specs/API.md`

## Fase 4 — Configuración por ambiente

- [ ] Crear `application-local.yaml`
- [ ] Crear `application-npe.yaml`
- [ ] Crear `application-uat.yaml`
- [ ] Crear `application-prod.yaml`
- [ ] Parametrizar host, puerto y log level por perfil

## Fase 5 — Containerización

- [ ] Crear `Dockerfile` multi-stage (build + runtime)
- [ ] Crear `docker-compose.yml` para desarrollo local
- [ ] Crear `Makefile` con targets: `build`, `run`, `test`, `docker-build`, `docker-run`
- [ ] Verificar imagen en ambiente local

## Fase 6 — Pipeline CI/CD

- [ ] Definir pipeline de CI (build + test)
- [ ] Definir pipeline de CD (build image → push → deploy)
- [ ] Configurar variables de entorno por ambiente en el orquestador
- [ ] Documentar proceso de promoción NPE → UAT → PROD

## Deuda técnica / Backlog

- [ ] Agregar validación de inputs con `spring-boot-starter-validation`
- [ ] Centralizar manejo de errores con `@ControllerAdvice`
- [ ] Agregar logging estructurado (JSON) para ambientes no-local
- [ ] Configurar rate limiting
