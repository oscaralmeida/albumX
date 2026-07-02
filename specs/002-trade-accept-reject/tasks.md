---
description: "Task list for Evolução 1 — Aceitar e Recusar Trocas"
---

# Tasks: Evolução 1 — Aceitar e Recusar Trocas

**Input**: Design documents from `/specs/002-trade-accept-reject/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Testes unitários de domínio e testes de integração MockMvc são OBRIGATÓRIOS (Constituição AlbumX, Princípio VII; plan.md).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Backend**: `src/main/java/com/albumx/` at repository root
- **Tests**: `src/test/java/com/albumx/`
- **Frontend**: `frontend/src/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Configuração da Evolução 1 sobre o MVP entregue — sem alterar comportamento existente

- [X] T001 Verify MVP (001-sticker-trade-mvp) is delivered and work on branch `002-trade-accept-reject`
- [X] T002 [P] Add `album.trade.protect-single-sticker: false` to `src/main/resources/application.yml` and `src/main/resources/application-docker.yml`
- [X] T003 [P] Extend `AlbumProperties` with nested `TradeProperties` (`protectSingleSticker`, default `false`) in `src/main/java/com/albumx/infrastructure/config/AlbumProperties.java`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Mutação de coleção, exceções de domínio, repositório e modelo de status — **BLOQUEIA** todas as user stories

**⚠️ CRITICAL**: Nenhuma user story pode começar antes desta fase

### 1. Modelo de status da troca

- [X] T004 Verify `TradeStatus` enum contains `PROPOSED`, `ACCEPTED`, `REJECTED` in `src/main/java/com/albumx/domain/model/TradeStatus.java`
- [X] T005 Add status transition methods on `TradeProposal` (e.g. `accept()`, `reject()`) in `src/main/java/com/albumx/domain/model/TradeProposal.java`
- [X] T006 Verify `TradeService.createProposal` still assigns `TradeStatus.PROPOSED` in `src/main/java/com/albumx/domain/service/TradeService.java`

### 2. Expandir CollectionService (domínio)

- [X] T007 Implement `UserCollection.removeSticker(int stickerNumber)` — decrementa qty, remove entrada se qty=0, falha se qty=0 — in `src/main/java/com/albumx/domain/model/UserCollection.java`
- [X] T008 Implement `CollectionService.removeSticker(UUID userId, int stickerNumber)` delegating to `UserCollection` and persisting in `src/main/java/com/albumx/domain/service/CollectionService.java`
- [X] T009 Implement `CollectionService.getQuantity(UUID userId, int stickerNumber)` in `src/main/java/com/albumx/domain/service/CollectionService.java`
- [X] T010 Implement `CollectionService.hasDuplicate(UUID userId, int stickerNumber)` (qty > 1) in `src/main/java/com/albumx/domain/service/CollectionService.java`
- [X] T011 [P] Add `removeSticker` tests (success, remove entry when qty→0, reject when qty=0) to `src/test/java/com/albumx/domain/service/CollectionServiceTest.java`
- [X] T012 [P] Add `getQuantity` and `hasDuplicate` tests to `src/test/java/com/albumx/domain/service/CollectionServiceTest.java`

### Infraestrutura compartilhada

- [X] T013 [P] Create `TradeNotFoundException` in `src/main/java/com/albumx/domain/exception/TradeNotFoundException.java`
- [X] T014 [P] Create `TradeAlreadyFinalizedException` in `src/main/java/com/albumx/domain/exception/TradeAlreadyFinalizedException.java`
- [X] T015 [P] Create `UnauthorizedTradeActionException` in `src/main/java/com/albumx/domain/exception/UnauthorizedTradeActionException.java`
- [X] T016 [P] Create `UnfairTradeException` in `src/main/java/com/albumx/domain/exception/UnfairTradeException.java`
- [X] T017 [P] Create `SingleStickerProtectionException` in `src/main/java/com/albumx/domain/exception/SingleStickerProtectionException.java`
- [X] T018 Add `findById(UUID id)` to `TradeProposalRepository` port in `src/main/java/com/albumx/domain/repository/TradeProposalRepository.java`
- [X] T019 Implement `findById` in `TradeProposalJpaRepository.java` and `TradeProposalRepositoryAdapter.java`
- [X] T020 Wire `protectSingleSticker` from `AlbumProperties` into `TradeService` bean in `src/main/java/com/albumx/infrastructure/config/DomainConfig.java`

