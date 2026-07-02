---
description: "Task list for feature implementation"
---

# Tasks: MVP de Troca de Figurinhas da Copa

**Input**: Design documents from `/specs/001-sticker-trade-mvp/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Testes unitários de domínio e testes de integração MockMvc são OBRIGATÓRIOS (Constituição AlbumX, Princípio VI; plan.md).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

## Path Conventions

- **Backend**: `src/main/java/com/albumx/` at repository root
- **Tests**: `src/test/java/com/albumx/`
- **Frontend**: `frontend/src/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Inicializar projeto Maven/Spring Boot, estrutura de pacotes por camada e base para testes

- [X] T001 Create Maven project with Spring Boot 3.4 parent, Java 21, and dependencies (Web, Data JPA, Validation, H2) in `pom.xml` and `src/main/java/com/albumx/AlbumXApplication.java`
- [X] T002 Create package structure per plan in `src/main/java/com/albumx/` (`domain/model`, `domain/service`, `domain/repository`, `domain/exception`, `application/usecase`, `application/dto`, `infrastructure/persistence`, `infrastructure/web`, `infrastructure/config`)
- [X] T003 [P] Configure `src/main/resources/application.yml` and `src/main/resources/application-dev.yml` with H2 in-memory datasource and `album.sticker-count: 700`
- [X] T004 [P] Create test package structure in `src/test/java/com/albumx/domain/service/` and `src/test/java/com/albumx/infrastructure/web/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Domínio compartilhado, exceções, validação de álbum e portas de repositório — **BLOQUEIA** todas as user stories

**⚠️ CRITICAL**: Nenhuma user story pode começar antes desta fase

- [X] T005 Create base `DomainException` in `src/main/java/com/albumx/domain/exception/DomainException.java`
- [X] T006 [P] Create domain exception subclasses in `src/main/java/com/albumx/domain/exception/` (`InvalidUserNameException`, `UserNotFoundException`, `InvalidStickerNumberException`, `StickerNotOwnedException`, `SelfTradeException`)
- [X] T007 Create `Album` domain validator in `src/main/java/com/albumx/domain/model/Album.java` (validate sticker number in [1, stickerCount])
- [X] T008 Create `AlbumProperties` with `@ConfigurationProperties(prefix = "album")` in `src/main/java/com/albumx/infrastructure/config/AlbumProperties.java`
- [X] T009 [P] Create repository port interfaces in `src/main/java/com/albumx/domain/repository/` (`UserRepository`, `CollectionRepository`, `TradeProposalRepository`)
- [X] T010 Create `GlobalExceptionHandler` in `src/main/java/com/albumx/infrastructure/web/GlobalExceptionHandler.java` mapping domain exceptions to HTTP 400/404 with `ErrorResponse` (code + message em português)

**Checkpoint**: Foundation ready — user story implementation can now begin

---

## Phase 3: User Story 1 — Cadastro de colecionador (Priority: P1) 🎯 MVP

**Goal**: Permitir cadastro e consulta de usuários com identificador único e nome validado

**Independent Test**: Cadastrar usuário via API e verificar retorno de UUID; buscar por ID; rejeitar nome vazio

### Tests for User Story 1 ⚠️

> **NOTE: Write domain unit tests FIRST, ensure they FAIL before implementation**

- [X] T011 [P] [US1] Create `UserServiceTest` in `src/test/java/com/albumx/domain/service/UserServiceTest.java` (cadastro com sucesso; rejeição de nome vazio)
- [X] T012 [P] [US1] Create `UserControllerTest` with MockMvc in `src/test/java/com/albumx/infrastructure/web/UserControllerTest.java`

### Implementation for User Story 1

- [X] T013 [P] [US1] Create `User` domain model in `src/main/java/com/albumx/domain/model/User.java` (id UUID, name String)
- [X] T014 [P] [US1] Create JPA entity `UserEntity` in `src/main/java/com/albumx/infrastructure/persistence/UserEntity.java` and `UserJpaRepository` in `src/main/java/com/albumx/infrastructure/persistence/UserJpaRepository.java`
- [X] T015 [US1] Implement `UserRepository` adapter in `src/main/java/com/albumx/infrastructure/persistence/UserRepositoryAdapter.java`
- [X] T016 [US1] Implement `UserService` in `src/main/java/com/albumx/domain/service/UserService.java` (register, findById; validar nome não vazio)
- [X] T017 [P] [US1] Create DTOs `CreateUserRequest` and `UserResponse` in `src/main/java/com/albumx/application/dto/`
- [X] T018 [US1] Create use cases `CreateUserUseCase` and `GetUserUseCase` in `src/main/java/com/albumx/application/usecase/`
- [X] T019 [US1] Implement `UserController` in `src/main/java/com/albumx/infrastructure/web/UserController.java` (`POST /api/users`, `GET /api/users/{id}`)

**Checkpoint**: User Story 1 fully functional — cadastro e consulta de usuários via API

---

## Phase 4: User Story 2 — Registro e visualização da coleção (Priority: P2)

**Goal**: Registrar figurinhas na coleção do usuário com controle de quantidade e listar coleção completa

**Independent Test**: Adicionar figurinhas a um usuário, incrementar quantidade, listar coleção; rejeitar número inválido e usuário inexistente

### Tests for User Story 2 ⚠️

- [X] T020 [P] [US2] Create `CollectionServiceTest` in `src/test/java/com/albumx/domain/service/CollectionServiceTest.java` (add sticker, increment quantity, reject invalid number, reject missing user)
- [X] T021 [P] [US2] Create `CollectionControllerTest` with MockMvc in `src/test/java/com/albumx/infrastructure/web/CollectionControllerTest.java`

### Implementation for User Story 2

- [X] T022 [P] [US2] Create `CollectionEntry` in `src/main/java/com/albumx/domain/model/CollectionEntry.java` and `UserCollection` in `src/main/java/com/albumx/domain/model/UserCollection.java` (addSticker, getQuantity, hasSticker)
- [X] T023 [P] [US2] Create JPA entities `CollectionEntryEntity` in `src/main/java/com/albumx/infrastructure/persistence/CollectionEntryEntity.java` and `CollectionJpaRepository` in `src/main/java/com/albumx/infrastructure/persistence/CollectionJpaRepository.java`
- [X] T024 [US2] Implement `CollectionRepository` adapter in `src/main/java/com/albumx/infrastructure/persistence/CollectionRepositoryAdapter.java`
- [X] T025 [US2] Implement `CollectionService` in `src/main/java/com/albumx/domain/service/CollectionService.java` (addSticker, getCollection; validar usuário existe, número válido via Album)
- [X] T026 [P] [US2] Create DTOs `AddStickerRequest`, `CollectionEntryResponse`, `CollectionResponse` in `src/main/java/com/albumx/application/dto/`
- [X] T027 [US2] Create use cases `AddStickerUseCase` and `GetCollectionUseCase` in `src/main/java/com/albumx/application/usecase/`
- [X] T028 [US2] Implement `CollectionController` in `src/main/java/com/albumx/infrastructure/web/CollectionController.java` (`POST /api/users/{id}/collection/stickers`, `GET /api/users/{id}/collection`)
- [X] T029 [US2] Ensure empty `UserCollection` is created when user is registered in `UserService.java` (via `CollectionRepository`)

**Checkpoint**: User Story 2 fully functional — adicionar e listar coleção

---

## Phase 5: User Story 3 — Consulta de figurinhas repetidas (Priority: P3)

**Goal**: Listar figurinhas com quantidade > 1 na coleção do usuário

**Independent Test**: Coleção com figurinhas em quantidades variadas retorna apenas as repetidas; lista vazia quando nenhuma repetida

### Tests for User Story 3 ⚠️

- [X] T030 [P] [US3] Add duplicate listing tests to `CollectionServiceTest.java` (returns only qty > 1; empty list when none)
- [X] T031 [P] [US3] Add `GET /api/users/{id}/collection/duplicates` test to `CollectionControllerTest.java`

### Implementation for User Story 3

- [X] T032 [US3] Implement `getDuplicates()` in `UserCollection.java` returning entries with quantity > 1
- [X] T033 [US3] Implement `getDuplicates(UUID userId)` in `CollectionService.java`
- [X] T034 [US3] Create `GetDuplicatesUseCase` in `src/main/java/com/albumx/application/usecase/GetDuplicatesUseCase.java`
- [X] T035 [US3] Add `GET /api/users/{userId}/collection/duplicates` endpoint to `CollectionController.java`

**Checkpoint**: User Story 3 fully functional — consulta de repetidas independente

---

## Phase 6: User Story 4 — Proposta de troca entre colecionadores (Priority: P4)

**Goal**: Criar e listar propostas de troca com status inicial PROPOSED; impedir propostas inválidas

**Independent Test**: Dois usuários com coleções; proposta válida retorna 201 com status PROPOSED; proposta sem figurinha oferecida ou self-trade retorna 400

### Tests for User Story 4 ⚠️

- [X] T036 [P] [US4] Create `TradeServiceTest` in `src/test/java/com/albumx/domain/service/TradeServiceTest.java` (proposta válida PROPOSED; rejeição sem figurinha; rejeição self-trade; rejeição usuário inexistente)
- [X] T037 [P] [US4] Create `TradeControllerTest` with MockMvc in `src/test/java/com/albumx/infrastructure/web/TradeControllerTest.java`

### Implementation for User Story 4

- [X] T038 [P] [US4] Create `TradeStatus` enum with `PROPOSED` (and reserved `ACCEPTED`/`REJECTED`) in `src/main/java/com/albumx/domain/model/TradeStatus.java`
- [X] T039 [P] [US4] Create `TradeProposal` domain model in `src/main/java/com/albumx/domain/model/TradeProposal.java`
- [X] T040 [P] [US4] Create JPA entity `TradeProposalEntity` and `TradeProposalJpaRepository` in `src/main/java/com/albumx/infrastructure/persistence/`
- [X] T041 [US4] Implement `TradeProposalRepository` adapter in `src/main/java/com/albumx/infrastructure/persistence/TradeProposalRepositoryAdapter.java`
- [X] T042 [US4] Implement `TradeService` in `src/main/java/com/albumx/domain/service/TradeService.java` (createProposal with PROPOSED status; validate requester owns offered sticker; validate requester ≠ target; validate users exist)
- [X] T043 [P] [US4] Create DTOs `CreateTradeRequest` and `TradeProposalResponse` in `src/main/java/com/albumx/application/dto/`
- [X] T044 [US4] Create `CreateTradeUseCase` and `ListTradesUseCase` in `src/main/java/com/albumx/application/usecase/`
- [X] T045 [US4] Implement `TradeController` in `src/main/java/com/albumx/infrastructure/web/TradeController.java` (`POST /api/trades`, `GET /api/trades` with optional query filters)

**Checkpoint**: User Story 4 fully functional — criar e listar propostas

---

## Phase 7: User Story 5 — Interface web didática (Priority: P5)

**Goal**: Interface React simples para cadastro, coleção, repetidas e propostas de troca via navegador

**Independent Test**: Acessar `http://localhost:3000` e executar fluxo completo (cadastro → coleção → repetidas → proposta) sem ferramentas externas

