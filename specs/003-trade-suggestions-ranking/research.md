# Research: Evolução 2 — Sugestão de Trocas e Ranking

**Feature**: `003-trade-suggestions-ranking`  
**Date**: 2026-07-02  
**Spec**: [spec.md](./spec.md)

## Decisões técnicas

### R1 — Sugestões calculadas sob demanda (sem persistência)

**Decision**: `TradeSuggestion` é um value object de domínio, não uma entidade JPA. Cada consulta recalcula oportunidades a partir do estado atual das coleções.

**Rationale**: Alinha com FR-009, FR-010 e FR-011; evita sincronização de sugestões obsoletas; mantém o modelo didático simples.

**Alternatives considered**:
- Cache em memória com invalidação — rejeitado por complexidade desnecessária em treinamento.
- Tabela `trade_suggestions` — rejeitado; fora de escopo e conflita com spec.

---

### R2 — Algoritmo de sugestão por comparação em pares

**Decision**: Para consulta por usuário A, iterar todos os outros usuários B; para cada par (A, B), cruzar repetidas oferecíveis de A com ausências de B e vice-versa; emitir sugestão para cada par válido (stickerA, stickerB) com `stickerA ≠ stickerB`.

**Rationale**: O(n² × d²) é aceitável para escala didática (poucos usuários, poucas repetidas); regra de benefício mútuo fica explícita no código.

**Critérios de oferta** (reutilizam config Evolução 1):
- `protectSingleSticker = true` → oferecer só se `qty ≥ 2`
- `protectSingleSticker = false` → oferecer se `qty ≥ 1` e parceiro não possui (`qty = 0`)

**Perspectiva do solicitante**: em `GET .../users/{userId}/trade-suggestions`, A é sempre o usuário consultado (`requesterUserId` na resposta = `userId` do path).

**Alternatives considered**:
- Índice invertido figurinha→usuários — rejeitado para fase didática; pode ser nota de evolução futura.
- Uma sugestão agregada por par de usuários — rejeitado; spec permite múltiplos pares de figurinhas entre os mesmos usuários.

---

### R3 — Ranking com desempate estável em três níveis

**Decision**: Ordenar por:
1. `albumCompletionPercentage` decrescente
2. `acceptedTradesCount` decrescente
3. `userId` ascendente (desempate estável, previsível em testes)

**Rationale**: Atende FR-016 e FR-017; critério terciário evita posições ambíguas sem gamificação extra.

**Alternatives considered**:
- Ordem alfabética por nome — rejeitado como primário; nome pode duplicar.
- Pontuação composta ponderada — fora de escopo (spec proíbe scoring complexo).

---

### R4 — Contagem de trocas aceitas via repositório

**Decision**: Adicionar `TradeProposalRepository.countAcceptedByUserId(UUID userId)` que conta propostas com `status = ACCEPTED` onde o usuário é solicitante **ou** destinatário.

**Rationale**: FR-018; consulta única por usuário no ranking; domínio não depende de JPA.

**Alternatives considered**:
- Filtrar `findAll(null, null)` em memória — rejeitado; não escala e acopla ranking a lista completa sem porta dedicada.
- Campo denormalizado em `User` — rejeitado; viola simplicidade e exige atualização no aceite.

---

### R5 — Extensões em CollectionService e UserRepository

**Decision**:
- `UserRepository.findAll()` — listar usuários para ranking e loop de pares.
- `CollectionService.getUniqueStickerCount(userId)` — figurinhas com qty ≥ 1.
- `CollectionService.getMissingStickerNumbers(userId)` — números do álbum ausentes na coleção.
- `CollectionService.canOfferSticker(userId, stickerNumber, protectSingleSticker)` — centraliza regra de oferta nas sugestões.

**Rationale**: Regras de coleção permanecem centralizadas (Princípio IV); serviços de sugestão/ranking orquestram sem duplicar lógica de quantidade.

---

### R6 — Endpoints REST

**Decision**:

| Método | Path | Descrição |
|--------|------|-----------|
| GET | `/api/users/{userId}/trade-suggestions` | Sugestões na perspectiva do usuário |
| GET | `/api/trade-suggestions` | Todas as oportunidades (opcional didático) |
| GET | `/api/ranking` | Ranking completo ordenado |

Versão OpenAPI: **1.2.0** (MVP + Evolução 1 + Evolução 2).

**Rationale**: Path por usuário segue padrão existente (`/api/users/{userId}/collection/...`); ranking global em recurso dedicado.

---

### R7 — Frontend: novas abas e ponte para proposta

**Decision**:
- Aba **Sugestões** (`SuggestionsPage`) — lista sugestões do usuário ativo; botão "Criar proposta" navega para aba Trocas com formulário pré-preenchido via estado compartilhado em `App.tsx` ou callback.
- Aba **Ranking** (`RankingPage`) — tabela ordenada com métricas.

**Rationale**: FR-021–FR-023; reutiliza `TradeForm` existente sem duplicar validação de criação.

**Alternatives considered**:
- Modal de criação na página de sugestões — rejeitado; duplicaria `TradeForm`.
- Rota React Router — rejeitado; app usa tabs simples sem router.

---

### R8 — Percentual de conclusão

**Decision**: `albumCompletionPercentage = (uniqueStickersCount / album.stickerCount) * 100`, arredondado para exibição com uma casa decimal no DTO; cálculo interno em `double` ou `BigDecimal` com comparação por valor numérico no sort.

**Rationale**: FR-015; `album.sticker-count` já configurado (default 700) em `AlbumProperties`.

---

## Riscos e mitigações

| Risco | Mitigação |
|-------|-----------|
| Sugestão desatualizada após mudança de coleção | Documentado; criação de proposta aplica validações atuais (spec edge case) |
| Performance com muitos usuários | Aceitável em treinamento; nota no plan para otimização futura |
| Regressão MVP/Evolução 1 | Testes existentes + `AlbumXFlowTest` estendido |

## NEEDS CLARIFICATION — resolvidos

Nenhum item pendente: stack, tamanho do álbum, regra de figurinha única e critérios de ranking estão definidos na spec e no código da Evolução 1.
