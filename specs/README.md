# APIVSCODEV2 — Índice de Especificaciones

Proyecto Spring Boot REST API.

| Propiedad     | Valor                        |
|---------------|------------------------------|
| groupId       | ar.com.marete                |
| artifactId    | apivscodev2                  |
| Java          | 17                           |
| Spring Boot   | 3.5.x                        |
| Build         | Maven                        |
| Versión actual | 0.0.1-SNAPSHOT              |

## Documentos

| Archivo                               | Contenido                                              |
|---------------------------------------|--------------------------------------------------------|
| [PLAN.md](PLAN.md)                    | Plan de implementación por fases con checkboxes        |
| [API.md](API.md)                      | Contrato del API — endpoints, request/response, OpenAPI |
| [DATABASE.md](DATABASE.md)            | Modelo de datos (estructura futura, sin BD activa)     |
| [ENVIRONMENTS.md](ENVIRONMENTS.md)   | Ambientes (local, NPE, UAT, PROD) y configuración YAML |
| [DEPLOYMENT.md](DEPLOYMENT.md)        | Guía de despliegue con Docker, Make y promoción        |

## Estructura del proyecto

```
apivscodev2/
├── src/
│   ├── main/
│   │   ├── java/ar/com/marete/apivscodev2/
│   │   │   └── Apivscodev2Application.java
│   │   └── resources/
│   │       └── application.yaml
│   └── test/
│       └── java/ar/com/marete/apivscodev2/
│           └── Apivscodev2ApplicationTests.java
├── specs/                  ← estás aquí
├── pom.xml
└── mvnw
```

## Endpoints disponibles

| Método | Path              | Descripción         |
|--------|-------------------|---------------------|
| GET    | `/api/v1/hello`   | Devuelve un saludo  |