### Implementation for User Story 5

- [X] T046 [P] [US5] Initialize React 18 + Vite 6 + TypeScript project in `frontend/package.json`, `frontend/vite.config.ts`, and `frontend/tsconfig.json`
- [X] T047 [P] [US5] Create HTTP client aligned with OpenAPI in `frontend/src/api/apiClient.ts` (users, collection, duplicates, trades)
- [X] T048 [P] [US5] Create reusable form and list components in `frontend/src/components/` (`UserForm.tsx`, `StickerForm.tsx`, `CollectionList.tsx`, `TradeForm.tsx`, `ErrorMessage.tsx`)
- [X] T049 [US5] Create users page with cadastro and seleção de usuário ativo in `frontend/src/pages/UsersPage.tsx`
- [X] T050 [US5] Create collection page (adicionar figurinha + listar coleção) in `frontend/src/pages/CollectionPage.tsx`
- [X] T051 [US5] Create duplicates page in `frontend/src/pages/DuplicatesPage.tsx`
- [X] T052 [US5] Create trades page (propor troca + listar propostas) in `frontend/src/pages/TradesPage.tsx`
- [X] T053 [US5] Wire navigation and active-user context in `frontend/src/App.tsx` and `frontend/src/main.tsx`
- [X] T054 [US5] Implement API error display (mensagens em português) without breaking pages in `frontend/src/components/ErrorMessage.tsx`
- [X] T055 [US5] Create `CorsConfig` allowing frontend origin (`http://localhost:3000`, `http://localhost:5173`) in `src/main/java/com/albumx/infrastructure/config/CorsConfig.java`

