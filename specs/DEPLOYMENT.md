# Guía de Despliegue — APIVSCODEV2

## Prerrequisitos

| Herramienta    | Versión mínima | Verificar con              |
|----------------|----------------|----------------------------|
| Java (JDK)     | 17             | `java -version`            |
| Maven          | 3.9+           | `./mvnw -version`          |
| Docker         | 24+            | `docker --version`         |
| Docker Compose | 2.x            | `docker compose version`   |
| Make           | 4.x            | `make --version`           |

---

## Dockerfile

Estrategia multi-stage: la etapa `builder` compila el JAR con Maven; la etapa
`runtime` genera una imagen mínima basada en `eclipse-temurin:17-jre` con un
usuario no-root (`appuser`) por seguridad.

```dockerfile
# ── Etapa 1: build ──────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -q

COPY src ./src
RUN ./mvnw package -DskipTests -q

# ── Etapa 2: runtime ────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
USER appuser

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

El perfil de Spring Boot se inyecta en runtime vía la variable de entorno
`SPRING_PROFILES_ACTIVE`; la imagen es agnóstica al ambiente.

---

## docker-compose.yml (desarrollo local)

```yaml
services:
  api:
    build:
      context: .
      target: runtime
    image: apivscodev2:local
    container_name: apivscodev2_local
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: local
    restart: unless-stopped
```

---

## Makefile

```makefile
APP      := apivscodev2
VERSION  := 0.0.1-SNAPSHOT
JAR      := target/$(APP)-$(VERSION).jar
IMAGE    := $(APP)

.PHONY: build test run docker-build docker-run docker-stop clean

build:
	./mvnw package -DskipTests -q

test:
	./mvnw test

run:
	./mvnw spring-boot:run -Dspring-boot.run.profiles=local

docker-build:
	docker build -t $(IMAGE):local .

docker-run: docker-build
	docker compose up -d

docker-stop:
	docker compose down

clean:
	./mvnw clean
	docker compose down --rmi local 2>/dev/null || true
```

### Referencia rápida de comandos

| Comando           | Descripción                                              |
|-------------------|----------------------------------------------------------|
| `make run`        | Levanta la app en local con Spring Boot DevTools activo  |
| `make test`       | Ejecuta la suite de tests con Maven                      |
| `make build`      | Compila y empaqueta el JAR (sin tests)                   |
| `make docker-build` | Construye la imagen Docker `apivscodev2:local`         |
| `make docker-run` | Construye la imagen y levanta el contenedor en background |
| `make docker-stop` | Detiene y elimina el contenedor                         |

---

## Correr la app en cada ambiente

### Local — sin Docker (desarrollo día a día)

```bash
make run
# equivale a: ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

La app queda en `http://localhost:8080`.

### Local — con Docker

```bash
make docker-run
# construye apivscodev2:local y levanta docker-compose con perfil local
```

Para detener:

```bash
make docker-stop
```

### NPE

La imagen se construye en CI y se despliega vía CD. Para correr manualmente
en un servidor con la imagen publicada:

```bash
docker pull registry.marete.com.ar/apivscodev2:<TAG>
docker run -d \
  --name apivscodev2_npe \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=npe \
  registry.marete.com.ar/apivscodev2:<TAG>
```

URL base: `http://api-npe.marete.com.ar`

### UAT

```bash
docker pull registry.marete.com.ar/apivscodev2:<TAG>
docker run -d \
  --name apivscodev2_uat \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=uat \
  registry.marete.com.ar/apivscodev2:<TAG>
```

URL base: `http://api-uat.marete.com.ar`

### PROD

```bash
docker pull registry.marete.com.ar/apivscodev2:<VERSION>
docker run -d \
  --name apivscodev2_prod \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  registry.marete.com.ar/apivscodev2:<VERSION>
```

URL base: `https://api.marete.com.ar`

> En PROD nunca se commitean secretos. Toda configuración sensible se inyecta
> por variables de entorno o desde un gestor de secretos (HashiCorp Vault,
> AWS Secrets Manager, etc.).

---

## Proceso de promoción entre ambientes

