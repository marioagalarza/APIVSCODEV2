# Guía de Despliegue — APIVSCODEV2

## Prerrequisitos

| Herramienta  | Versión mínima | Verificar con          |
|--------------|----------------|------------------------|
| Java (JDK)   | 17             | `java -version`        |
| Maven        | 3.9+           | `./mvnw -version`      |
| Docker       | 24+            | `docker --version`     |
| Docker Compose | 2.x          | `docker compose version` |
| Make         | 4.x            | `make --version`       |

---

## Dockerfile

Estrategia multi-stage: la primera etapa compila con Maven; la segunda genera
una imagen de runtime mínima basada en `eclipse-temurin:17-jre`.

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

run: build
	SPRING_PROFILES_ACTIVE=local java -jar $(JAR)

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

---

## Proceso de promoción entre ambientes

El flujo de promoción sigue la cadena: **local → NPE → UAT → PROD**.

```
Desarrollador
    │
    ├─► build local  →  test local  →  push rama
    │
    ▼
[ NPE ]  ←── deploy automático desde rama main (CI/CD)
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
3. El pipeline de CD construye la imagen y la publica con el tag de versión:
   ```bash
   docker build -t apivscodev2:1.0.0 .
   docker tag apivscodev2:1.0.0 registry.marete.com.ar/apivscodev2:1.0.0
   docker push registry.marete.com.ar/apivscodev2:1.0.0
   ```
4. Desplegar la nueva imagen en PROD con `SPRING_PROFILES_ACTIVE=prod`.
5. Ejecutar smoke test contra `https://api.marete.com.ar/api/v1/hello`.
6. Si el smoke test falla: hacer rollback a la imagen anterior.

---

## Smoke test post-deploy

```bash
# Esperar a que la app levante y verificar respuesta
curl -sf https://api.marete.com.ar/api/v1/hello | grep -q "message" \
  && echo "OK" || echo "FALLO"
```

---

## Rollback

```bash
# Volver a la imagen anterior (reemplazar X.Y.Z por la versión estable previa)
docker pull registry.marete.com.ar/apivscodev2:X.Y.Z
docker tag registry.marete.com.ar/apivscodev2:X.Y.Z apivscodev2:prod
docker compose up -d
```

---

## Checklist de despliegue

- [ ] Crear `Dockerfile` en la raíz del proyecto
- [ ] Crear `docker-compose.yml`
- [ ] Crear `Makefile`
- [ ] Verificar imagen local con `make docker-run`
- [ ] Probar smoke test local: `curl http://localhost:8080/api/v1/hello`
- [ ] Configurar pipeline CI (build + test en cada push)
- [ ] Configurar pipeline CD (build image + push + deploy a NPE en merge a main)
- [ ] Definir proceso de aprobación para UAT y PROD
- [ ] Configurar monitoreo y alertas en PROD