**Checkpoint**: User Story 5 fully functional — fluxo completo via browser na interface web

---

## Phase 8: User Story 6 — Documentação interativa e execução simplificada (Priority: P6)

**Goal**: Swagger UI acessível no navegador; aplicação executável com `docker-compose up --build`

**Independent Test**: Subir com um comando; acessar Swagger em `/swagger-ui.html`; invocar cada endpoint do MVP; frontend em `http://localhost:3000`

### Implementation for User Story 6

- [X] T056 [P] [US6] Add `springdoc-openapi-starter-webmvc-ui` dependency to `pom.xml`
- [X] T057 [US6] Create `OpenApiConfig` in `src/main/java/com/albumx/infrastructure/config/OpenApiConfig.java` aligned with `specs/001-sticker-trade-mvp/contracts/openapi.yaml`
- [X] T058 [US6] Configure springdoc Swagger UI path in `src/main/resources/application.yml` (`springdoc.swagger-ui.path: /swagger-ui.html`)
- [X] T059 [P] [US6] Create backend multi-stage `Dockerfile` at repository root (Maven build + JRE 21 runtime)
- [X] T060 [P] [US6] Create `src/main/resources/application-docker.yml` with docker profile settings (H2, album config)
- [X] T061 [P] [US6] Create `frontend/Dockerfile` (Node build + Nginx) and `frontend/nginx.conf` (SPA fallback + optional API proxy)
- [X] T062 [US6] Create `docker-compose.yml` at repository root orchestrating `backend` (port 8080) and `frontend` (port 3000) with env vars `SPRING_PROFILES_ACTIVE=docker` and `VITE_API_URL=http://localhost:8080`