**Checkpoint**: Foundation ready — `mvn test` verde; MVP endpoints inalterados; nenhum endpoint novo ainda

---

## Phase 3: User Story 1 — Aceitar proposta de troca válida (Priority: P1) 🎯 MVP

**Goal**: Destinatário aceita proposta PROPOSED; status → ACCEPTED; coleções de solicitante e destinatário atualizadas corretamente

**Independent Test**: Dois usuários com coleções compatíveis e proposta PROPOSED; aceitar como destinatário → status ACCEPTED e quantidades corretas nas duas coleções

### Tests for User Story 1 ⚠️

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [X] T021 [P] [US1] Add `shouldAcceptValidProposal` test (status ACCEPTED) to `src/test/java/com/albumx/domain/service/TradeServiceTest.java`
- [X] T022 [P] [US1] Add `shouldUpdateRequesterCollectionOnAccept` test (remove offered, add requested) to `TradeServiceTest.java`
- [X] T023 [P] [US1] Add `shouldUpdateTargetCollectionOnAccept` test (remove requested, add offered) to `TradeServiceTest.java`
- [X] T024 [P] [US1] Add accept happy-path MockMvc test (`POST /api/trades/{id}/accept` → 200 ACCEPTED) to `src/test/java/com/albumx/infrastructure/web/TradeControllerTest.java`

### Implementation for User Story 1

- [X] T025 [US1] Implement `TradeService.getProposal(UUID tradeId)` in `src/main/java/com/albumx/domain/service/TradeService.java`
- [X] T026 [US1] Implement `TradeService.acceptProposal(UUID tradeId, UUID targetUserId)` — validações V-07/V-09/V-10/V-11/V-12, 4 mutações de coleção, status ACCEPTED — in `TradeService.java`
- [X] T027 [P] [US1] Create `TradeActionRequest` DTO (`targetUserId`) in `src/main/java/com/albumx/application/dto/TradeActionRequest.java`
- [X] T028 [US1] Create `AcceptTradeUseCase` with `@Transactional` in `src/main/java/com/albumx/application/usecase/AcceptTradeUseCase.java`
- [X] T029 [US1] Create `GetTradeUseCase` in `src/main/java/com/albumx/application/usecase/GetTradeUseCase.java`
- [X] T030 [US1] Add `GET /api/trades/{tradeId}` endpoint in `src/main/java/com/albumx/infrastructure/web/TradeController.java`
- [X] T031 [US1] Add `POST /api/trades/{tradeId}/accept` endpoint in `TradeController.java`
- [X] T032 [US1] Register `AcceptTradeUseCase` and `GetTradeUseCase` beans in `src/main/java/com/albumx/infrastructure/config/DomainConfig.java`

**Checkpoint**: User Story 1 fully functional — aceitar troca válida via API com coleções atualizadas

---

## Phase 4: User Story 2 — Recusar proposta de troca (Priority: P2)

**Goal**: Destinatário recusa proposta PROPOSED; status → REJECTED; coleções inalteradas

**Independent Test**: Proposta PROPOSED recusada como destinatário → status REJECTED; quantidades de ambos os usuários permanecem iguais

### Tests for User Story 2 ⚠️

