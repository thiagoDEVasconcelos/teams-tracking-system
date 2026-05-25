# Teams Tracking System

Sistema de rastreamento de equipes externas em tempo real, desenvolvido como desafio técnico fullstack com Spring Boot e Next.js.

---

## Tecnologias Utilizadas

### Backend
- Java 17
- Spring Boot 3.3.5
- Spring Data JPA
- Spring WebFlux (WebClient)
- Resilience4j (CircuitBreaker)
- MySQL 8
- Flyway (migrations)
- Springdoc OpenAPI (Swagger)

### Frontend
- Next.js 16
- TypeScript
- Tailwind CSS
- shadcn/ui
- TanStack Query (React Query)
- React Hook Form + Zod
- Leaflet / React Leaflet

---

## Funcionalidades Implementadas

### Obrigatórias
- CRUD completo de agentes de campo
- Sincronização automática via 4 schedulers independentes
- Rastreamento geográfico em tempo real
- Histórico completo de rota do dia por agente
- Registro de check-ins manuais e automáticos
- Cálculo de distância entre check-ins com fórmula Haversine
- Tratamento de idempotência via upsert por externalId
- Painel de monitoramento de sincronização

### Diferenciais
- Mapa interativo com Leaflet
- Geofencing visual (polígonos e círculos no mapa)
- Swagger/OpenAPI em `/swagger-ui/index.html`
- Tratamento de erros 429 (Rate Limiting) e 503 (Instabilidade) com retry e backoff exponencial com jitter
- CircuitBreaker com fallback para proteger chamadas à API externa
- Atualizações em tempo real no frontend via Server-Sent Events (SSE)
- Testes unitários para services e utilitários de domínio

---

## Arquitetura

```text
teams-tracking-system/
├── backend/          # Spring Boot API REST
├── frontend/         # Next.js App Router
└── README.md         # Documentação principal do projeto
```

### Fluxo de sincronização

Schedulers (a cada X segundos)
↓
GpsApiClient (WebClient)
↓
API Externa (desafio-media.onrender.com)
↓
SyncService (upsert + regras de negócio)
↓
MySQL (persistência)
↓
Eventos SSE (/api/events)
↓
Frontend (TanStack Query invalidando caches em tempo real)

### Os 4 Schedulers

| Scheduler | Intervalo | Responsabilidade |
|---|---|---|
| AgentSyncScheduler | 5 minutos | Sincroniza cadastro de agentes via upsert por externalId |
| LocationSyncScheduler | 30 segundos | Atualiza posição atual e persiste histórico de localização |
| CheckInSyncScheduler | 1 minuto | Sincronização incremental de eventos via syncToken |
| GeofenceSyncScheduler | 10 minutos | Sincroniza cercas geográficas via upsert por externalId |

---

## Decisões Técnicas

### WebClient em vez de RestTemplate
O desafio exige WebClient. Utilizado WebClient com tratamento de erros 429 (rate limiting) lendo o header `Retry-After` e 503 (instabilidade simulada) com backoff exponencial e jitter de 50%, garantindo resiliência nas chamadas à API externa.

### CircuitBreaker com Resilience4j
As chamadas do `GpsApiClient` para agentes, localizações, check-ins e geofences usam `@CircuitBreaker(name = "gpsApi")` com fallbacks seguros. A configuração fica em `application.properties` e evita que instabilidades recorrentes da API externa derrubem o fluxo de sincronização.

### Atualização em tempo real com SSE
O backend expõe `/api/events` com Server-Sent Events. Quando agentes, localizações, check-ins, geofences ou logs de sincronização mudam, o frontend recebe o evento e invalida as queries correspondentes do TanStack Query.

### Flyway para migrations
Todas as alterações no banco são versionadas via Flyway, garantindo rastreabilidade e reprodutibilidade do schema em qualquer ambiente.

### syncToken incremental
O `CheckInSyncScheduler` persiste o `syncToken` retornado pela API após cada sincronização bem-sucedida. Nas execuções seguintes, envia o token para buscar apenas eventos novos, evitando reprocessamento.

