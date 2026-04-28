# API de Salas de Reunião — FIAP CP02

**Nome:** Matheus Rivera Montovaneli  
**RM:** 555499

---

## Descrição

API REST para gerenciamento de salas de reunião e reservas, construída com Spring Boot seguindo arquitetura SOA (Service-Oriented Architecture) com separação em camadas (Controller → Service → Repository).

---

## Tecnologias

- Java 17+ (testado com Java 26)
- Spring Boot 3.2.5
- Spring Security + JWT (jjwt 0.12.5)
- Spring Data JPA + H2 (banco em memória)
- Spring Cache (ConcurrentMapCache)
- SpringDoc OpenAPI 2.5 (Swagger UI)
- JUnit 5 + Mockito

---

## Diferenciais implementados

| Diferencial | Detalhe |
|---|---|
| ✅ Cache | `@Cacheable` / `@CacheEvict` em `SalaService` |
| ✅ Paginação | `Pageable` em todos os endpoints de listagem |
| ✅ Filtros de busca | Filtro por capacidade, localização, sala e período |
| ✅ Logging estruturado | SLF4J + padrão de log configurado em `application.properties` |
| ✅ Tratamento global de exceções | `GlobalExceptionHandler` com `@RestControllerAdvice` |

---

## Como executar

### Pré-requisitos

- Java 17+ instalado ([Download JDK](https://adoptium.net/))
- Maven 3.8+ instalado ([Download Maven](https://maven.apache.org/download.cgi))

### Opção 1 — Rodar direto com Maven

```bash
mvn spring-boot:run
```

### Opção 2 — Gerar e executar o JAR

```bash
mvn clean package -DskipTests
java -jar target/cp02-1.0.0.jar
```

A API ficará disponível em `http://localhost:8080`

> **Obs:** não é necessário instalar nenhum banco de dados. O H2 sobe em memória automaticamente junto com a aplicação.

---

## Endpoints disponíveis

### Autenticação

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/auth/login` | Gera token JWT |

**Credenciais de teste:**
- `admin` / `admin123`
- `user` / `user123`

---

### Salas de Reunião

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/salas` | Criar sala |
| GET | `/api/salas?page=0&size=10` | Listar salas (paginado) |
| GET | `/api/salas/filtro?capacidadeMinima=10` | Filtrar por capacidade |
| GET | `/api/salas/filtro?localizacao=Bloco` | Filtrar por localização |
| GET | `/api/salas/{id}` | Buscar por ID |
| PUT | `/api/salas/{id}` | Atualizar sala |
| DELETE | `/api/salas/{id}` | Remover sala |

---

### Reservas

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/reservas` | Criar reserva |
| GET | `/api/reservas?page=0&size=10` | Listar reservas (paginado) |
| GET | `/api/reservas/sala/{salaId}` | Listar reservas de uma sala específica |
| GET | `/api/reservas/filtro?salaId=1&inicio=...&fim=...` | Filtrar reservas por sala e/ou período |
| GET | `/api/reservas/{id}` | Buscar por ID |
| DELETE | `/api/reservas/{id}` | Cancelar reserva |

---

## Links úteis

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **H2 Console:** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:cp02db`
  - User: `sa` | Password: *(vazio)*

---

## Exemplos de requisições

### 1. Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer"
}
```

---

### 2. Criar Sala

```http
POST /api/salas
Authorization: Bearer <token>
Content-Type: application/json

{
  "nome": "Sala Inovação",
  "capacidade": 20,
  "localizacao": "Bloco A - 2º andar"
}
```

---

### 3. Criar Reserva

```http
POST /api/reservas
Authorization: Bearer <token>
Content-Type: application/json

{
  "salaId": 1,
  "dataHoraInicio": "2026-05-01T09:00:00",
  "dataHoraFim": "2026-05-01T10:00:00",
  "responsavel": "João Silva"
}
```

---

### 4. Filtrar Reservas por Período

```http
GET /api/reservas/filtro?inicio=2026-05-01T00:00:00&fim=2026-05-31T23:59:59
Authorization: Bearer <token>
```

---

### 5. Conflito de Reserva (retorna 409)

```http
POST /api/reservas
Authorization: Bearer <token>
Content-Type: application/json

{
  "salaId": 1,
  "dataHoraInicio": "2026-05-01T09:30:00",
  "dataHoraFim": "2026-05-01T10:30:00",
  "responsavel": "Maria Santos"
}
```

**Resposta 409 Conflict:**
```json
{
  "timestamp": "2026-04-27T10:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Conflito de reserva: a sala já está ocupada no intervalo solicitado"
}
```

---

## Regras de negócio aplicadas

- Reservas conflitantes são bloqueadas (mesma sala, intervalo sobreposto) → `409 Conflict`
- Data/hora de início deve ser futura
- Data/hora de fim deve ser posterior ao início
- Duração mínima de **15 minutos** por reserva
- Todos os endpoints protegidos por JWT (exceto `/api/auth/login`)

---

## Dados de exemplo (seed)

A aplicação inicia com dados pré-carregados para facilitar os testes:

| Sala | Capacidade | Localização |
|------|-----------|-------------|
| Sala Inovação | 20 pessoas | Bloco A - 2º andar |
| Sala Executiva | 8 pessoas | Bloco B - 1º andar |
| Auditório | 50 pessoas | Bloco C - Térreo |

Duas reservas de exemplo também são carregadas (sala 1 e sala 2 em 02/05/2026).

---

## Arquitetura (SOA)

```
Controller  →  DTO (request/response)
    ↓
Service     →  Regras de negócio, validações, cache
    ↓
Repository  →  JPA / H2
    ↓
Entity      →  Mapeamento JPA
```