- [X] T033 [P] [US2] Add `shouldRejectValidProposal` test (status REJECTED) to `src/test/java/com/albumx/domain/service/TradeServiceTest.java`
- [X] T034 [P] [US2] Add `shouldNotMutateCollectionsOnReject` test (zero chamadas remove/add) to `TradeServiceTest.java`
- [X] T035 [P] [US2] Add reject happy-path MockMvc test (`POST /api/trades/{id}/reject` → 200 REJECTED) to `src/test/java/com/albumx/infrastructure/web/TradeControllerTest.java`

### Implementation for User Story 2

- [X] T036 [US2] Implement `TradeService.rejectProposal(UUID tradeId, UUID targetUserId)` — validações V-07/V-08/V-09, status REJECTED, sem tocar coleções — in `TradeService.java`
- [X] T037 [US2] Create `RejectTradeUseCase` with `@Transactional` in `src/main/java/com/albumx/application/usecase/RejectTradeUseCase.java`
- [X] T038 [US2] Add `POST /api/trades/{tradeId}/reject` endpoint in `TradeController.java`
- [X] T039 [US2] Register `RejectTradeUseCase` bean in `DomainConfig.java`

**Checkpoint**: User Stories 1 AND 2 fully functional — aceitar e recusar via API

---

## Phase 5: User Story 3 — Validação de regras de negócio na efetivação (Priority: P3)

**Goal**: Impedir aceite/recusa inválidos; manter status PROPOSED e coleções inalteradas quando validação de aceite falhar

**Independent Test**: Tentar aceitar propostas inválidas (finalizada, sem posse, injusta, figurinha única com regra ativa) → erro claro; coleções e status PROPOSED preservados quando aplicável

### Tests for User Story 3 ⚠️

- [X] T040 [P] [US3] Add `shouldRejectAcceptWhenAlreadyAccepted` test to `TradeServiceTest.java`
- [X] T041 [P] [US3] Add `shouldRejectRejectWhenAlreadyAccepted` test to `TradeServiceTest.java`
- [X] T042 [P] [US3] Add `shouldRejectAcceptWhenAlreadyRejected` test to `TradeServiceTest.java`
- [X] T043 [P] [US3] Add `shouldRejectAcceptWhenRequesterLacksOfferedSticker` test (status permanece PROPOSED) to `TradeServiceTest.java`
- [X] T044 [P] [US3] Add `shouldRejectAcceptWhenTargetLacksRequestedSticker` test to `TradeServiceTest.java`
- [X] T045 [P] [US3] Add `shouldRejectUnfairTrade` test (mesmo número oferecido/desejado) to `TradeServiceTest.java`
- [X] T046 [P] [US3] Add `shouldRejectSingleStickerWhenProtectionEnabled` test to `TradeServiceTest.java`
- [X] T047 [P] [US3] Add `shouldAllowSingleStickerWhenProtectionDisabled` test to `TradeServiceTest.java`
- [X] T048 [P] [US3] Add accept by requester (não destinatário) returns 403 test to `TradeControllerTest.java`
- [X] T049 [P] [US3] Add accept finalized proposal returns 409 test to `TradeControllerTest.java`
- [X] T050 [P] [US3] Add GET trade not found returns 404 test to `TradeControllerTest.java`

### Implementation for User Story 3

- [X] T051 [US3] Ensure `acceptProposal` validates status PROPOSED before any mutation (V-08) in `TradeService.java`
- [X] T052 [US3] Ensure `acceptProposal` validates `targetUserId` equals proposal recipient (V-09) in `TradeService.java`
- [X] T053 [US3] Ensure `acceptProposal` validates offered ≠ requested — troca justa (V-12) in `TradeService.java`
- [X] T054 [US3] Implement single-sticker protection check (V-13) using `AlbumProperties.trade.protectSingleSticker` in `TradeService.java`
- [X] T055 [US3] Map `UnauthorizedTradeActionException` → HTTP 403 in `src/main/java/com/albumx/infrastructure/web/GlobalExceptionHandler.java`
- [X] T056 [US3] Map `TradeAlreadyFinalizedException` → HTTP 409 in `GlobalExceptionHandler.java`
- [X] T057 [US3] Map `TradeNotFoundException` → 404, `UnfairTradeException` and `SingleStickerProtectionException` → 400 in `GlobalExceptionHandler.java`

