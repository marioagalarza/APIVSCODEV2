# Especificación del API — APIVSCODEV2

Base URL por ambiente:

| Ambiente | Base URL                              |
|----------|---------------------------------------|
| local    | `http://localhost:8080`               |
| NPE      | `http://api-npe.marete.com.ar`        |
| UAT      | `http://api-uat.marete.com.ar`        |
| PROD     | `https://api.marete.com.ar`           |

---

## GET /api/v1/hello

Devuelve un mensaje de saludo. No requiere autenticación.

### Request

```
GET /api/v1/hello HTTP/1.1
Accept: application/json
```

Sin parámetros ni body.

### Response — 200 OK

```json
{
  "message": "Hello from APIVSCODEV2!"
}
```

| Campo     | Tipo   | Descripción          |
|-----------|--------|----------------------|
| `message` | string | Texto del saludo     |

### Códigos de respuesta

| Código | Descripción                          |
|--------|--------------------------------------|
| 200    | Saludo devuelto correctamente        |
| 500    | Error interno del servidor           |

---

## Contrato OpenAPI 3.0 (YAML)

```yaml
openapi: "3.0.3"
info:
  title: APIVSCODEV2
  description: API de ejemplo con Spring Boot 3.x
  version: "0.0.1-SNAPSHOT"
  contact:
    email: mario.alberto.galarza@gmail.com

servers:
  - url: http://localhost:8080
    description: Local
  - url: http://api-npe.marete.com.ar
    description: NPE
  - url: http://api-uat.marete.com.ar
    description: UAT
  - url: https://api.marete.com.ar
    description: PROD

paths:
  /api/v1/hello:
    get:
      summary: Saludo de bienvenida
      operationId: getHello
      tags:
        - hello
      responses:
        "200":
          description: Saludo devuelto correctamente
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/HelloResponse"
              example:
                message: "Hello from APIVSCODEV2!"
        "500":
          description: Error interno del servidor
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/ErrorResponse"

components:
  schemas:
    HelloResponse:
      type: object
      required:
        - message
      properties:
        message:
          type: string
          example: "Hello from APIVSCODEV2!"

    ErrorResponse:
      type: object
      required:
        - status
        - error
      properties:
        status:
          type: integer
          example: 500
        error:
          type: string
          example: "Internal Server Error"
        message:
          type: string
          example: "An unexpected error occurred"
        timestamp:
          type: string
          format: date-time
```

---

## Notas de implementación

- El controller vive en `ar.com.marete.apivscodev2.controller.HelloController`
- La respuesta se modela como un Java record: `HelloResponse(String message)`
- El path prefix `/api/v1` se configura a nivel de controller con `@RequestMapping`
