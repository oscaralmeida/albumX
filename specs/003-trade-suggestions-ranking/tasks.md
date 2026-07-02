---
description: "Task list for Evolução 2 — Sugestão de Trocas e Ranking de Usuários"
---

# Tasks: Evolução 2 — Sugestão de Trocas e Ranking de Usuários

**Input**: Design documents from `/specs/003-trade-suggestions-ranking/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Testes unitários de domínio e testes de integração MockMvc são OBRIGATÓRIOS (Constituição AlbumX, Princípio VII; spec.md e plan.md).

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

**Purpose**: Confirmar base MVP + Evolução 1 e configuração reutilizada — sem alterar comportamento existente

- [X] T001 Verify MVP (001-sticker-trade-mvp) and Evolução 1 (002-trade-accept-reject) are delivered; work on branch `003-trade-suggestions-ranking`
- [X] T002 [P] Confirm `album.sticker-count` and `album.trade.protect-single-sticker` (default `false`) in `src/main/resources/application.yml` and `src/main/resources/application-docker.yml`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Consultas read-only em coleções, usuários e trocas aceitas; value objects — **BLOQUEIA** todas as user stories

**⚠️ CRITICAL**: Nenhuma user story pode começar antes desta fase

### 1. Preparar modelo e consultas de coleção para sugestões e ranking

- [X] T003 Add `getUniqueStickerNumbers()` to `src/main/java/com/albumx/domain/model/UserCollection.java`
- [X] T004 Add `getMissingStickerNumbers(int albumSize)` to `src/main/java/com/albumx/domain/model/UserCollection.java`
- [X] T005 Implement `getUniqueStickerCount(UUID userId)` in `src/main/java/com/albumx/domain/service/CollectionService.java`
- [X] T006 Implement `getMissingStickerNumbers(UUID userId)` in `src/main/java/com/albumx/domain/service/CollectionService.java`
- [X] T007 Implement `canOfferSticker(UUID userId, int stickerNumber, boolean protectSingleSticker)` in `CollectionService.java` (qty ≥ 2 se proteção ativa; qty ≥ 1 caso contrário)
- [X] T008 [P] Add `getUniqueStickerCount` and `getMissingStickerNumbers` tests to `src/test/java/com/albumx/domain/service/CollectionServiceTest.java`
- [X] T009 [P] Add `canOfferSticker` tests (protectSingleSticker true/false) to `CollectionServiceTest.java`

### 2. Expandir repositórios (read-only)

- [X] T010 Add `findAll()` to `src/main/java/com/albumx/domain/repository/UserRepository.java`
- [X] T011 Implement `findAll()` in `src/main/java/com/albumx/infrastructure/persistence/UserRepositoryAdapter.java` (e `UserJpaRepository` se necessário)
- [X] T012 Add `countAcceptedByUserId(UUID userId)` to `src/main/java/com/albumx/domain/repository/TradeProposalRepository.java`
- [X] T013 Implement `countAcceptedByUserId` in `TradeProposalRepositoryAdapter.java` com query JPA (`status = ACCEPTED` AND requester OR target)

### 3. Value objects (conceitos derivados, não persistidos)

- [X] T014 [P] Create immutable `TradeSuggestion` value object (`userAId`, `userBId`, `stickerOfferedByUserA`, `stickerOfferedByUserB`, `reason`) in `src/main/java/com/albumx/domain/model/TradeSuggestion.java`
- [X] T015 [P] Create immutable `UserRankingPosition` value object (`userId`, `userName`, `uniqueStickersCount`, `albumCompletionPercentage`, `acceptedTradesCount`, `position`) in `src/main/java/com/albumx/domain/model/UserRankingPosition.java`

**Checkpoint**: Foundation ready — `mvn test` verde; endpoints MVP e Evolução 1 inalterados; nenhum endpoint novo ainda

---

## Phase 3: User Story 1 — Consultar sugestões automáticas de troca (Priority: P1) 🎯 MVP

**Goal**: Consultar sugestões de troca mútua para um usuário; retorno consultivo sem alterar coleções nem criar propostas

**Independent Test**: Dois usuários com repetidas complementares; `GET /api/users/{userId}/trade-suggestions` retorna par válido (A, B, figurinha oferecida, figurinha desejada); coleções e propostas inalteradas

### Tests for User Story 1 ⚠️

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [X] T016 [P] [US1] Create `TradeSuggestionServiceTest` with `shouldSuggestMutualBenefitBetweenTwoUsers` in `src/test/java/com/albumx/domain/service/TradeSuggestionServiceTest.java`
- [X] T017 [P] [US1] Add `shouldReturnEmptyWhenNoMutualBenefit` test to `TradeSuggestionServiceTest.java`
- [X] T018 [P] [US1] Add `shouldReturnEmptyWhenNoDuplicates` test to `TradeSuggestionServiceTest.java`
- [X] T019 [P] [US1] Add `shouldNotMutateCollectionsOrCreateProposals` test (verify zero saves) to `TradeSuggestionServiceTest.java`
- [X] T020 [P] [US1] Add `shouldThrowUserNotFoundWhenUserDoesNotExist` test to `TradeSuggestionServiceTest.java`

### Implementation for User Story 1

- [X] T021 [US1] Create `TradeSuggestionService` with `findSuggestionsForUser(UUID userAId)` — algoritmo em pares, perspectiva do usuário consultado — in `src/main/java/com/albumx/domain/service/TradeSuggestionService.java`
- [X] T022 [US1] Implement `offerableBy(owner, partner)` usando `canOfferSticker` + `partner.getQuantity(n) == 0` em `TradeSuggestionService.java`
- [X] T023 [US1] Populate `reason` didático em cada `TradeSuggestion` (ex.: "Troca mútua: A oferece #10, B oferece #25")
- [X] T024 [P] [US1] Create `TradeSuggestionResponse` DTO (`requesterUserId`, `partnerUserId`, `offeredStickerNumber`, `requestedStickerNumber`, `reason`) in `src/main/java/com/albumx/application/dto/TradeSuggestionResponse.java`
- [X] T025 [US1] Create `GetTradeSuggestionsUseCase` in `src/main/java/com/albumx/application/usecase/GetTradeSuggestionsUseCase.java`
- [X] T026 [US1] Create `TradeSuggestionController` with `GET /api/users/{userId}/trade-suggestions` in `src/main/java/com/albumx/infrastructure/web/TradeSuggestionController.java`
- [X] T027 [P] [US1] Create `GetAllTradeSuggestionsUseCase` (consulta global opcional) in `src/main/java/com/albumx/application/usecase/GetAllTradeSuggestionsUseCase.java`
- [X] T028 [US1] Add `GET /api/trade-suggestions` endpoint (global, opcional) in `TradeSuggestionController.java`
- [X] T029 [US1] Register `TradeSuggestionService` and use case beans in `src/main/java/com/albumx/infrastructure/config/DomainConfig.java`
- [X] T030 [US1] Create `TradeSuggestionControllerTest` — happy path 200 com lista e usuário inexistente 404 — in `src/test/java/com/albumx/infrastructure/web/TradeSuggestionControllerTest.java`

**Checkpoint**: User Story 1 fully functional — sugestões consultáveis via API; zero mutação de dados

---

## Phase 4: User Story 2 — Regras de negócio nas sugestões (Priority: P2)

**Goal**: Sugestões respeitam troca justa (1:1), benefício mútuo, proteção de figurinha única e ausência de auto-troca/duplicatas

**Independent Test**: Coleções que violam regras (sem repetida, figurinha única, mesmo número nos dois lados, auto-troca) não aparecem nas sugestões

### Tests for User Story 2 ⚠️

- [X] T031 [P] [US2] Add `shouldRejectSameStickerNumber` (troca injusta) test to `TradeSuggestionServiceTest.java`
- [X] T032 [P] [US2] Add `shouldRespectSingleStickerProtectionWhenEnabled` test to `TradeSuggestionServiceTest.java`
- [X] T033 [P] [US2] Add `shouldAllowOfferWhenProtectionDisabled` test to `TradeSuggestionServiceTest.java`
- [X] T034 [P] [US2] Add `shouldInvertPerspectiveWhenPartnerQueries` test to `TradeSuggestionServiceTest.java`
- [X] T035 [P] [US2] Add `shouldExcludeSelfTrade` test to `TradeSuggestionServiceTest.java`
- [X] T036 [P] [US2] Add `shouldNotDuplicateSameOpportunityInSingleQuery` test to `TradeSuggestionServiceTest.java`

### Implementation for User Story 2

- [X] T037 [US2] Enforce V-21..V-24 in pair iteration (`stickerX != stickerY`, skip self, `canOfferSticker` com `protectSingleSticker` de `AlbumProperties`) in `TradeSuggestionService.java`
- [X] T038 [US2] Implement `findAllSuggestions()` (global) reutilizando mesmas regras em `TradeSuggestionService.java`

**Checkpoint**: User Stories 1 AND 2 fully functional — todas as regras de sugestão validadas por testes de domínio

---

## Phase 5: User Story 4 — Consultar ranking de usuários (Priority: P4)

**Goal**: Ranking ordenado por percentual de conclusão do álbum (decrescente) com desempate por trocas aceitas; cálculo sob demanda sem alterar dados

**Independent Test**: Três usuários com coleções e histórico distintos; `GET /api/ranking` retorna posições corretas; empate em % desempata por trocas aceitas

### Tests for User Story 4 ⚠️

- [X] T039 [P] [US4] Create `RankingServiceTest` with `shouldOrderByCompletionPercentage` in `src/test/java/com/albumx/domain/service/RankingServiceTest.java`
- [X] T040 [P] [US4] Add `shouldTieBreakByAcceptedTrades` test to `RankingServiceTest.java`
- [X] T041 [P] [US4] Add `shouldReturnZeroPercentForEmptyCollection` test to `RankingServiceTest.java`
- [X] T042 [P] [US4] Add `shouldCountAcceptedTradesForBothParticipants` test to `RankingServiceTest.java`
- [X] T043 [P] [US4] Add `shouldReturnEmptyWhenNoUsers` test to `RankingServiceTest.java`
- [X] T044 [P] [US4] Add `shouldNotMutateDataOnRankingQuery` test (verify zero saves) to `RankingServiceTest.java`

### Implementation for User Story 4

- [X] T045 [US4] Create `RankingService` with `getRanking()` — pct = (unique / album.stickerCount) × 100; sort pct DESC, trades DESC, userId ASC; assign position 1..n — in `src/main/java/com/albumx/domain/service/RankingService.java`
- [X] T046 [P] [US4] Create `UserRankingResponse` DTO in `src/main/java/com/albumx/application/dto/UserRankingResponse.java`
- [X] T047 [US4] Create `GetRankingUseCase` in `src/main/java/com/albumx/application/usecase/GetRankingUseCase.java`
- [X] T048 [US4] Create `RankingController` with `GET /api/ranking` in `src/main/java/com/albumx/infrastructure/web/RankingController.java`
- [X] T049 [US4] Register `RankingService` and `GetRankingUseCase` beans in `DomainConfig.java`
- [X] T050 [US4] Create `RankingControllerTest` — ranking ordenado 200 — in `src/test/java/com/albumx/infrastructure/web/RankingControllerTest.java`

**Checkpoint**: User Story 4 fully functional — ranking consultável via API; independente das sugestões

---

## Phase 6: User Story 3 — Criar proposta a partir de sugestão na interface web (Priority: P3)

**Goal**: Botão na sugestão pré-preenche formulário de proposta e usa fluxo existente de criação (POST /api/trades)

**Independent Test**: Acionar "Criar proposta" em sugestão → aba Trocas com solicitante, destinatário, figurinha oferecida e desejada preenchidos; proposta criada com status PROPOSED

### Implementation for User Story 3

- [X] T051 [US3] Add `onCreateProposalFromSuggestion` callback and `initialTradeForm` state in `frontend/src/App.tsx`
- [X] T052 [US3] Pass `initialTradeForm` prop to `frontend/src/pages/TradesPage.tsx` and pre-fill `TradeForm` in `frontend/src/components/TradeForm.tsx`
- [X] T053 [US3] Add "Criar proposta" button per suggestion calling `onCreateProposalFromSuggestion` in `frontend/src/components/SuggestionList.tsx` (ou inline em `SuggestionsPage.tsx`)

**Checkpoint**: User Story 3 fully functional — ponte sugestão → proposta sem novo endpoint backend

---

## Phase 7: User Story 5 — Visualizar sugestões e ranking na interface web (Priority: P5)

**Goal**: Abas Sugestões e Ranking no SPA; integração com API; estados vazios e erros compreensíveis

**Independent Test**: Browser — selecionar usuário em Sugestões, ver lista; abrir Ranking, ver tabela ordenada; lista vazia e erros exibidos sem quebrar página

### Implementation for User Story 5

- [X] T054 [P] [US5] Add `getTradeSuggestions(userId)` and `getRanking()` to `frontend/src/api/apiClient.ts` aligned with `specs/003-trade-suggestions-ranking/contracts/openapi.yaml`
- [X] T055 [US5] Create `frontend/src/pages/SuggestionsPage.tsx` — seletor de usuário ativo, carrega sugestões via API
- [X] T056 [P] [US5] Create `frontend/src/components/SuggestionList.tsx` — exibe parceiro, figurinha oferecida e figurinha recebida
- [X] T057 [US5] Create `frontend/src/pages/RankingPage.tsx` — tabela com posição, nome, % conclusão, figurinhas únicas, trocas aceitas
- [X] T058 [US5] Add **Sugestões** and **Ranking** tabs/navigation in `frontend/src/App.tsx`
- [X] T059 [US5] Add empty states and `ErrorMessage` handling in `SuggestionsPage.tsx` and `RankingPage.tsx`

**Checkpoint**: User Story 5 fully functional — demonstração visual completa no browser

---

## Phase 8: User Story 6 — Compatibilidade com MVP e Evolução 1 (Priority: P6)

**Goal**: Zero regressão nos fluxos anteriores; documentação interativa atualizada; quickstart validado

**Independent Test**: Fluxo MVP + Evolução 1 (cadastro → coleção → repetidas → proposta → aceite/recusa) seguido de sugestões e ranking; comportamento preservado

### Implementation for User Story 6

- [X] T060 [P] [US6] Verify `specs/003-trade-suggestions-ranking/contracts/openapi.yaml` v1.2.0 documents `GET /api/users/{userId}/trade-suggestions`, `GET /api/trade-suggestions` and `GET /api/ranking`
- [X] T061 [US6] Extend E2E flow in `src/test/java/com/albumx/infrastructure/web/AlbumXFlowTest.java` — sugestões → criar proposta → aceite → ranking atualizado
- [X] T062 [US6] Run `mvn test` and confirm all MVP (001) and Evolução 1 (002) tests pass without assertion changes
- [X] T063 [P] [US6] Execute validation scenarios from `specs/003-trade-suggestions-ranking/quickstart.md` in browser
- [X] T064 [US6] Confirm GET sugestões/ranking are read-only — no collection or proposal mutations on consulta

**Checkpoint**: Evolução 2 entregue com compatibilidade total e documentação testável

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — can start immediately
- **Foundational (Phase 2)**: Depends on Setup — **BLOCKS** all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational — MVP da Evolução 2
- **User Story 2 (Phase 4)**: Depends on Phase 3 (`TradeSuggestionService` base)
- **User Story 4 (Phase 5)**: Depends on Foundational only — **can run in parallel with Phase 3/4** after Phase 2
- **User Story 3 (Phase 6)**: Depends on Phase 3 (API sugestões) and existing `TradeForm`
- **User Story 5 (Phase 7)**: Depends on Phase 3 (sugestões API) and Phase 5 (ranking API); incorporates Phase 6 button
- **User Story 6 (Phase 8)**: Depends on all prior phases

### User Story Dependencies

| Story | Priority | Depends on | Independently testable via |
|-------|----------|------------|----------------------------|
| US1 | P1 | Phase 2 | `GET .../trade-suggestions` + testes domínio |
| US2 | P2 | US1 | Testes de regras em `TradeSuggestionServiceTest` |
| US4 | P4 | Phase 2 | `GET /api/ranking` + `RankingServiceTest` |
| US3 | P3 | US1 + TradeForm | Browser: criar proposta a partir de sugestão |
| US5 | P5 | US1, US4, US3 | Browser: abas Sugestões e Ranking |
| US6 | P6 | All | `AlbumXFlowTest` + quickstart |

### Within Each User Story

- Tests MUST be written and FAIL before implementation
- Models/value objects before services
- Services before use cases and controllers
- API before frontend integration
- Story complete before moving to dependent story

### Parallel Opportunities

- **Phase 1**: T002 parallel with T001
- **Phase 2**: T008/T009 parallel; T014/T015 parallel; T010–T013 sequential per repo but User vs Trade repo parallelizable
- **Phase 3**: T016–T020 (all tests) parallel; T024/T027 parallel
- **Phase 4**: T031–T036 (all tests) parallel
- **Phase 5**: Entire phase parallel with Phase 3/4 after Phase 2 (ranking independent of suggestions)
- **Phase 7**: T054/T056 parallel
- **Phase 8**: T060/T063 parallel

---

## Parallel Example: User Story 1

```bash
# Launch all domain tests for User Story 1 together:
Task T016: "shouldSuggestMutualBenefitBetweenTwoUsers in TradeSuggestionServiceTest.java"
Task T017: "shouldReturnEmptyWhenNoMutualBenefit in TradeSuggestionServiceTest.java"
Task T018: "shouldReturnEmptyWhenNoDuplicates in TradeSuggestionServiceTest.java"
Task T019: "shouldNotMutateCollectionsOrCreateProposals in TradeSuggestionServiceTest.java"
Task T020: "shouldThrowUserNotFoundWhenUserDoesNotExist in TradeSuggestionServiceTest.java"