### Idempotência
- Agentes e geofences: upsert pelo `externalId`
- Check-ins automáticos: verificação pelo `externalEventId` antes de salvar
- Localizações: filtro de acurácia GPS — leituras acima de 50 metros são descartadas

### Haversine
Cálculo de distância em metros entre check-ins consecutivos do mesmo agente, implementado em `HaversineUtil` e aplicado tanto em check-ins manuais quanto automáticos.

### Separação de responsabilidades
- `GpsApiClient` — comunicação com API externa
- `SyncService` — orquestração da sincronização
- `Schedulers` — agendamento, delegam para SyncService
- `RealtimeEventService` — publicação de eventos SSE para o frontend
- `Controllers` — exposição dos endpoints REST

### Testes unitários
O projeto possui testes com JUnit 5 e Mockito cobrindo regras de service e utilitários de domínio:

- `AgentServiceTest` — listagem, criação, remoção e exceção para agente inexistente
- `SyncServiceTest` — sincronização de agentes e comportamento quando não há dados externos
- `HaversineUtilTest` — cálculo de distância, mesmo ponto e distância positiva

Para executar:

```bash
cd backend
./mvnw test
```

---

## Pré-requisitos

- Docker e Docker Compose (para Opção 1)
- Java 17+, Maven 3.8+ e MySQL 8+ (para Opção 2)
- Node.js 18+ (para Opção 2)

---

## Como Rodar A Aplicação

### Opção 1: Via Docker Compose (Recomendado)
A aplicação está totalmente dockerizada. Certifique-se de ter o Docker instalado e execute:

Clone o repositório e entre na pasta do projeto:

```bash
cd teams-tracking-system
```

Suba todos os serviços (Banco, Backend e Frontend) de forma integrada:

```bash
# Se seu usuário estiver no grupo do docker:
docker compose up --build

# Se precisar de permissões de administrador:
sudo docker compose up --build
```

Serviços disponíveis:

- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui/index.html
- MySQL: localhost:3306

O Compose já configura o banco, as credenciais e a URL pública da API usada pelo frontend (`NEXT_PUBLIC_API_BASE_URL=http://localhost:8080`).

### Opção 2: Inicialização Manual (Local)

1. Banco de dados
Crie o schema e o usuário no seu MySQL local:

```sql
CREATE DATABASE teams_tracking;
CREATE USER 'tracker'@'localhost' IDENTIFIED BY 'tracker123';
GRANT ALL PRIVILEGES ON teams_tracking.* TO 'tracker'@'localhost';
FLUSH PRIVILEGES;
```

2. Backend

```bash
cd backend
mvn spring-boot:run
```

O Flyway criará as tabelas automaticamente na primeira execução.

Backend disponível em: http://localhost:8080

Swagger disponível em: http://localhost:8080/swagger-ui/index.html

3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend disponível em: http://localhost:3000

---

## Endpoints principais

| Método | Endpoint | Descrição |
|---|---|---|
| GET | /api/agents | Lista todos os agentes |
| POST | /api/agents | Cria novo agente |
| PUT | /api/agents/{id} | Atualiza agente |
| DELETE | /api/agents/{id} | Remove agente |
| GET | /api/check-ins | Lista check-ins |
| POST | /api/check-ins | Registra check-in manual |
| GET | /api/locations/agent/{id}/route | Rota do dia do agente |
| GET | /api/geofences | Lista geofences |
| POST | /api/sync/agents | Sincroniza agentes manualmente |
| POST | /api/sync/locations | Sincroniza localizações manualmente |
| POST | /api/sync/check-ins | Sincroniza check-ins manualmente |
| POST | /api/sync/geofences | Sincroniza geofences manualmente |
| GET | /api/sync/logs | Histórico de sincronizações |
| GET | /api/events | Stream SSE de eventos em tempo real |

Documentação completa: `http://localhost:8080/swagger-ui/index.html`

---

## Telas do Sistema

| Tela | Rota | Descrição |
|---|---|---|
| Agentes | /agents | CRUD completo de agentes |
| Mapa | /map | Mapa em tempo real com geofencing visual |
| Check-ins | /check-ins | Listagem e registro manual |
| Sincronização | /sync | Monitoramento dos schedulers |