**Checkpoint**: User Story 6 fully functional — `docker-compose up --build` sobe backend + frontend + Swagger

---

## Phase 9: Polish & Cross-Cutting Concerns

**Purpose**: Revisão final de escopo MVP, validação end-to-end e preparação para Evolução 1

- [X] T063 [P] Create end-to-end flow test in `src/test/java/com/albumx/infrastructure/web/AlbumXFlowTest.java` (2 users → add stickers → duplicates → create trade → list trades)
- [X] T064 Verify `TradeStatus` enum reserves `ACCEPTED`/`REJECTED` but MVP only assigns `PROPOSED` in `src/main/java/com/albumx/domain/model/TradeStatus.java`
- [X] T065 Verify MVP does NOT implement accept/reject trade, collection transfer, or fair-trade validation across `TradeService.java` and controllers
- [X] T066 Verify business rules remain centralized in domain services (`UserService`, `CollectionService`, `TradeService`) — no logic in controllers or JPA entities
- [X] T067 Run `mvn test` and ensure all domain and MockMvc tests pass
- [X] T068 Run quickstart validation from `specs/001-sticker-trade-mvp/quickstart.md` (docker-compose up, browser flow < 5 min, Swagger invoca 7 endpoints)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: No dependencies — start immediately
- **Phase 2 (Foundational)**: Depends on Phase 1 — **BLOCKS** all user stories
- **Phase 3–6 (US1–US4)**: Depends on Phase 2; sequential P1 → P2 → P3 → P4 recommended
- **Phase 7 (US5 Frontend)**: Depends on US1–US4 API endpoints; T055 (CORS) before frontend integration test
- **Phase 8 (US6 Docker/Swagger)**: Depends on backend (Phases 1–6); frontend build (T046) before T061–T062
- **Phase 9 (Polish)**: Depends on all desired user stories complete

