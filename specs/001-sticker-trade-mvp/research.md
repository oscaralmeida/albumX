# Research: MVP de Troca de Figurinhas da Copa

**Feature**: `001-sticker-trade-mvp`  
**Date**: 2026-06-26

## R1 — Stack de linguagem e runtime (backend)

**Decision**: Java 21 com Spring Boot 3.4 como stack principal do backend.

**Rationale**:
- Alinha-se à constituição do projeto (Restrições Técnicas).
- Arquitetura em camadas didática (domínio / aplicação / infraestrutura) comum em treinamentos corporativos.
- Spring Boot oferece REST, injeção de dependências, persistência e testes com configuração mínima.
- Java 21 (LTS) traz records úteis para DTOs e value objects sem complexidade excessiva.
- Ecossistema JUnit 5 + Mockito + MockMvc cobre testes de domínio e integração de API.

**Alternatives considered**:
- **Python + FastAPI**: mais conciso, porém fora do padrão definido na constituição.
- **Node.js + TypeScript**: viável para full stack, mas backend Java já é requisito do projeto.

## R2 — Canal de entrega (interface web)

**Decision**: Aplicação **full stack** — API REST JSON + **frontend web** simples consumindo a API.

**Rationale**:
- A spec (US-5, FR-016, SC-006) e a constituição (Princípio VIII — Demonstrabilidade Visual) exigem demonstração via navegador.
- Participantes do treinamento devem executar o fluxo completo sem Postman ou `curl`.
- A API permanece o contrato estável; o frontend é camada de apresentação fina (sem regras de negócio).

**Alternatives considered**:
- **Apenas API REST**: viola requisitos de demonstrabilidade visual e escopo do prompt de plan.
- **Thymeleaf server-side**: reduz número de serviços, mas mistura apresentação com backend e dificulta evolução do frontend.

## R3 — Stack do frontend

**Decision**: **React 18 + Vite + TypeScript**, com fetch nativo para chamadas à API.

**Rationale**:
- React é amplamente conhecido em treinamentos; Vite oferece dev server rápido e build simples.
- TypeScript alinha tipos com o contrato OpenAPI.
- SPA leve com páginas/seções por fluxo do MVP (usuários, coleção, repetidas, trocas).
- Nginx no container de produção serve o build estático.

**Alternatives considered**:
- **Vue 3**: equivalente em simplicidade; React escolhido por maior familiaridade em treinamentos.
- **HTML + JavaScript vanilla**: menos estrutura para múltiplas telas; aceitável mas menos evolutivo.

## R4 — Persistência

**Decision**: Spring Data JPA com H2 em memória; schema via `ddl-auto=create-drop`.

**Rationale**:
- Zero configuração externa para treinamento.
- Adaptadores na infraestrutura implementam portas definidas no domínio.
- Evolução futura para PostgreSQL exige apenas troca de datasource.

**Alternatives considered**:
- **Repositórios em memória (HashMap)**: mais simples, mas não demonstra persistência real.
- **PostgreSQL desde o MVP**: dependência de infraestrutura desnecessária nesta fase.

## R5 — Modelagem do álbum e figurinhas

**Decision**: Álbum fixo configurável via `album.sticker-count` (padrão **700**). Figurinha identificada apenas pelo **número** (1..N); sem entidade `Sticker` persistida no MVP.

**Rationale**:
- Atende FR-006 e SC-003.
- Evita CRUD de catálogo fora do escopo.
- Campo `description` opcional do modelo sugerido fica reservado para evolução.

**Alternatives considered**:
- **Entidade Sticker persistida**: YAGNI no MVP.
- **N=670**: válido; 700 cobre a spec com margem.

## R6 — Onde vivem os serviços de domínio

**Decision**: `UserService`, `CollectionService` e `TradeService` na camada **domínio**, recebendo portas de repositório. Casos de uso na camada **aplicação** delegam aos serviços e convertem DTOs.

**Rationale**:
- Centraliza regras conforme constituição (Princípio IV).
- Controllers e componentes React não contêm validações de negócio.
- Serviços testáveis com repositórios mockados.

**Alternatives considered**:
- **Regras nos use cases**: duplicaria lógica na Evolução 1.
- **Regras nos controllers**: viola constituição.

## R7 — Identificadores

**Decision**: `UUID` para `User.id` e `TradeProposal.id`; figurinhas identificadas por `Integer number`.

**Rationale**:
- UUID evita colisão sem sequenciador global.
- Número da figurinha é a linguagem do domínio.

**Alternatives considered**:
- **Long auto-increment**: expõe ordem de cadastro desnecessariamente.

## R8 — Documentação interativa da API

**Decision**: **springdoc-openapi** (Swagger UI) em `/swagger-ui.html`, contrato mantido em `specs/001-sticker-trade-mvp/contracts/openapi.yaml`.

**Rationale**:
- Atende FR-018 e SC-008.
- Integração nativa com Spring Boot 3; UI acessível no navegador.
- Arquivo OpenAPI versionado serve como fonte de verdade do contrato.

**Alternatives considered**:
- **Swagger anotado apenas em código**: diverge do contrato versionado no Spec Kit.
- **Postman Collection**: complementar, não substitui requisito de doc interativa.

## R9 — Containerização e orquestração

**Decision**: `Dockerfile` para backend (multi-stage Maven + JRE 21), `Dockerfile` para frontend (multi-stage Node build + Nginx), `docker-compose.yml` na raiz orquestrando ambos.

**Rationale**:
- Atende FR-019, SC-007 e prompt de plan (`docker-compose up`).
- Um único comando sobe API, Swagger, frontend e H2 em memória.
- Variável de ambiente `VITE_API_URL` aponta o frontend para o backend no compose.

**Alternatives considered**:
- **Apenas scripts Maven/npm**: não atende requisito de containerização do prompt.
- **Monolito único**: mistura ciclos de build frontend/backend.

## R10 — Estratégia de testes

**Decision**:
- **Unitários** (JUnit 5 + Mockito): serviços de domínio — foco principal e obrigatório.
- **Integração** (`@SpringBootTest` + MockMvc): endpoints REST.
- **Frontend**: validação manual via browser no quickstart; sem E2E automatizado no MVP (YAGNI).

**Rationale**:
- Constituição exige testes de domínio (Princípio VII).
- MockMvc cobre contrato HTTP sem browser automation.
- Fluxo visual validado no quickstart atende SC-005 e SC-006.

**Alternatives considered**:
- **Playwright/Cypress**: útil em evoluções; complexidade desnecessária no MVP didático.
- **Apenas testes de integração**: menos precisos para isolar regras.

## R11 — Tratamento de erros

**Decision**: Exceções de domínio tipadas (`DomainException` subclasses) traduzidas para HTTP 400/404 por `@RestControllerAdvice`. Frontend exibe campo `message` da resposta.

**Rationale**:
- Mensagens compreensíveis (SC-002, FR-020).
- Domínio não conhece códigos HTTP.

**Alternatives considered**:
- **Optional/Result em todo lugar**: verboso demais para MVP didático.

## R12 — Controle de versão (Git)

**Decision**: Branch de implementação do MVP: **`feature/mvp-base`**. Artefatos Spec Kit permanecem em `specs/001-sticker-trade-mvp/`.

**Rationale**:
- Prompt de plan define branch dedicada para a fase MVP.
- Separa convenção de feature Spec Kit (`001-sticker-trade-mvp`) da branch de desenvolvimento.

**Alternatives considered**:
- **Usar apenas `001-sticker-trade-mvp` como branch**: válido no Spec Kit, mas diverge do prompt de treinamento.
