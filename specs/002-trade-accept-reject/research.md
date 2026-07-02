# Research: Evolução 1 — Aceitar e Recusar Trocas

**Feature**: `002-trade-accept-reject`  
**Date**: 2026-06-26  
**Spec**: [spec.md](./spec.md)

## R1 — Transação lógica no aceite de troca

**Decision**: Orquestrar aceite em `TradeService.acceptProposal()` com sequência fixa: (1) carregar proposta; (2) validar autorização e status; (3) validar posse e regras de justiça; (4) aplicar transferência nas coleções; (5) persistir proposta com status `ACCEPTED`. Usar `@Transactional` no caso de uso (`AcceptTradeUseCase`) para garantir rollback em falha de persistência.

**Rationale**:
- Atende FR-006, FR-008–FR-009, FR-011–FR-016 e requisito do usuário de troca transacional.
- Validações ocorrem antes de qualquer mutação de coleção; status só muda após sucesso das transferências.
- Falha de validação não altera status (permanece `PROPOSED`) nem coleções.
- `@Transactional` na camada de aplicação é padrão Spring Boot sem poluir o domínio com anotações de framework.

**Alternatives considered**:
- **Transação manual sem Spring**: mais didático, porém duplica mecanismo já disponível e aumenta risco de inconsistência em falha de I/O.
- **Atualizar status antes das coleções**: viola requisito explícito de só marcar `ACCEPTED` após sucesso na atualização.

## R2 — Transferência de figurinhas entre coleções

**Decision**: Estender `UserCollection` com `removeSticker(number)` (decrementa 1; remove entrada se quantidade chegar a 0) e `CollectionService` com `removeSticker`, `getQuantity`, `hasDuplicate` (qty > 1). Método privado `transferSticker(fromUser, toUser, number)` em `CollectionService` ou lógica inline em `TradeService` que chama remove + add.

**Rationale**:
- Reutiliza `addSticker` existente para incremento.
- `getQuantity` exposto no serviço evita que `TradeService` acesse repositório de coleção diretamente.
- `hasDuplicate` encapsula regra de figurinha repetida para proteção opcional de figurinha única (FR-014, FR-015).
- Mantém regras no domínio conforme Princípio IV.

**Alternatives considered**:
- **`transfer()` atômico único**: mais elegante, mas adiciona método composto antes de ser necessário; aceite chama 4 operações simples (2 removes + 2 adds) de forma sequencial dentro da mesma transação.
- **Alterar quantidade via SQL direto**: viola camadas e dificulta testes unitários.

## R3 — Regra opcional de figurinha única

**Decision**: Propriedade `album.trade.protect-single-sticker` (padrão **`false`**) em `AlbumProperties` ou bean `TradeRules` injetado em `TradeService`. Quando `true`, rejeitar aceite se solicitante tiver qty = 1 da oferecida **ou** destinatário qty = 1 da desejada.

**Rationale**:
- Assumption da spec: padrão desativado preserva compatibilidade com propostas MVP.
- Configuração centralizada permite demonstrar regra no treinamento sem alterar código.
- Validação apenas no aceite, não na criação (FR-018 preservado).

**Alternatives considered**:
- **Sempre ativar regra**: quebraria propostas MVP com figurinha qty 1.
- **Flag por proposta**: complexidade desnecessária fora do escopo.

## R4 — Novos endpoints REST (compatibilidade MVP)

**Decision**: Adicionar endpoints **sem alterar** contratos existentes:
- `POST /api/trades/{tradeId}/accept` — body `{ targetUserId }`
- `POST /api/trades/{tradeId}/reject` — body `{ targetUserId }`
- `GET /api/trades/{tradeId}` — consulta status

Manter `POST/GET /api/trades` inalterados em comportamento.

**Rationale**:
- Princípio VI: zero breaking change nos 7 endpoints MVP.
- RESTful: ação sobre recurso identificado.
- `targetUserId` no body identifica quem executa a ação (modelo didático sem auth).

**Alternatives considered**:
- **PATCH /api/trades/{id} com status**: menos explícito para aceite/recusa; mistura semântica.
- **Query param apenas**: body JSON alinha com demais endpoints e OpenAPI.

## R5 — Exceções de domínio e HTTP

**Decision**: Novas subclasses de `DomainException`:

| Exceção | Código | HTTP | Cenário |
|---------|--------|------|---------|
| `TradeNotFoundException` | `TRADE_NOT_FOUND` | 404 | ID inexistente |
| `TradeAlreadyFinalizedException` | `TRADE_ALREADY_FINALIZED` | 409 | Status ACCEPTED/REJECTED |
| `UnauthorizedTradeActionException` | `UNAUTHORIZED_TRADE_ACTION` | 403 | Não é o destinatário |
| `UnfairTradeException` | `UNFAIR_TRADE` | 400 | Mesmo número oferecido/desejado |
| `SingleStickerProtectionException` | `SINGLE_STICKER_PROTECTED` | 400 | Regra opcional ativa |
| `StickerNotOwnedException` | (existente) | 400 | Posse insuficiente no aceite |

**Rationale**:
- Mensagens em português (SC-002, FR-024, FR-026).
- 409 para conflito de estado finalizado distingue de validação de posse (400).
- Reutiliza `StickerNotOwnedException` já mapeada no `GlobalExceptionHandler`.

**Alternatives considered**:
- **Uma exceção genérica `TradeValidationException`**: simplifica handler, mas perde clareza nos testes e códigos de erro.

## R6 — Evolução do frontend

**Decision**: Estender `TradesPage` com abas ou seções: (1) **Enviadas** — filtro `requesterUserId=activeUser`; (2) **Recebidas** — filtro `targetUserId=activeUser` com botões Aceitar/Recusar para `PROPOSED`. Badges visuais por status. Ações desabilitadas para propostas finalizadas.

**Rationale**:
- Atende FR-020–FR-023 e Princípio VIII com diff mínimo.
- Reutiliza `listTrades` com query params já existentes no MVP.
- Novos métodos em `apiClient.ts`: `acceptTrade`, `rejectTrade`, `getTrade`.

**Alternatives considered**:
- **Página separada `ReceivedTradesPage`**: mais arquivos; seções na mesma página mantém fluxo didático coeso.

## R7 — Estratégia de testes

**Decision**: Expandir `TradeServiceTest` e `CollectionServiceTest` com cenários listados na spec; adicionar `AcceptTradeUseCase`/`RejectTradeUseCase` cobertos via `TradeControllerTest` e fluxo E2E em `AlbumXFlowTest`.

**Rationale**:
- Princípio VII: cada regra material com teste unitário.
- MockMvc valida mapeamento HTTP ↔ exceções.
- Fluxo integrado: MVP + aceite + verificação de coleções.

**Alternatives considered**:
- **Apenas testes de integração**: mais lentos e menos precisos para isolar regras de domínio.

## R8 — Repositório de propostas

**Decision**: Adicionar `findById(UUID id)` e `save` já existente em `TradeProposalRepository`; sem alterar assinatura de `findAll`.

**Rationale**:
- Porta mínima para aceite/recusa/consulta.
- Adapter JPA implementa `Optional<TradeProposal> findById(UUID id)`.

**Alternatives considered**:
- **Retornar entidade mutável com `accept()` interno**: encapsula transição, mas o MVP usa records/immutability parcial; manter serviço como orquestrador é consistente com `createProposal`.
