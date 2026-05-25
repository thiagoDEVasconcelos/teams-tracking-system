# Arquitetura do Sistema

## Visão Geral

O sistema é composto por três camadas principais:

- **Frontend** (Next.js) — interface do usuário
- **Backend** (Spring Boot) — API REST e schedulers
- **Banco de dados** (MySQL) — persistência dos dados

## Fluxo de Sincronização

A sincronização com a API externa é feita exclusivamente pelo backend através de 4 schedulers independentes. O frontend nunca acessa a API externa diretamente.

API Externa (desafio-media.onrender.com)
↑
GpsApiClient (WebClient)
↑
SyncService
↑
Schedulers (AgentSync, LocationSync, CheckInSync, GeofenceSync)
↑
MySQL (persistência)
↑
Controllers (REST API)
↑
Frontend (TanStack Query)

## Resiliência

Três camadas de proteção contra falhas da API externa:

1. Retry com backoff exponencial e jitter (Reactor)
2. Tratamento de rate limiting via header Retry-After
3. Circuit Breaker (Resilience4j) — abre após 50% de falhas

## Banco de Dados

O schema é gerenciado pelo Flyway com migrations versionadas. Cada alteração tem seu próprio arquivo SQL numerado sequencialmente.