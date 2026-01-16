# Guía de Verificación - Challenge Backend

## 🚀 Pasos para Verificar que Todo Funciona

### 1. Levantar la Aplicación

#### Opción A: Con Docker (Recomendado)

```bash
# Construir y levantar
docker-compose up --build

# O en segundo plano
docker-compose up -d

# Ver logs
docker-compose logs -f backend
```

#### Opción B: Localmente

```bash
# Asegúrate de tener PostgreSQL corriendo
# Luego ejecuta:
mvn spring-boot:run
```

### 2. Verificar que la Aplicación Está Corriendo

```bash
# Verificar que Swagger/OpenAPI responde
curl http://localhost:8080/api-docs

# O verificar que el endpoint de historial responde (aunque esté vacío)
curl http://localhost:8080/api/history?page=0&size=10
```

**Si obtienes una respuesta JSON, la aplicación está corriendo correctamente.**


### 3. Probar el Endpoint de Cálculo

#### Test 1: Cálculo Exitoso

```bash
curl -X POST http://localhost:8080/api/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "num1": 10.5,
    "num2": 20.3
  }'
```

**Respuesta esperada (200 OK):**
```json
{
  "result": 34.08,
  "num1": 10.5,
  "num2": 20.3,
  "sum": 30.8,
  "percentage": 10.5,
  "timestamp": "2024-01-15T10:30:00"
}
```

#### Test 2: Validación de Errores

```bash
# Sin num1 (debe fallar con 400)
curl -X POST http://localhost:8080/api/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "num2": 20.3
  }'
```

**Respuesta esperada (400 Bad Request):**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Error de validación en los datos de entrada",
  "details": {
    "num1": "num1 es requerido"
  }
}
```

#### Test 3: Servicio Externo No Disponible

Si el servicio externo no está disponible y no hay caché:

**Respuesta esperada (503 Service Unavailable):**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 503,
  "error": "Service Unavailable",
  "message": "No se pudo obtener el porcentaje. El servicio externo no está disponible y no hay valor en caché."
}
```

### 4. Verificar el Historial

#### Test 1: Obtener Historial Completo

```bash
curl http://localhost:8080/api/history?page=0&size=10
```

**Respuesta esperada:**
```json
{
  "content": [
    {
      "id": 1,
      "timestamp": "2024-01-15T10:30:00",
      "endpoint": "/api/calculate",
      "method": "POST",
      "requestBody": "{\"num1\":10.5,\"num2\":20.3}",
      "responseBody": "{\"result\":34.08,...}",
      "statusCode": 200,
      "executionTimeMs": 45,
      "errorMessage": null
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

#### Test 2: Filtrar por Endpoint

```bash
curl "http://localhost:8080/api/history?endpoint=/api/calculate&page=0&size=10"
```

#### Test 3: Filtrar por Fechas

```bash
curl "http://localhost:8080/api/history?startDate=2024-01-15&endDate=2024-01-16&page=0&size=10"
```

### 5. Verificar Swagger UI

1. Abre en el navegador: http://localhost:8080/swagger-ui.html
2. Deberías ver:
   - **Calculation** tag con el endpoint `POST /api/calculate`
   - **History** tag con el endpoint `GET /api/history`
3. Prueba los endpoints directamente desde Swagger

### 6. Verificar el Caché

#### Test 1: Primera Llamada (Obtiene del Servicio Externo)

```bash
# Primera llamada - obtiene del servicio externo
curl -X POST http://localhost:8080/api/calculate \
  -H "Content-Type: application/json" \
  -d '{"num1": 10, "num2": 20}'
```

**En los logs deberías ver:**
```
Porcentaje obtenido del servicio externo y almacenado en caché: 10.5
```

#### Test 2: Desactivar el Servicio Mock y Hacer Segunda Llamada

1. Desactiva o cambia la URL del servicio mock a una inválida
2. Reinicia la aplicación
3. Haz otra llamada:

```bash
curl -X POST http://localhost:8080/api/calculate \
  -H "Content-Type: application/json" \
  -d '{"num1": 5, "num2": 15}'
```

**En los logs deberías ver:**
```
Error al obtener porcentaje del servicio externo: ...
Usando porcentaje desde caché: 10.5
```

**La respuesta debería usar el porcentaje del caché (10.5) aunque el servicio esté caído.**