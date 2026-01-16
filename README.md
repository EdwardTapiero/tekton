# Challenge Backend - API REST Spring Boot

API REST desarrollada en Spring Boot para cálculo con porcentaje dinámico, caché con Caffeine e historial de llamadas asíncrono.

## 🚀 Características

- **Cálculo dinámico**: Suma de dos números con porcentaje obtenido de servicio externo
- **Caché inteligente**: Almacenamiento en memoria (Caffeine) con TTL de 30 minutos y fallback automático
- **Historial asíncrono**: Registro de todas las llamadas API sin afectar el rendimiento
- **Documentación**: Swagger/OpenAPI integrado
- **Docker**: Despliegue completo con docker-compose

## 📋 Requisitos Previos

- Java 21 (JDK)
- Maven 3.9+
- Docker y Docker Compose
- PostgreSQL (si ejecutas sin Docker)

## 🏗️ Arquitectura

```
com.tekton.backend/
├── config/          # Configuraciones (Caffeine, Async, Swagger)
├── controller/      # REST Controllers
├── service/         # Lógica de negocio
├── repository/      # JPA Repositories
├── entity/          # Entidades JPA
├── dto/             # Data Transfer Objects
├── exception/       # Excepciones y handlers
├── aspect/          # AOP para historial
└── util/            # Utilidades
```

## 🐳 Despliegue con Docker (Recomendado)

### Prerrequisitos
- Docker Desktop instalado y ejecutándose
- Mac M3 compatible (ARM64) o cualquier plataforma con Docker

### Pasos de Despliegue

1. **Clonar el repositorio** (si aplica):
```bash
git clone <repository-url>
cd tekton
```

2. **Construir y levantar los servicios**:
```bash
docker-compose up --build
```

3. **Acceder a la aplicación**:
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

### Comandos Útiles

```bash
# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down

# Detener y limpiar volúmenes
docker-compose down -v

# Ejecutar en segundo plano
docker-compose up -d
```

## 💻 Desarrollo Local (Sin Docker)

### 1. Configurar Base de Datos PostgreSQL

```bash
# Con Docker (solo PostgreSQL)
docker run --name tekton-postgres \
  -e POSTGRES_DB=tekton_db \
  -e POSTGRES_USER=tekton_user \
  -e POSTGRES_PASSWORD=tekton_password \
  -p 5432:5432 \
  -d postgres:16-alpine
```

### 2. Configurar Variables de Entorno

Editar `src/main/resources/application.yml` o crear `application-local.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tekton_db
    username: tekton_user
    password: tekton_password
```

### 3. Compilar y Ejecutar

```bash
# Compilar
mvn clean package

# Ejecutar
mvn spring-boot:run

# O ejecutar el JAR
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

## 📡 Endpoints de la API

### 1. Calcular con Porcentaje Dinámico

```http
POST /api/calculate
Content-Type: application/json

{
  "num1": 10.5,
  "num2": 20.3
}
```

**Respuesta exitosa (200 OK):**
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

**Respuesta de error (400 Bad Request) - Validación:**
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

**Respuesta de error (503 Service Unavailable) - Servicio externo no disponible:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 503,
  "error": "Service Unavailable",
  "message": "No se pudo obtener el porcentaje. El servicio externo no está disponible y no hay valor en caché."
}
```

### 2. Obtener Historial de Llamadas

```http
GET /api/history?page=0&size=10
```

**Parámetros de consulta:**
- `page`: Número de página (default: 0)
- `size`: Tamaño de página (default: 10)
- `endpoint`: Filtrar por endpoint (opcional, ej: `/api/calculate`)
- `startDate`: Fecha inicio (opcional, formato: yyyy-MM-dd)
- `endDate`: Fecha fin (opcional, formato: yyyy-MM-dd)
- `sortBy`: Campo para ordenar (default: `timestamp`)
- `sortDir`: Dirección del orden - `ASC` o `DESC` (default: `DESC`)

**Ejemplos de uso:**
```bash
# Obtener todas las llamadas
GET /api/history

# Filtrar por endpoint
GET /api/history?endpoint=/api/calculate

# Filtrar por rango de fechas
GET /api/history?startDate=2024-01-15&endDate=2024-01-16

# Paginación
GET /api/history?page=0&size=20

# Combinar filtros
GET /api/history?endpoint=/api/calculate&startDate=2024-01-15&page=0&size=10
```

**Respuesta:**
```json
{
  "content": [
    {
      "id": 1,
      "timestamp": "2024-01-15T10:30:00Z",
      "endpoint": "/api/calculate",
      "method": "POST",
      "requestBody": "{\"num1\":10.5,\"num2\":20.3}",
      "responseBody": "{\"result\":34.08}",
      "statusCode": 200,
      "executionTimeMs": 45
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

## 🔧 Configuración

### Caché de Porcentaje
- **TTL**: 30 minutos
- **Estrategia**: Cache-aside pattern
- **Fallback**: Si el servicio externo falla, se usa el último valor en caché

### Servicio Externo
- **URL**: Configurable en `application.yml` (default: `http://localhost:8081/api/percentage`)
- **Timeout**: 5000ms
- **Comportamiento**: 
  - Si el servicio está disponible, obtiene el porcentaje y lo almacena en caché
  - Si el servicio falla, usa el último valor almacenado en caché (válido 30 minutos)
  - Si no hay caché disponible, retorna error 503

### Historial Asíncrono
- **Ejecución**: Thread pool dedicado
- **Registro**: Automático mediante AOP
- **Datos capturados**: Endpoint, método, parámetros, respuesta, tiempo de ejecución

## 🧪 Testing

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests de integración (requiere Docker para Testcontainers)
mvn verify

# Ejecutar un test específico
mvn test -Dtest=CalculationServiceTest
```

### Cobertura de Tests

El proyecto incluye tests para:
- ✅ Servicios (CalculationService, PercentageCacheService, ExternalPercentageService)
- ✅ Controllers (CalculationController)
- ✅ Integración end-to-end con Testcontainers
- ✅ Manejo de errores y validaciones
- ✅ Fallback del caché cuando el servicio externo falla

## 📚 Documentación API

Una vez que la aplicación esté corriendo, accede a:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 🛠️ Tecnologías Utilizadas

- **Spring Boot 4.0.1**
- **Java 21**
- **PostgreSQL 16**
- **Caffeine Cache 3.1.8**
- **Spring AOP** (para historial asíncrono)
- **Swagger/OpenAPI** (documentación)
- **Lombok** (reducción de boilerplate)
- **Testcontainers** (tests de integración)
- **Docker & Docker Compose**

## 📝 Notas de Desarrollo

### Mac M3 (ARM64)
El proyecto está optimizado para Mac con procesador M3 (ARM64).

### Variables de Entorno en Docker
Las siguientes variables se pueden sobrescribir en `docker-compose.yml`:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `EXTERNAL_SERVICE_PERCENTAGE_URL` (URL del servicio externo)
- `EXTERNAL_SERVICE_PERCENTAGE_TIMEOUT` (Timeout en ms)

### Configuración del Servicio Externo

Para probar con un servicio externo mock (por ejemplo, SoapUI):

1. Configurar el servicio mock en `http://localhost:8081/api/percentage`
2. El servicio debe retornar un `Double` como respuesta
3. Si el servicio no está disponible, la aplicación usará el caché automáticamente

## 🤝 Contribuir

1. Crear una rama desde `develop`
2. Realizar cambios
3. Crear Pull Request hacia `develop`

## 📄 Licencia

Este proyecto es parte de un challenge técnico.

---

**Desarrollado con ❤️ usando Spring Boot**