### User Story Dependencies

| Story | Depende de | Entrega independente |
|-------|-----------|----------------------|
| US1 (P1) | Phase 2 | Cadastro/consulta de usuários |
| US2 (P2) | Phase 2, US1 (usuário existe) | Adicionar/listar coleção |
| US3 (P3) | US2 (CollectionService) | Listar repetidas |
| US4 (P4) | US1, US2 (users + hasSticker) | Criar/listar propostas |
| US5 (P5) | US1–US4 (API REST) | Fluxo via browser |
| US6 (P6) | US1–US4; US5 para compose completo | Um comando + Swagger |

### Within Each User Story

1. Domain unit tests written first (must FAIL before implementation)
2. Domain models before services
3. Services before use cases
4. Use cases before controllers
5. Integration tests after controllers

### Parallel Opportunities

- **Phase 1**: T003, T004
- **Phase 2**: T006, T009
- **US1**: T011+T012 (tests); T013+T014+T017 (models/DTOs)
- **US2**: T020+T021; T022+T023+T026
- **US3**: T030+T031
- **US4**: T036+T037; T038+T039+T040+T043
- **US5**: T046+T047+T048 (scaffold + client + components in parallel)
- **US6**: T056+T059+T060+T061 (dependency, Dockerfiles in parallel)
- **Polish**: T063 can run after US4; T067–T068 after US6

---

## Parallel Example: User Story 5

```bash
# Scaffold frontend in parallel:
Task T046: frontend/package.json + vite.config.ts
Task T047: frontend/src/api/apiClient.ts
Task T048: frontend/src/components/*.tsx

# Pages after components exist:
Task T049: UsersPage.tsx
Task T050: CollectionPage.tsx
Task T051: DuplicatesPage.tsx
Task T052: TradesPage.tsx
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: `UserServiceTest` + `UserControllerTest` green
5. Demo cadastro de usuário via API

### Incremental Delivery (Full MVP)

1. Setup + Foundational → foundation ready
2. US1 → cadastro/consulta de usuários ✅
3. US2 → adicionar/listar coleção ✅
4. US3 → consultar repetidas ✅
5. US4 → criar/listar propostas ✅
6. US5 → interface web React
7. US6 → Swagger + Docker Compose
8. Phase 9 → revisão final e quickstart

### Parallel Team Strategy

1. Team completes Setup + Foundational together
2. After US4 API is ready:
   - Developer A: US5 Frontend (T046–T055)
   - Developer B: US6 Swagger + Docker (T056–T062)
3. Both converge on Phase 9 validation

---

## Notes

- Controllers MUST NOT contain business rules — only delegate to use cases
- Album sticker validation uses configured range `[1, 700]`
- MVP MUST NOT implement accept/reject trade, collection transfer, notifications, or fair-trade validation
- All domain service tests use Mockito mocks — no Spring context
- Commit after each phase checkpoint on branch `feature/mvp-base`
