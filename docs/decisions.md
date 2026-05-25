# Decisões Técnicas

## WebClient em vez de RestTemplate
Exigido pelo desafio. Permite chamadas reativas e integração com Resilience4j.

## Flyway para migrations
Garante rastreabilidade e reprodutibilidade do schema em qualquer ambiente.

## SyncToken incremental
O CheckInSyncScheduler persiste o syncToken após cada sync bem-sucedida, buscando apenas eventos novos nas execuções seguintes.

## Upsert por externalId
Agentes e geofences usam upsert pelo externalId para garantir idempotência — a mesma sincronização pode rodar várias vezes sem duplicar dados.

## Soft delete vs Hard delete
Optou-se por hard delete com cascade — ao deletar um agente, seus check-ins e histórico de localização são removidos junto, mantendo a integridade referencial.

## Haversine
Implementado em HaversineUtil para calcular a distância em metros entre check-ins consecutivos do mesmo agente.

## Separação frontend/backend
O frontend nunca acessa a API externa diretamente. Toda comunicação com a API externa é feita pelo backend, que expõe seus próprios endpoints REST para o frontend.