**Checkpoint**: Todas as validações V-07..V-13 cobertas; falha de validação não altera coleções nem status final

---

## Phase 6: User Story 4 — Visualizar e responder propostas na interface web (Priority: P4)

**Goal**: Listar propostas recebidas/enviadas com status visível; aceitar/recusar via browser com feedback de erro

**Independent Test**: Acessar página de trocas, selecionar usuário destinatário, ver propostas com status, aceitar ou recusar PROPOSED com atualização visual

### Implementation for User Story 4

- [X] T058 [P] [US4] Add `getTrade(tradeId)`, `acceptTrade(tradeId, targetUserId)`, `rejectTrade(tradeId, targetUserId)` to `frontend/src/api/apiClient.ts`
- [X] T059 [US4] Add **Propostas recebidas** section filtering `listTrades(undefined, activeUserId)` in `frontend/src/pages/TradesPage.tsx`
- [X] T060 [US4] Add **Propostas enviadas** read-only section filtering `listTrades(activeUserId)` in `TradesPage.tsx`
- [X] T061 [P] [US4] Create `TradeActions` component with Aceitar/Recusar buttons in `frontend/src/components/TradeActions.tsx`
- [X] T062 [US4] Add status badge CSS classes (`status-proposed`, `status-accepted`, `status-rejected`) in `frontend/src/App.css`
- [X] T063 [US4] Wire accept/reject handlers with `ErrorMessage`, loading/disabled state, success feedback, and list reload in `TradesPage.tsx`
- [X] T064 [US4] Show Aceitar/Recusar only when `status === 'PROPOSED'` and `activeUserId === trade.targetUserId` in `TradesPage.tsx`

**Checkpoint**: User Story 4 fully functional — fluxo aceitar/recusar via browser

---

## Phase 7: User Story 5 — Documentação interativa e compatibilidade com MVP (Priority: P5)

**Goal**: Swagger e quickstart atualizados; zero regressão no MVP; fluxo E2E completo verificável

**Independent Test**: Fluxo MVP (cadastro → coleção → repetidas → criar proposta) + aceite ou recusa via Swagger/browser; todos os testes existentes verdes

### Implementation for User Story 5

- [X] T065 [P] [US5] Verify `createProposal` still creates PROPOSED without mutating collections in `TradeServiceTest.java`
- [X] T066 [US5] Extend `AlbumXFlowTest.java` with MVP flow + accept trade + assert collection quantities for both users
- [X] T067 [US5] Run `mvn test` and confirm all MVP tests (`UserControllerTest`, `CollectionControllerTest`, create/list trade tests) pass without regression
- [X] T068 [US5] Verify collections mutate only after all accept validations pass (failed accept keeps PROPOSED, no collection change)
- [X] T069 [P] [US5] Verify springdoc/Swagger exposes `GET /api/trades/{id}`, `POST .../accept`, `POST .../reject` per `specs/002-trade-accept-reject/contracts/openapi.yaml`
- [X] T070 [US5] Execute validation scenarios from `specs/002-trade-accept-reject/quickstart.md` (browser + Swagger)
- [X] T071 [US5] Validate full accept/reject flow via UI (not API only) per SC-006 (< 7 min training flow)
- [X] T072 [US5] Confirm no Evolução 2 features were added (no suggestions, ranking, notifications, chat)

**Checkpoint**: Evolução 1 completa — MVP preservado, aceite/recusa demonstrável visualmente

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: No dependencies — start immediately
- **Phase 2 (Foundational)**: Depends on Phase 1 — **BLOCKS** all user stories
- **Phase 3 (US1 Accept)**: Depends on Phase 2
- **Phase 4 (US2 Reject)**: Depends on Phase 2; can start after US1 domain patterns established
- **Phase 5 (US3 Validation)**: Depends on US1 (acceptProposal exists); tests can be added incrementally
- **Phase 6 (US4 Frontend)**: Depends on US1 + US2 API endpoints (T030–T031, T038)
- **Phase 7 (US5 Polish)**: Depends on US1–US4 complete