El flujo de promoción sigue la cadena: **local → NPE → UAT → PROD**.

```
Desarrollador
    │
    ├─► make run / make test  →  push rama
    │
    ▼
[ NPE ]  ←── deploy automático desde merge a main (CD)
    │         SPRING_PROFILES_ACTIVE=npe
    │
    ▼ (aprobación QA)
[ UAT ]  ←── deploy manual o tag release candidate
    │         SPRING_PROFILES_ACTIVE=uat
    │
    ▼ (aprobación stakeholders)
[ PROD ] ←── deploy desde tag semver vX.Y.Z
              SPRING_PROFILES_ACTIVE=prod
```

### Pasos para promover a PROD

1. Verificar que todos los tests pasan en CI.
2. Crear tag semver en git:
   ```bash
   git tag -a v1.0.0 -m "Release v1.0.0"
   git push origin v1.0.0
   ```
3. El pipeline de CD construye la imagen y la publica:
   ```bash
   docker build -t apivscodev2:1.0.0 .
   docker tag apivscodev2:1.0.0 registry.marete.com.ar/apivscodev2:1.0.0
   docker push registry.marete.com.ar/apivscodev2:1.0.0
   ```
4. Desplegar con `SPRING_PROFILES_ACTIVE=prod`.
5. Ejecutar smoke test (ver sección siguiente).
6. Si el smoke test falla: hacer rollback a la imagen anterior.

---

## Smoke test post-deploy

```bash
# Local
curl -sf http://localhost:8080/api/v1/hello | grep -q "message" \
  && echo "OK" || echo "FALLO"

# PROD
curl -sf https://api.marete.com.ar/api/v1/hello | grep -q "message" \
  && echo "OK" || echo "FALLO"
```

---

## Rollback

```bash
# Reemplazar X.Y.Z por la versión estable previa
docker pull registry.marete.com.ar/apivscodev2:X.Y.Z
docker tag registry.marete.com.ar/apivscodev2:X.Y.Z apivscodev2:prod
docker compose up -d
```

---

## GitHub Actions — Secrets requeridos

Configurar en **Settings → Secrets and variables → Actions** del repositorio:

| Secret               | Descripción                                                  |
|----------------------|--------------------------------------------------------------|
| `REGISTRY_USER`      | Usuario con permisos de push en `registry.marete.com.ar`    |
| `REGISTRY_PASSWORD`  | Password o token del registry Docker                         |
| `NPE_DEPLOY_HOST`    | IP o hostname del servidor NPE                               |
| `NPE_DEPLOY_KEY`     | Clave SSH privada del usuario `deploy` en NPE                |
| `PROD_DEPLOY_HOST`   | IP o hostname del servidor PROD                              |
| `PROD_DEPLOY_KEY`    | Clave SSH privada del usuario `deploy` en PROD               |

### Flujo de triggers

| Evento                      | Pipeline                   | Destino                                         |
|-----------------------------|----------------------------|-------------------------------------------------|
| Push a cualquier rama o PR  | `ci.yml` — build + test    | —                                               |
| Merge a `main`              | `cd.yml` — `deploy-npe`    | NPE con `SPRING_PROFILES_ACTIVE=npe`            |
| Push de tag `v*.*.*`        | `cd.yml` — `deploy-prod`   | PROD con `SPRING_PROFILES_ACTIVE=prod` + smoke  |

---

## Checklist de despliegue

- [x] Crear `Dockerfile` multistage en la raíz del proyecto
- [x] Crear `docker-compose.yml` para ambiente local
- [x] Crear `Makefile` con targets: `run`, `test`, `build`, `docker-build`, `docker-run`, `docker-stop`
- [ ] Verificar imagen local con `make docker-run`
- [ ] Probar smoke test local: `curl http://localhost:8080/api/v1/hello`
- [ ] Configurar pipeline CI (build + test en cada push)
- [ ] Configurar pipeline CD (build image + push + deploy a NPE en merge a main)
- [ ] Definir proceso de aprobación para UAT y PROD
- [ ] Configurar monitoreo y alertas en PROD