# Launch DTO while implementing service:
Task T024: "TradeSuggestionResponse.java"
```

## Parallel Example: Ranking vs Sugestões (after Phase 2)

```bash
# Developer A — User Story 1+2 (sugestões):
Phase 3 → Phase 4

# Developer B — User Story 4 (ranking), em paralelo:
Phase 5 (T039–T050)
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (**CRITICAL**)
3. Complete Phase 3: User Story 1 (sugestões básicas + endpoint)
4. **STOP and VALIDATE**: `mvn test` + `GET /api/users/{id}/trade-suggestions` via Swagger/curl
5. Demo sugestões consultivas sem frontend

### Incremental Delivery

1. Setup + Foundational → base read-only pronta
2. US1 → sugestões via API (**MVP Evolução 2**)
3. US2 → regras completas de sugestão
4. US4 → ranking via API (paralelo possível após Foundational)
5. US3 + US5 → frontend sugestões, ranking e ponte para proposta
6. US6 → regressão e quickstart

### Suggested Commit Boundaries

- `feat(domain): read-only collection queries and value objects`
- `feat(domain): trade suggestion service with business rules`
- `feat(domain): ranking service`
- `feat(web): suggestion and ranking endpoints`
- `feat(frontend): suggestions and ranking pages`

---

## Notes

- Sugestões e ranking são **calculados sob demanda** — nenhuma tabela nova
- `TradeSuggestion` perspectiva: `userAId` = usuário consultado (solicitante na oportunidade)
- Criação de proposta a partir de sugestão reutiliza `POST /api/trades` e validações MVP/Evolução 1
- Consulta global `GET /api/trade-suggestions` é opcional (didática); fluxo principal é por usuário
- Evitar: persistir sugestões, criar propostas automaticamente, alterar coleções em consultas GET