### User Story Dependencies

| Story | Depende de | Entrega independente |
|-------|-----------|----------------------|
| US1 (P1) | Phase 2 | Aceitar troca válida + atualizar coleções |
| US2 (P2) | Phase 2 | Recusar troca sem alterar coleções |
| US3 (P3) | US1 (acceptProposal) | Validações e erros HTTP corretos |
| US4 (P4) | US1, US2 (API) | Aceitar/recusar via interface web |
| US5 (P5) | US1–US4 | Regressão MVP + quickstart + E2E |

### Within Each User Story

1. Domain unit tests written first (must FAIL before implementation)
2. Domain services before use cases
3. Use cases before controllers
4. MockMvc tests after controllers
5. Frontend after API endpoints stable

### Parallel Opportunities

- **Phase 1**: T002, T003
- **Phase 2**: T011+T012 (tests); T013–T017 (exceções em paralelo)
- **US1**: T021–T024 (tests em paralelo); T027 (DTO) paralelo a T025–T026
- **US2**: T033–T035 (tests em paralelo)
- **US3**: T040–T050 (todos os testes de validação em paralelo)
- **US4**: T058+T061 (apiClient + TradeActions em paralelo)
- **US5**: T065+T069 (regressão + Swagger em paralelo)

---

## Parallel Example: User Story 1

```bash
# Tests first (must fail):
Task T021: shouldAcceptValidProposal in TradeServiceTest.java
Task T022: shouldUpdateRequesterCollectionOnAccept
Task T023: shouldUpdateTargetCollectionOnAccept
Task T024: accept MockMvc test in TradeControllerTest.java

# Then implementation:
Task T025: getProposal in TradeService.java
Task T026: acceptProposal in TradeService.java
Task T027: TradeActionRequest DTO
Task T028–T032: use cases + controller + beans
```

---

## Parallel Example: User Story 4

```bash
# Client + component in parallel:
Task T058: apiClient.ts (acceptTrade, rejectTrade, getTrade)
Task T061: TradeActions.tsx

# Then page integration:
Task T059–T060: received/sent sections in TradesPage.tsx
Task T062–T064: CSS, handlers, conditional buttons
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL — blocks all stories)
3. Complete Phase 3: User Story 1 (aceitar troca)
4. **STOP and VALIDATE**: `TradeServiceTest` accept scenarios + `TradeControllerTest` accept endpoint green
5. Demo aceite via Swagger ou curl

### Incremental Delivery

1. Setup + Foundational → foundation ready
2. US1 → aceitar troca + atualizar coleções ✅
3. US2 → recusar troca ✅
4. US3 → validações completas + erros HTTP ✅
5. US4 → interface web com aceitar/recusar ✅
6. US5 → regressão MVP + quickstart + fluxo browser ✅

### Parallel Team Strategy

1. Team completes Setup + Foundational together
2. After Foundational:
   - Developer A: US1 + US2 (TradeService + endpoints)
   - Developer B: US3 validation tests (after US1 acceptProposal skeleton)
3. After API stable:
   - Developer C: US4 Frontend
4. All converge on US5 polish

---

## Notes

- Controllers MUST NOT contain business rules — only delegate to use cases
- `acceptProposal` MUST execute collection mutations (passos 8–9) only after validations (passos 1–7) pass
- Failed accept validation keeps status `PROPOSED` and does NOT mutate collections (FR-016)
- `album.trade.protect-single-sticker` defaults to `false` (compatível com MVP)
- Reject does NOT validate sticker ownership (FR-010)
- Do NOT implement Evolução 2 (suggestions, ranking, notifications, chat, cancel by requester)
- Commit after each phase checkpoint on branch `002-trade-accept-reject`
